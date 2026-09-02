package com.jjyp.ftbicec.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.CachedEnergyStorage;
import dev.ftb.mods.ftbic.util.CachedEnergyStorageOrigin;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICCapabilities;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.ZapEnergyHandler;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleSupplier;

public abstract class ExpansionGeneratorBlockEntity extends BlockEntity implements ZapEnergyHandler {
    private final DoubleSupplier capacitySupplier;
    private final DoubleSupplier transferRateSupplier;

    protected double energy;
    private boolean activeThisTick;
    private long currentElectricNetwork = -1L;
    private CachedEnergyStorage[] connectedEnergyBlocks;
    private int[] validConsumerIndices;
    private BlockCapabilityCache<EnergyHandler, Direction>[] fePushCaches;
    private BlockCapabilityCache<ZapEnergyHandler, Direction>[] zapPushCaches;
    private final Map<Long, BlockCapabilityCache<EnergyHandler, Direction>> feFindCaches = new HashMap<>();
    private final Map<Long, BlockCapabilityCache<ZapEnergyHandler, Direction>> zapFindCaches = new HashMap<>();

    protected ExpansionGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                            DoubleSupplier capacitySupplier, DoubleSupplier transferRateSupplier) {
        super(type, pos, state);
        this.capacitySupplier = capacitySupplier;
        this.transferRateSupplier = transferRateSupplier;
    }

    public final void serverTick() {
        if (level == null || level.isClientSide()) {
            return;
        }

        double capacity = getEnergyCapacity();
        if (energy > capacity) {
            energy = capacity;
            setChanged();
        }

        handleGeneration();
        handleEnergyOutput();
        updateActiveState();
    }

    protected abstract void handleGeneration();

    protected final void markActive() {
        activeThisTick = true;
    }

    protected final double storedEnergy() {
        return energy;
    }

    protected final double addEnergy(double amount) {
        if (amount <= 0D) {
            return 0D;
        }
        double accepted = Math.min(amount, Math.max(0D, getEnergyCapacity() - energy));
        if (accepted > 0D) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }

    private void updateActiveState() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        if (state.hasProperty(ExpansionGeneratorBlock.ACTIVE)
            && state.getValue(ExpansionGeneratorBlock.ACTIVE) != activeThisTick) {
            level.setBlock(worldPosition, state.setValue(ExpansionGeneratorBlock.ACTIVE, activeThisTick), 3);
        }
        activeThisTick = false;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (energy > 0D) {
            output.putDouble("Energy", energy);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = Math.min(input.getDoubleOr("Energy", 0D), getEnergyCapacity());
    }

    @Override
    public double getEnergyCapacity() {
        return Math.max(1D, capacitySupplier.getAsDouble());
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergyRaw(double value) {
        energy = Math.max(0D, Math.min(value, getEnergyCapacity()));
    }

    @Override
    public void energyChanged(double previousEnergy) {
        setChanged();
    }

    @Override
    public double getMaxInputEnergy() {
        return 0D;
    }

    @Override
    public double getMaxOutputEnergy() {
        return Math.max(1D, transferRateSupplier.getAsDouble());
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        return false;
    }

    public InteractionResult interactWithItem(Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public InteractionResult interactWithoutItem(Player player, BlockHitResult hit) {
        if (level != null && !level.isClientSide()) {
            player.sendSystemMessage(Component.literal(String.format("Energy: %.0f / %.0f Zaps", energy, getEnergyCapacity())));
        }
        return InteractionResult.SUCCESS;
    }

    private void handleEnergyOutput() {
        pushFEToNeighbours();
        if (energy <= 0D) {
            return;
        }

        double transferable = Math.min(energy, getMaxOutputEnergy());
        if (transferable <= 0D) {
            return;
        }

        CachedEnergyStorage[] blocks = getConnectedEnergyBlocks();
        if (blocks.length == 0) {
            return;
        }
        if (validConsumerIndices == null || validConsumerIndices.length < blocks.length) {
            validConsumerIndices = new int[blocks.length];
        }

        int validBlocks = 0;
        for (int i = 0; i < blocks.length; i++) {
            CachedEnergyStorage storage = blocks[i];
            if (storage.isInvalid()) {
                ElectricBlockEntity.electricNetworkUpdated(level, storage.blockEntity.getBlockPos());
            } else if (storage.shouldReceiveEnergy()) {
                validConsumerIndices[validBlocks++] = i;
            }
        }

        if (validBlocks == 0) {
            return;
        }

        double share = transferable / validBlocks;
        boolean changed = false;
        for (int vi = 0; vi < validBlocks; vi++) {
            CachedEnergyStorage storage = blocks[validConsumerIndices[vi]];
            double thisShare = share;
            if (storage.feHandlerCache != null) {
                thisShare = Math.min(thisShare, storage.origin.cableTransferRate);
            } else if (storage.origin.cableTransferRate < share) {
                burnCableNetwork(storage.origin.cablePos, storage.origin.cableTier);
                storage.origin.cableBurnt = true;
                continue;
            }

            double accepted = storage.insertZaps(Math.min(thisShare, energy));
            if (accepted > 0D) {
                energy -= accepted;
                markActive();
                changed = true;
            }
            if (energy < share) {
                break;
            }
        }

        if (changed) {
            setChanged();
        }
    }

    private void pushFEToNeighbours() {
        if (energy <= 0D || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (Direction direction : FTBICUtils.DIRECTIONS) {
            if (zapPushCache(serverLevel, direction).getCapability() != null) {
                continue;
            }
            EnergyHandler fe = fePushCache(serverLevel, direction).getCapability();
            if (fe == null) {
                continue;
            }
            double zapsAvailable = Math.min(energy, getMaxOutputEnergy());
            int feToOffer = ZapFEConversion.zapsToFEFloor(zapsAvailable);
            if (feToOffer <= 0) {
                continue;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                int accepted = fe.insert(feToOffer, transaction);
                if (accepted > 0) {
                    energy -= Math.min(ZapFEConversion.feToZapsCeil(accepted), energy);
                    transaction.commit();
                    markActive();
                    setChanged();
                }
            }
            if (energy <= 0D) {
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private BlockCapabilityCache<EnergyHandler, Direction> fePushCache(ServerLevel level, Direction direction) {
        if (fePushCaches == null) {
            fePushCaches = new BlockCapabilityCache[FTBICUtils.DIRECTIONS.length];
        }
        BlockCapabilityCache<EnergyHandler, Direction> cache = fePushCaches[direction.ordinal()];
        if (cache == null) {
            cache = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, level,
                worldPosition.relative(direction), direction.getOpposite());
            fePushCaches[direction.ordinal()] = cache;
        }
        return cache;
    }

    @SuppressWarnings("unchecked")
    private BlockCapabilityCache<ZapEnergyHandler, Direction> zapPushCache(ServerLevel level, Direction direction) {
        if (zapPushCaches == null) {
            zapPushCaches = new BlockCapabilityCache[FTBICUtils.DIRECTIONS.length];
        }
        BlockCapabilityCache<ZapEnergyHandler, Direction> cache = zapPushCaches[direction.ordinal()];
        if (cache == null) {
            cache = BlockCapabilityCache.create(FTBICCapabilities.ZAP_ENERGY_BLOCK, level,
                worldPosition.relative(direction), direction.getOpposite());
            zapPushCaches[direction.ordinal()] = cache;
        }
        return cache;
    }

    private CachedEnergyStorage[] getConnectedEnergyBlocks() {
        if (level == null || level.isClientSide()) {
            return CachedEnergyStorage.EMPTY;
        }

        long networkId = ElectricBlockEntity.getCurrentElectricNetwork(level, worldPosition);
        if (connectedEnergyBlocks != null && currentElectricNetwork == networkId) {
            return connectedEnergyBlocks;
        }

        Set<CachedEnergyStorage> set = new HashSet<>();
        LongOpenHashSet traversed = new LongOpenHashSet();
        traversed.add(worldPosition.asLong());
        int maxCableLength = FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get();

        for (Direction direction : FTBICUtils.DIRECTIONS) {
            CachedEnergyStorageOrigin origin = new CachedEnergyStorageOrigin();
            origin.direction = direction;
            find(traversed, set, origin, 0, maxCableLength, worldPosition, direction);
        }

        connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
        currentElectricNetwork = networkId;
        return connectedEnergyBlocks;
    }

    private void find(LongOpenHashSet traversed, Set<CachedEnergyStorage> set, CachedEnergyStorageOrigin origin,
                      int distance, int maxCableLength, BlockPos currentPos, Direction direction) {
        if (level == null || distance > maxCableLength) {
            return;
        }

        BlockPos pos = currentPos.relative(direction);
        if (!traversed.add(pos.asLong())) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CableBlock cableBlock) {
            double rate = cableBlock.tier.transferRate();
            if (rate < origin.cableTransferRate) {
                origin.cableTier = cableBlock.tier;
                origin.cableTransferRate = rate;
                origin.cablePos = pos;
            }
            for (Direction next : FTBICUtils.DIRECTIONS) {
                if (state.getValue(CableBlock.CONNECTION[next.get3DDataValue()])) {
                    find(traversed, set, origin, distance + 1, maxCableLength, pos, next);
                }
            }
            return;
        }

        if (state.getBlock() instanceof NuclearReactorChamberBlock) {
            for (Direction next : FTBICUtils.DIRECTIONS) {
                find(traversed, set, origin, distance + 1, maxCableLength, pos, next);
            }
            return;
        }

        if (!state.hasBlockEntity() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null) {
            return;
        }

        long key = pos.asLong() ^ ((long) direction.ordinal() << 56);
        BlockCapabilityCache<ZapEnergyHandler, Direction> zapCache = zapFindCaches.get(key);
        if (zapCache == null) {
            zapCache = BlockCapabilityCache.create(FTBICCapabilities.ZAP_ENERGY_BLOCK, serverLevel, pos, direction.getOpposite());
            zapFindCaches.put(key, zapCache);
        }
        ZapEnergyHandler zapHandler = zapCache.getCapability();
        if (zapHandler != null && zapHandler != this && zapHandler.getMaxInputEnergy() > 0D
            && !zapHandler.isBurnt() && zapHandler.isValidEnergyInputSide(direction.getOpposite())) {
            CachedEnergyStorage storage = new CachedEnergyStorage();
            storage.origin = origin;
            storage.distance = distance;
            storage.blockEntity = entity;
            storage.energyHandler = zapHandler;
            set.add(storage);
            return;
        }

        BlockCapabilityCache<EnergyHandler, Direction> feCache = feFindCaches.get(key);
        if (feCache == null) {
            feCache = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, serverLevel, pos, direction.getOpposite());
            feFindCaches.put(key, feCache);
        }
        if (feCache.getCapability() != null) {
            CachedEnergyStorage storage = new CachedEnergyStorage();
            storage.origin = origin;
            storage.distance = distance;
            storage.blockEntity = entity;
            storage.feHandlerCache = feCache;
            set.add(storage);
        }
    }

    private void burnCableNetwork(BlockPos startPos, EnergyTier tier) {
        if (level == null || startPos == null || tier == null) {
            return;
        }
        LongOpenHashSet visited = new LongOpenHashSet();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        visited.add(startPos.asLong());
        queue.add(startPos);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof CableBlock cable) || cable.tier != tier) {
                continue;
            }
            level.setBlock(pos, cable.getBurntState(state), 3);
            level.levelEvent(1502, pos, 0);
            for (Direction direction : FTBICUtils.DIRECTIONS) {
                if (!state.getValue(CableBlock.CONNECTION[direction.get3DDataValue()])) {
                    continue;
                }
                BlockPos next = pos.relative(direction);
                if (visited.add(next.asLong())) {
                    queue.add(next);
                }
            }
        }
        ElectricBlockEntity.electricNetworkUpdated(level, startPos);
    }
}
