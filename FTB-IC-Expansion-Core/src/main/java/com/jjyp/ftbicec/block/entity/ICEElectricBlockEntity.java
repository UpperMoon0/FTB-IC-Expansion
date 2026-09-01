package com.jjyp.ftbicec.block.entity;

import com.jjyp.ftbicec.block.ICEElectricBlock;
import com.jjyp.ftbicec.block.ICEElectricBlockInstance;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.OpenMenuFactory;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ICEElectricBlockEntity extends BlockEntity implements EnergyHandler, IItemHandlerModifiable {
    private static final AtomicLong ELECTRIC_NETWORK_CHANGES = new AtomicLong(0L);
    public final ICEElectricBlockInstance electricBlockInstance;
    private boolean changed;
    public double energy;
    public final ItemStack[] inputItems;
    public final ItemStack[] outputItems;
    private LazyOptional<?> thisOptional;
    public boolean active;
    private int changeStateTicks;
    private boolean burnt;
    public double energyCapacity;
    public double maxInputEnergy;
    public boolean autoEject;
    public UUID placerId;
    public String placerName;

    public static void electricNetworkUpdated(LevelAccessor level, BlockPos pos) {
        ELECTRIC_NETWORK_CHANGES.incrementAndGet();
    }

    public static long getCurrentElectricNetwork(LevelAccessor level, BlockPos pos) {
        return ELECTRIC_NETWORK_CHANGES.get();
    }

    public ICEElectricBlockEntity(ICEElectricBlockInstance type, BlockPos pos, BlockState state) {
        super((BlockEntityType)type.blockEntity.get(), pos, state);
        this.placerId = Util.NIL_UUID;
        this.placerName = "";
        this.electricBlockInstance = type;
        this.changed = false;
        this.energy = 0.0;
        this.inputItems = new ItemStack[type.inputItemCount];
        this.outputItems = new ItemStack[type.outputItemCount];
        Arrays.fill(this.inputItems, ItemStack.EMPTY);
        Arrays.fill(this.outputItems, ItemStack.EMPTY);
        if (this.inputItems.length + this.outputItems.length > 127) {
            Registry var10002 = Registry.BLOCK_ENTITY_TYPE;
            throw new RuntimeException("Internal inventory of " + var10002.getKey(this.getType()) + " too large!");
        } else {
            this.thisOptional = null;
            this.active = false;
            this.changeStateTicks = 0;
            this.burnt = false;
        }
    }

    public void writeData(CompoundTag tag) {
        tag.putDouble("Energy", this.energy);
        if (this.inputItems.length + this.outputItems.length > 0) {
            ListTag inv = new ListTag();

            for(int slot = 0; slot < this.inputItems.length + this.outputItems.length; ++slot) {
                ItemStack stack = this.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    CompoundTag tag1 = stack.serializeNBT();
                    tag1.putByte("Slot", (byte)slot);
                    inv.add(tag1);
                }
            }

            tag.put("Inventory", inv);
        }

        if (this.burnt) {
            tag.putBoolean("Burnt", true);
        }

        if (!this.placerId.equals(Util.NIL_UUID)) {
            tag.putUUID("PlacerId", this.placerId);
            tag.putString("PlacerName", this.placerName);
        }

    }

    public void readData(CompoundTag tag) {
        this.energy = tag.getDouble("Energy");
        if (this.inputItems.length + this.outputItems.length > 0) {
            Arrays.fill(this.inputItems, ItemStack.EMPTY);
            Arrays.fill(this.outputItems, ItemStack.EMPTY);
            ListTag inv = tag.getList("Inventory", 10);

            for(int i = 0; i < inv.size(); ++i) {
                CompoundTag tag1 = inv.getCompound(i);
                this.setStackInSlot(tag1.getByte("Slot"), ItemStack.of(tag1));
            }
        }

        this.burnt = tag.getBoolean("Burnt");
        if (tag.hasUUID("PlacerId")) {
            this.placerId = tag.getUUID("PlacerId");
            this.placerName = tag.getString("PlacerName");
        } else {
            this.placerId = Util.NIL_UUID;
            this.placerName = "";
        }

    }

    public void writeNetData(CompoundTag tag) {
        tag.putBoolean("Burnt", this.burnt);
    }

    public void readNetData(CompoundTag tag) {
        if (tag != null) {
            this.burnt = tag.getBoolean("Burnt");
        }

    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.readData(tag);
        this.initProperties();
        this.upgradesChanged();
    }

    protected void saveAdditional(CompoundTag arg) {
        super.saveAdditional(arg);
        this.writeData(arg);
    }

    public void handleUpdateTag(CompoundTag tag) {
        this.readNetData(tag);
        this.initProperties();
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.writeNetData(tag);
        return tag;
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.readNetData(pkt.getTag());
        this.initProperties();
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onLoad() {
        this.initProperties();
        if (this.level != null && !this.level.isClientSide()) {
            this.upgradesChanged();
        }

        super.onLoad();
    }

    public LazyOptional<?> getThisOptional() {
        if (this.thisOptional == null) {
            this.thisOptional = LazyOptional.of(() -> {
                return this;
            });
        }

        return this.thisOptional;
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        if (this.thisOptional != null) {
            this.thisOptional.invalidate();
            this.thisOptional = null;
        }

    }

    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER && this.inputItems.length + this.outputItems.length > 0 ? this.getThisOptional().cast() : super.getCapability(cap, side);
    }

    protected void handleChanges() {
        if (this.changeStateTicks > 0) {
            --this.changeStateTicks;
        }

        if (this.changeStateTicks <= 0) {
            if (!this.isBurnt()) {
                if (this.level != null && this.electricBlockInstance.canBeActive && this.getBlockState().getBlock() instanceof ICEElectricBlock && (Boolean)this.getBlockState().getValue(ICEElectricBlock.ACTIVE) != this.active && !this.level.isClientSide()) {
                    this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue(ICEElectricBlock.ACTIVE, this.active), 3);
                    this.setChanged();
                }

                this.active = false;
            }

            if (this.level != null && !this.getBlockState().isAir()) {
                this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
            }

            this.changeStateTicks = (Integer)FTBICConfig.MACHINES.STATE_UPDATE_TICKS.get();
            if (this.changed) {
                this.setChangedNow();
            }
        }

    }

    public void tick() {
        this.handleChanges();
    }

    public void setChanged() {
        this.changed = true;
    }

    public void setChangedNow() {
        this.changed = false;
        this.level.blockEntityChanged(this.worldPosition);
    }

    public int getRedstoneOutputSignalEnergyStorage() {
        return Math.round((float)(this.energy / this.energyCapacity * 15.0));
    }

    public final double getEnergyCapacity() {
        return this.energyCapacity;
    }

    public final double getEnergy() {
        return this.energy;
    }

    public final void setEnergyRaw(double e) {
        this.energy = e;
    }

    public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.SUCCESS;
    }

    public void openMenu(ServerPlayer player, final OpenMenuFactory openMenuFactory) {
        NetworkHooks.openScreen(player, new MenuProvider() {
            public Component getDisplayName() {
                return ICEElectricBlockEntity.this.createDisplayName();
            }

            public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player1) {
                return openMenuFactory.create(id, playerInv);
            }
        }, (buf) -> {
            this.writeMenu(player, buf);
        });
    }

    public Component createDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    public void writeMenu(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }

    public boolean isEnergyHandlerInvalid() {
        return this.isBurnt() || this.isRemoved();
    }

    public final double getMaxInputEnergy() {
        return this.maxInputEnergy;
    }

    public @Nullable RecipeCache getRecipeCache() {
        return this.level == null ? null : RecipeCache.get(this.level);
    }

    public int getSlots() {
        return this.inputItems.length + this.outputItems.length;
    }

    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot >= 0 && slot < this.getSlots()) {
            return slot >= this.inputItems.length ? this.outputItems[slot - this.inputItems.length] : this.inputItems[slot];
        } else {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
        }
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.getSlots()) {
            ItemStack prev;
            if (slot >= this.inputItems.length) {
                prev = this.outputItems[slot - this.inputItems.length];
                this.outputItems[slot - this.inputItems.length] = stack;
                this.inventoryChanged(slot, prev);
            } else {
                prev = this.inputItems[slot];
                this.inputItems[slot] = stack;
                this.inventoryChanged(slot, prev);
            }

        } else {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
        }
    }

    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot < this.inputItems.length && !stack.isEmpty() && this.isItemValid(slot, stack)) {
            ItemStack existing = this.inputItems[slot];
            int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    ItemStack prev;
                    if (existing.isEmpty()) {
                        prev = this.inputItems[slot];
                        this.inputItems[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
                        this.inventoryChanged(slot, prev);
                    } else {
                        prev = existing.copy();
                        existing.grow(reachedLimit ? limit : stack.getCount());
                        this.inventoryChanged(slot, prev);
                    }
                }

                return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }
        } else {
            return stack;
        }
    }

    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot >= this.inputItems.length && amount > 0) {
            slot -= this.inputItems.length;
            ItemStack existing = this.outputItems[slot];
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        this.outputItems[slot] = ItemStack.EMPTY;
                        this.inventoryChanged(slot, existing);
                        return existing;
                    } else {
                        return existing.copy();
                    }
                } else {
                    if (!simulate) {
                        this.outputItems[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract);
                        this.inventoryChanged(slot, existing);
                    }

                    return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
                }
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public void inventoryChanged(int slot, @Nullable ItemStack prev) {
        this.setChanged();
    }

    public void energyChanged(int prev) {
        if (this.energy == 0.0 || prev == 0 || this.energy == this.energyCapacity) {
            this.setChanged();
        }

    }

    public int getSlotLimit(int slot) {
        return 64;
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot < this.inputItems.length;
    }

    public ItemStack addOutputInSlot(int slot, ItemStack stack) {
        if (this.outputItems[slot].isEmpty()) {
            this.outputItems[slot] = stack;
            return ItemStack.EMPTY;
        } else {
            ItemStack existing = this.outputItems[slot];
            int limit = stack.getMaxStackSize();
            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (existing.isEmpty()) {
                    this.outputItems[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }

                this.inventoryChanged(slot, existing);
                return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    public ItemStack addOutput(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            int i;
            for(i = 0; i < this.outputItems.length; ++i) {
                if (this.outputItems[i].getItem() == stack.getItem()) {
                    stack = this.addOutputInSlot(i, stack);
                    if (stack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            for(i = 0; i < this.outputItems.length; ++i) {
                if (this.outputItems[i].isEmpty()) {
                    stack = this.addOutputInSlot(i, stack);
                    if (stack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            return stack;
        }
    }

    public Direction[] getEjectDirections() {
        if (this.electricBlockInstance.facingProperty != BlockStateProperties.HORIZONTAL_FACING) {
            return Direction.values();
        } else {
            Direction rot = (Direction)this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction[] values = new Direction[]{Direction.DOWN, rot.getCounterClockWise(), rot.getOpposite(), rot.getClockWise(), rot, Direction.UP};
            return values;
        }
    }

    public void shiftInputs() {
        if (this.inputItems.length > 1) {
            List<ItemStack> stacks = new ArrayList();

            for(int i = 0; i < this.inputItems.length; ++i) {
                if (!this.inputItems[i].isEmpty()) {
                    stacks.add(this.inputItems[i]);
                    this.inputItems[i] = ItemStack.EMPTY;
                }
            }

            Iterator var4 = stacks.iterator();

            while(var4.hasNext()) {
                ItemStack stack = (ItemStack)var4.next();
                ItemHandlerHelper.insertItemStacked(this, stack, false);
            }

        }
    }

    public void ejectOutputItems() {
        if (this.autoEject) {
            Direction[] directions = null;

            for(int i = 0; i < this.outputItems.length; ++i) {
                if (!this.outputItems[i].isEmpty()) {
                    Direction[] var3 = directions == null ? (directions = this.getEjectDirections()) : directions;
                    int var4 = var3.length;

                    for(int var5 = 0; var5 < var4; ++var5) {
                        Direction direction = var3[var5];
                        BlockEntity entity = this.level.getBlockEntity(this.worldPosition.relative(direction));
                        IItemHandler itemHandler = entity == null ? null : (IItemHandler)entity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).orElse((IItemHandler) null);
                        if (itemHandler != null) {
                            this.outputItems[i] = ItemHandlerHelper.insertItemStacked(itemHandler, this.outputItems[i].copy(), false);
                            if (this.outputItems[i].isEmpty()) {
                                this.outputItems[i] = ItemStack.EMPTY;
                                break;
                            }
                        }
                    }
                }
            }

        }
    }

    public void onBroken(Level level, BlockPos pos) {
        ItemStack[] var3 = this.inputItems;
        int var4 = var3.length;

        int var5;
        ItemStack stack;
        for(var5 = 0; var5 < var4; ++var5) {
            stack = var3[var5];
            Block.popResource(level, pos, stack);
        }

        var3 = this.outputItems;
        var4 = var3.length;

        for(var5 = 0; var5 < var4; ++var5) {
            stack = var3[var5];
            Block.popResource(level, pos, stack);
        }

    }

    public void initProperties() {
        this.energyCapacity = (Double)this.electricBlockInstance.energyCapacity.get();
        this.maxInputEnergy = (Double)this.electricBlockInstance.maxEnergyInput.get();
        this.autoEject = false;
    }

    public void upgradesChanged() {
    }

    public double getTotalPossibleEnergyCapacity() {
        return (Double)this.electricBlockInstance.energyCapacity.get();
    }

    public void addSyncData(SyncedData data) {
        data.addDouble(SyncedData.ENERGY, () -> {
            return this.energy;
        });
        data.addDouble(SyncedData.ENERGY_CAPACITY, () -> {
            return this.energyCapacity;
        });
    }

    public final boolean canBurn() {
        return this.electricBlockInstance.canBurn;
    }

    public final void setBurnt(boolean b) {
        if (this.burnt != b && !this.level.isClientSide() && this.canBurn()) {
            this.burnt = b;
            this.setChanged();
            this.syncBlock();
            electricNetworkUpdated(this.level, this.worldPosition);
            if (this.burnt) {
                this.level.levelEvent(1502, this.worldPosition, 0);
                if (this.electricBlockInstance.canBeActive) {
                    this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue(ICEElectricBlock.ACTIVE, false), 3);
                }
            }
        }

    }

    public final boolean isBurnt() {
        return this.burnt;
    }

    public void stepOn(ServerPlayer player) {
    }

    @OnlyIn(Dist.CLIENT)
    public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, RandomSource r) {
    }

    public Direction getFacing(Direction def) {
        if (this.electricBlockInstance.facingProperty == null) {
            return def;
        } else {
            BlockState state = this.getBlockState();
            return state.getBlock() instanceof ICEElectricBlock ? (Direction)state.getValue(this.electricBlockInstance.facingProperty) : def;
        }
    }

    public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
        if (this.savePlacer()) {
            if (entity != null) {
                this.placerId = entity.getUUID();
                this.placerName = entity.getScoreboardName();
            } else if (!this.level.isClientSide()) {
                this.level.removeBlock(this.worldPosition, false);
            }
        }

    }

    public boolean savePlacer() {
        return false;
    }

    public void syncBlock() {
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 11);
        this.setChanged();
    }

    public void neighborChanged(BlockPos pos1, Block block1) {
        if (!this.level.getBlockState(pos1).is(block1)) {
            electricNetworkUpdated(this.level, pos1);
        }

    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos pos, BlockState state, T entity) {
        ((ICEElectricBlockEntity)entity).tick();
    }
}
