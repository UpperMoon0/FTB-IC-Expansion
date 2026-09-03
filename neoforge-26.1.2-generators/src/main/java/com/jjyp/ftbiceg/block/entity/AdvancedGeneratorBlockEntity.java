package com.jjyp.ftbiceg.block.entity;

import com.jjyp.ftbicec.machine.ExpansionGeneratorBlockEntity;
import com.jjyp.ftbiceg.ICEGConfig;
import com.jjyp.ftbiceg.registry.ICEGRegistries;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;

public final class AdvancedGeneratorBlockEntity extends ExpansionGeneratorBlockEntity {
    private ItemStack fuel = ItemStack.EMPTY;
    private int fuelTicks;
    private int maxFuelTicks;

    public AdvancedGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(
            ICEGRegistries.ADVANCED_GENERATOR_BE.get(),
            pos,
            state,
            () -> ICEGConfig.MACHINES.ADVANCED_GENERATOR_CAPACITY.get(),
            () -> FTBICConfig.ENERGY.LV_TRANSFER_RATE.get()
        );
    }

    @Override
    protected void handleGeneration() {
        if (fuelTicks > 0) {
            fuelTicks--;
            addEnergy(ICEGConfig.MACHINES.ADVANCED_GENERATOR_OUTPUT.get());
            markActive();
            if (fuelTicks == 0) {
                setChanged();
            }
        }

        if (fuelTicks == 0 && storedEnergy() < getEnergyCapacity() && !fuel.isEmpty()) {
            int baseFuelTicks = getFuelTicksFor(fuel);
            if (baseFuelTicks > 0) {
                double baseOutput = Math.max(0.1D, FTBICConfig.MACHINES.BASIC_GENERATOR_OUTPUT.get());
                double output = Math.max(0.1D, ICEGConfig.MACHINES.ADVANCED_GENERATOR_OUTPUT.get());
                maxFuelTicks = Math.max(1, (int) Math.ceil(baseFuelTicks / (output / baseOutput)));
                fuelTicks = maxFuelTicks;
                consumeOneFuelItem();
                markActive();
                setChanged();
            }
        }
    }

    public boolean isFuel(ItemStack stack) {
        return getFuelTicksFor(stack) > 0;
    }

    private int getFuelTicksFor(ItemStack stack) {
        if (stack.isEmpty() || !(level instanceof ServerLevel server)) {
            return 0;
        }
        @SuppressWarnings("unchecked")
        RecipeType<BasicGeneratorFuelRecipe> type =
            (RecipeType<BasicGeneratorFuelRecipe>) (RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get();
        for (RecipeHolder<BasicGeneratorFuelRecipe> holder : server.recipeAccess().recipeMap().byType(type)) {
            if (holder.value().ingredient().test(stack)) {
                return holder.value().ticks();
            }
        }
        return 0;
    }

    private void consumeOneFuelItem() {
        @SuppressWarnings("deprecation")
        ItemStackTemplate template = fuel.getItem().getCraftingRemainder();
        ItemStack remainder = template == null ? ItemStack.EMPTY : template.create();
        if (fuel.getCount() == 1) {
            fuel = remainder.isEmpty() ? ItemStack.EMPTY : remainder;
        } else {
            fuel.shrink(1);
            if (!remainder.isEmpty() && level != null) {
                Block.popResource(level, worldPosition, remainder);
            }
        }
    }

    @Override
    public InteractionResult interactWithItem(Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
        if (!isFuel(stack)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (level == null || level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!fuel.isEmpty() && !ItemStack.isSameItemSameComponents(fuel, stack)) {
            return InteractionResult.PASS;
        }
        int maxStack = stack.getMaxStackSize();
        if (!fuel.isEmpty() && fuel.getCount() >= maxStack) {
            return InteractionResult.PASS;
        }
        if (fuel.isEmpty()) {
            fuel = stack.copyWithCount(1);
        } else {
            fuel.grow(1);
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        setChanged();
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactWithoutItem(Player player, BlockHitResult hit) {
        if (level != null && !level.isClientSide() && player.isShiftKeyDown() && !fuel.isEmpty()) {
            ItemStack extracted = fuel.copy();
            fuel = ItemStack.EMPTY;
            if (!player.addItem(extracted)) {
                Block.popResource(level, worldPosition, extracted);
            }
            setChanged();
            return InteractionResult.SUCCESS;
        }
        return super.interactWithoutItem(player, hit);
    }

    public ItemStack getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack stack) {
        fuel = stack;
    }

    public int getFuelTicks() {
        return fuelTicks;
    }

    public int getMaxFuelTicks() {
        return maxFuelTicks;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!fuel.isEmpty()) {
            output.store("Fuel", ItemStack.CODEC, fuel);
        }
        if (fuelTicks > 0) {
            output.putInt("FuelTicks", fuelTicks);
            output.putInt("MaxFuelTicks", maxFuelTicks);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fuel = input.read("Fuel", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        fuelTicks = input.getIntOr("FuelTicks", 0);
        maxFuelTicks = input.getIntOr("MaxFuelTicks", 0);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null && !fuel.isEmpty()) {
            Block.popResource(level, pos, fuel);
            fuel = ItemStack.EMPTY;
        }
    }
}
