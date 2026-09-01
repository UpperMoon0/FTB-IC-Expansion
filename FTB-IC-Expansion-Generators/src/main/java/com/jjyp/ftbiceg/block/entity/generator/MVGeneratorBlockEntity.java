package com.jjyp.ftbiceg.block.entity.generator;

import com.jjyp.ftbicec.block.ICEElectricBlockInstance;
import com.jjyp.ftbicec.block.entity.ICEElectricBlockEntity;
import com.jjyp.ftbicec.block.entity.machine.ICEBatteryInventory;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BurntCableBlock;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashSet;
import java.util.Set;

public class MVGeneratorBlockEntity extends ICEElectricBlockEntity {
    private long currentElectricNetwork = -1L;
    private CachedEnergyStorage[] connectedEnergyBlocks;
    public final ICEBatteryInventory chargeBatteryInventory = new ICEBatteryInventory(this, true);
    public double maxEnergyOutput;
    public double maxEnergyOutputTransfer;

    public MVGeneratorBlockEntity(ICEElectricBlockInstance type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void initProperties() {
        super.initProperties();
        this.maxEnergyOutput = (Double)this.electricBlockInstance.maxEnergyOutput.get();
        this.maxEnergyOutputTransfer = (Double) FTBICConfig.ENERGY.MV_TRANSFER_RATE.get();
    }

    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        if (!this.chargeBatteryInventory.getStackInSlot(0).isEmpty()) {
            tag.put("ChargeBattery", this.chargeBatteryInventory.getStackInSlot(0).serializeNBT());
        }

    }

    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("ChargeBattery")) {
            this.chargeBatteryInventory.loadItem(ItemStack.of(tag.getCompound("ChargeBattery")));
        } else {
            this.chargeBatteryInventory.loadItem(ItemStack.EMPTY);
        }

    }

    public void onBroken(Level level, BlockPos pos) {
        super.onBroken(level, pos);
        Block.popResource(level, pos, this.chargeBatteryInventory.getStackInSlot(0));
    }

    public void handleEnergyOutput() {
        if (!this.level.isClientSide()) {
            double e;
            if (this.energy > 0.0) {
                ItemStack battery = this.chargeBatteryInventory.getStackInSlot(0);
                if (!battery.isEmpty()) {
                    Item var3 = battery.getItem();
                    if (var3 instanceof EnergyItemHandler) {
                        EnergyItemHandler item = (EnergyItemHandler)var3;
                        double transfer = item.isCreativeEnergyItem() ? Double.POSITIVE_INFINITY : this.maxEnergyOutputTransfer * (Double)FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
                        e = item.insertEnergy(battery, Math.min(this.energy, transfer), false);
                        if (e > 0.0) {
                            this.energy -= e;
                            this.active = true;
                            this.setChanged();
                        }
                    }
                }
            }

            double tenergy = Math.min(this.energy, this.maxEnergyOutputTransfer);
            if (!(tenergy <= 0.0)) {
                CachedEnergyStorage[] blocks = this.getConnectedEnergyBlocks();
                int validBlocks = 0;
                CachedEnergyStorage[] var16 = blocks;
                int var6 = blocks.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    CachedEnergyStorage storage = var16[var7];
                    if (storage.isInvalid()) {
                        electricNetworkUpdated(this.level, storage.blockEntity.getBlockPos());
                    } else if (storage.shouldReceiveEnergy()) {
                        ++validBlocks;
                    }
                }

                if (validBlocks > 0) {
                    e = tenergy / (double)validBlocks;
                    CachedEnergyStorage[] var17 = blocks;
                    int var18 = blocks.length;

                    for(int var9 = 0; var9 < var18; ++var9) {
                        CachedEnergyStorage storage = var17[var9];
                        if (!storage.isInvalid() && storage.shouldReceiveEnergy()) {
                            if (storage.origin.cableTier != null && (Double)storage.origin.cableTier.transferRate.get() < e) {
                                this.level.setBlock(storage.origin.cablePos, BurntCableBlock.getBurntCable(this.level.getBlockState(storage.origin.cablePos)), 3);
                                this.level.levelEvent(1502, storage.origin.cablePos, 0);
                                storage.origin.cableBurnt = true;
                            } else {
                                double a = storage.energyHandler.insertEnergy(Math.min(e, this.energy), false);
                                if (a > 0.0) {
                                    this.energy -= a;
                                    this.active = true;
                                    this.setChanged();
                                }

                                if (this.energy < e) {
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    public void handleGeneration() {
    }

    public void tick() {
        if (!this.level.isClientSide()) {
            this.handleGeneration();
        }

        this.handleEnergyOutput();
        this.handleChanges();
    }

    public boolean isValidEnergyOutputSide(Direction direction) {
        return true;
    }

    public boolean isValidEnergyInputSide(Direction direction) {
        return false;
    }

    public CachedEnergyStorage[] getConnectedEnergyBlocks() {
        if (this.level != null && !this.level.isClientSide()) {
            long currentId = getCurrentElectricNetwork(this.level, this.getBlockPos());
            if (this.connectedEnergyBlocks == null || this.currentElectricNetwork == -1L || this.currentElectricNetwork != currentId) {
                Set<CachedEnergyStorage> set = new HashSet();
                Set<BlockPos> traversed = new HashSet();
                traversed.add(this.worldPosition);
                Direction[] var5 = FTBICUtils.DIRECTIONS;
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    Direction direction = var5[var7];
                    if (this.isValidEnergyOutputSide(direction)) {
                        CachedEnergyStorageOrigin origin = new CachedEnergyStorageOrigin();
                        origin.direction = direction;
                        this.find(traversed, set, origin, 0, this.worldPosition, direction);
                    }
                }

                this.connectedEnergyBlocks = (CachedEnergyStorage[])set.toArray(CachedEnergyStorage.EMPTY);
                this.currentElectricNetwork = currentId;
            }

            return this.connectedEnergyBlocks;
        } else {
            return CachedEnergyStorage.EMPTY;
        }
    }

    private void find(Set<BlockPos> traversed, Set<CachedEnergyStorage> set, CachedEnergyStorageOrigin origin, int distance, BlockPos currentPos, Direction direction) {
        if (this.level != null && distance <= (Integer)FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get()) {
            BlockPos pos = currentPos.relative(direction);
            if (traversed.add(pos)) {
                BlockState state = this.level.getBlockState(pos);
                Block var10 = state.getBlock();
                if (var10 instanceof CableBlock) {
                    CableBlock cableBlock = (CableBlock)var10;
                    if (origin.cableTier == null || (Double)cableBlock.tier.transferRate.get() < (Double)origin.cableTier.transferRate.get()) {
                        origin.cableTier = cableBlock.tier;
                        origin.cablePos = pos;
                    }

                    Direction[] var15 = FTBICUtils.DIRECTIONS;
                    int var11 = var15.length;

                    for(int var12 = 0; var12 < var11; ++var12) {
                        Direction dir = var15[var12];
                        if ((Boolean)state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
                            this.find(traversed, set, origin, distance + 1, pos, dir);
                        }
                    }
                } else if (state.hasBlockEntity()) {
                    BlockEntity entity = this.level.getBlockEntity(pos);
                    EnergyHandler handler = entity instanceof EnergyHandler ? (EnergyHandler)entity : null;
                    if (handler != null) {
                        if (handler != this && handler.getMaxInputEnergy() > 0.0 && !handler.isBurnt() && handler.isValidEnergyInputSide(direction.getOpposite())) {
                            CachedEnergyStorage s = new CachedEnergyStorage();
                            s.origin = origin;
                            s.distance = distance;
                            s.blockEntity = entity;
                            s.energyHandler = handler;
                            set.add(s);
                        }
                    } else if ((Double)FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get() > 0.0) {
                        if (entity == null) {
                            return;
                        }

                        LazyOptional<IEnergyStorage> energyCap = entity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
                        IEnergyStorage feStorage = (IEnergyStorage)energyCap.orElse((IEnergyStorage) null);
                        if (feStorage != null && feStorage.canReceive()) {
                            CachedEnergyStorage s = new CachedEnergyStorage();
                            s.origin = origin;
                            s.distance = distance;
                            s.blockEntity = entity;
                            s.energyHandler = new ForgeEnergyHandler(energyCap, feStorage);
                            set.add(s);
                        }
                    }
                }

            }
        }
    }
}
