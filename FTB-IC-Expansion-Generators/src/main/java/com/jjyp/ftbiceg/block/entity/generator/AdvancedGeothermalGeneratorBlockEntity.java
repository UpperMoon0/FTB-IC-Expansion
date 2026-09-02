package com.jjyp.ftbiceg.block.entity.generator;

import com.jjyp.ftbiceg.screen.AdvancedGeothermalGeneratorMenu;
import com.jjyp.ftbiceg.block.entity.ICEGElectricBlocks;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedGeothermalGeneratorBlockEntity extends MVGeneratorBlockEntity {
    public int fluidAmount = 0;
    private LazyOptional<AdvancedGeothermalGeneratorTank> tankOptional;

    public AdvancedGeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ICEGElectricBlocks.ADVANCED_GEOTHERMAL_GENERATOR, pos, state);
    }

    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        tag.putInt("FluidAmount", this.fluidAmount);
    }

    public void readData(CompoundTag tag) {
        super.readData(tag);
        this.fluidAmount = tag.getInt("FluidAmount");
    }

    public LazyOptional<?> getTankOptional() {
        if (this.tankOptional == null) {
            this.tankOptional = LazyOptional.of(() -> new AdvancedGeothermalGeneratorTank(this));
        }
        return this.tankOptional;
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        if (this.tankOptional != null) {
            this.tankOptional.invalidate();
            this.tankOptional = null;
        }
    }

    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.FLUID_HANDLER ? this.getTankOptional().cast() : super.getCapability(cap, side);
    }

    public void handleGeneration() {
        if (this.energy >= this.energyCapacity || this.fluidAmount <= 0) {
            return;
        }

        double requested = Math.min(this.maxEnergyOutput, this.energyCapacity - this.energy);
        int fluidCost = Math.max(1, (int) Math.ceil(requested / 20.0D));
        fluidCost = Math.min(fluidCost, this.fluidAmount);
        double produced = Math.min(requested, fluidCost * 20.0D);
        if (produced > 0.0D) {
            this.energy += produced;
            this.fluidAmount -= fluidCost;
            this.active = true;
            this.setChanged();
        }
    }

    public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!FluidUtil.interactWithFluidHandler(player, hand, (IFluidHandler) this.getTankOptional().orElse(null))) {
            if (!this.level.isClientSide()) {
                this.openMenu((ServerPlayer) player, (id, inventory) -> new AdvancedGeothermalGeneratorMenu(id, inventory, this));
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void addSyncData(SyncedData data) {
        super.addSyncData(data);
        data.addShort(SyncedData.BAR, () -> this.fluidAmount);
    }
}
