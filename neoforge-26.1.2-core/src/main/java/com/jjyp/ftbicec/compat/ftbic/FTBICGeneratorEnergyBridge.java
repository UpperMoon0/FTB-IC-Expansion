package com.jjyp.ftbicec.compat.ftbic;

import com.jjyp.ftbicec.machine.ExpansionGeneratorBlockEntity;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Version-specific compatibility boundary for FTB Industrial Contraptions 26.1.2 energy transport.
 *
 * <p>FTBIC currently exposes the Zap capability but not an addon-safe API for a generator to push
 * through its cable graph. This bridge mirrors only that transport behavior. Generator inventory,
 * fuel, fluid, persistence, balance and UI state must stay outside this class. When upstream exposes
 * a public transport API, this class should collapse to that API rather than accumulating more copied
 * internals.</p>
 */
public final class FTBICGeneratorEnergyBridge {
    private final ExpansionGeneratorBlockEntity generator;
    private long currentElectricNetwork = -1L;
    private CachedEnergyStorage[] connectedEnergyBlocks;
    private int[] validConsumerIndices;
    private BlockCapabilityCache<EnergyHandler, Direction>[] fePushCaches;
    private BlockCapabilityCache<ZapEnergyHandler, Direction>[] zapPushCaches;
    private final Map<Long, BlockCapabilityCache<EnergyHandler, Direction>> feFindCaches = new HashMap<>();
    private final Map<Long, BlockCapabilityCache<ZapEnergyHandler, Direction>> zapFindCaches = new HashMap<>();

    public FTBICGeneratorEnergyBridge(ExpansionGeneratorBlockEntity generator) {
        this.generator = generator;
    }

    /**
     * Mirrors the first stage of FTBIC generator output: direct adjacent FE consumers get priority.
     */
    public void pushAdjacentFE() {
        pushFEToNeighbours();
    }

    /**
     * Mirrors the final stage of FTBIC generator output after battery charging: distribute through
     * the Zap/cable network.
     */
    public void pushCableNetwork() {
        if (generator.getEnergy() <= 0D) {
            return;
        }

        double transferable = Math.min(generator.getEnergy(), generator.getMaxOutputEnergy());
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
        Level level = generator.getLevel();
        for (int i = 0; i < blocks.length; i++) {
            CachedEnergyStorage storage = blocks[i];
            if (storage.isInvalid()) {
                if (level != null) {
                    ElectricBlockEntity.electricNetworkUpdated(level, storage.blockEntity.getBlockPos());
                }
            } else if (storage.shouldReceiveEnergy()) {
                validConsumerIndices[validBlocks++] = i;
            }
        }

        if (validBlocks == 0) {
            return;
        }

        double share = transferable / validBlocks;
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

            double accepted = storage.insertZaps(Math.min(thisShare, generator.getEnergy()));
            if (accepted > 0D) {
                generator.consumeOutputEnergy(accepted);
            }
            if (generator.getEnergy() < share) {
                break;
            }
        }
    }

    private void pushFEToNeighbours() {
        if (generator.getEnergy() <= 0D || !(generator.getLevel() instanceof ServerLevel serverLevel)) {
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
            double zapsAvailable = Math.min(generator.getEnergy(), generator.getMaxOutputEnergy());
            int feToOffer = ZapFEConversion.zapsToFEFloor(zapsAvailable);
            if (feToOffer <= 0) {
                continue;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                int accepted = fe.insert(feToOffer, transaction);
                if (accepted > 0) {
                    transaction.commit();
                    generator.consumeOutputEnergy(ZapFEConversion.feToZapsCeil(accepted));
                }
            }
            if (generator.getEnergy() <= 0D) {
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
                generator.getBlockPos().relative(direction), direction.getOpposite());
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
                generator.getBlockPos().relative(direction), direction.getOpposite());
            zapPushCaches[direction.ordinal()] = cache;
        }
        return cache;
    }

    private CachedEnergyStorage[] getConnectedEnergyBlocks() {
        Level level = generator.getLevel();
        if (level == null || level.isClientSide()) {
            return CachedEnergyStorage.EMPTY;
        }

        BlockPos generatorPos = generator.getBlockPos();
        long networkId = ElectricBlockEntity.getCurrentElectricNetwork(level, generatorPos);
        if (connectedEnergyBlocks != null && currentElectricNetwork == networkId) {
            return connectedEnergyBlocks;
        }

        Set<CachedEnergyStorage> set = new HashSet<>();
        LongOpenHashSet traversed = new LongOpenHashSet();
        traversed.add(generatorPos.asLong());
        int maxCableLength = FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get();

        for (Direction direction : FTBICUtils.DIRECTIONS) {
            CachedEnergyStorageOrigin origin = new CachedEnergyStorageOrigin();
            origin.direction = direction;
            find(traversed, set, origin, 0, maxCableLength, generatorPos, direction);
        }

        connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
        currentElectricNetwork = networkId;
        return connectedEnergyBlocks;
    }

    private void find(LongOpenHashSet traversed, Set<CachedEnergyStorage> set, CachedEnergyStorageOrigin origin,
                      int distance, int maxCableLength, BlockPos currentPos, Direction direction) {
        Level level = generator.getLevel();
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
        if (zapHandler != null && zapHandler != generator) {
            if (zapHandler.getMaxInputEnergy() > 0D
                && !zapHandler.isBurnt()
                && zapHandler.isValidEnergyInputSide(direction.getOpposite())) {
                CachedEnergyStorage storage = new CachedEnergyStorage();
                storage.origin = origin;
                storage.distance = distance;
                storage.blockEntity = entity;
                storage.energyHandler = zapHandler;
                set.add(storage);
            }
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
        Level level = generator.getLevel();
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
