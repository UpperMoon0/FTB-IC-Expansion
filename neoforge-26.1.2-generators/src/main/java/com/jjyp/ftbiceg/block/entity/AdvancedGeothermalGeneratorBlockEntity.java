package com.jjyp.ftbiceg.block.entity;

import com.jjyp.ftbicec.machine.ExpansionGeneratorBlockEntity;
import com.jjyp.ftbiceg.ICEGConfig;
import com.jjyp.ftbiceg.registry.ICEGRegistries;
import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;

public final class AdvancedGeothermalGeneratorBlockEntity extends ExpansionGeneratorBlockEntity {
    private int fluidAmount;

    public AdvancedGeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(
            ICEGRegistries.ADVANCED_GEOTHERMAL_GENERATOR_BE.get(),
            pos,
            state,
            () -> ICEGConfig.MACHINES.ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY.get(),
            () -> FTBICConfig.ENERGY.MV_TRANSFER_RATE.get()
        );
    }

    @Override
    protected void handleGeneration() {
        if (fluidAmount <= 0 || storedEnergy() >= getEnergyCapacity()) {
            return;
        }

        double output = ICEGConfig.MACHINES.ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT.get();
        double produced = Math.min(output, getEnergyCapacity() - storedEnergy());
        if (produced <= 0D) {
            return;
        }

        int fluidCost = Math.max(1, (int) Math.ceil(produced / 20D));
        fluidCost = Math.min(fluidCost, fluidAmount);
        double scaledProduction = Math.min(produced, fluidCost * 20D);
        if (addEnergy(scaledProduction) > 0D) {
            fluidAmount -= fluidCost;
            markActive();
            setChanged();
        }
    }

    @Override
    public InteractionResult interactWithItem(Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
        if (!stack.is(Items.LAVA_BUCKET)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (fluidAmount + 1000 > getTankCapacity()) {
            return InteractionResult.PASS;
        }
        if (level == null || level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        fluidAmount += 1000;
        if (!player.isCreative()) {
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        }
        level.playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1F, 1F);
        setChanged();
        return InteractionResult.SUCCESS;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public void setFluidAmount(int amount) {
        fluidAmount = Math.max(0, Math.min(amount, getTankCapacity()));
    }

    public int getTankCapacity() {
        return ICEGConfig.MACHINES.ADVANCED_GEOTHERMAL_GENERATOR_TANK_SIZE.get();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (fluidAmount > 0) {
            output.putInt("FluidAmount", fluidAmount);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setFluidAmount(input.getIntOr("FluidAmount", 0));
    }
}
