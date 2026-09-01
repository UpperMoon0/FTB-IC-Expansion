package com.jjyp.ftbiceg.block.entity.generator;

import com.jjyp.ftbiceg.block.entity.ICEGElectricBlocks;
import com.jjyp.ftbiceg.screen.AdvancedGeneratorMenu;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.screen.sync.SyncedDataKey;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class AdvancedGeneratorBlockEntity extends LVGeneratorBlockEntity {
    public static final SyncedDataKey<Integer> FUEL_BAR = new SyncedDataKey("fuel_ticks", 0);
    public int fuelTicks = 0;
    public int maxFuelTicks = 0;

    public AdvancedGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ICEGElectricBlocks.ADVANCED_GENERATOR, pos, state);
    }

    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        tag.putInt("FuelTicks", this.fuelTicks);
        tag.putInt("MaxFuelTicks", this.maxFuelTicks);
    }

    public void readData(CompoundTag tag) {
        super.readData(tag);
        this.fuelTicks = tag.getInt("FuelTicks");
        this.maxFuelTicks = tag.getInt("MaxFuelTicks");
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot != 0) {
            return false;
        } else {
            RecipeCache recipeCache = this.getRecipeCache();
            return recipeCache != null && recipeCache.getBasicGeneratorFuelTicks(this.level, stack) > 0;
        }
    }

    public void handleGeneration() {
        if (this.fuelTicks > 0) {
            --this.fuelTicks;
            if (this.energy < this.energyCapacity) {
                this.energy += Math.min(this.energyCapacity - this.energy, this.maxEnergyOutput);
            }

            if (this.fuelTicks == 0) {
                this.setChanged();
            }
        }

        if (this.fuelTicks == 0 && this.energy < this.energyCapacity && !this.inputItems[0].isEmpty()) {
            RecipeCache recipeCache = this.getRecipeCache();
            if (recipeCache != null) {
                this.maxFuelTicks = Math.round((float) recipeCache.getBasicGeneratorFuelTicks(this.level, this.inputItems[0]) / ((int) this.maxEnergyOutput / 10));
                this.fuelTicks = this.maxFuelTicks;
                if (this.maxFuelTicks > 0) {
                    if (this.inputItems[0].getCount() == 1) {
                        this.inputItems[0] = this.inputItems[0].getCraftingRemainingItem();
                    } else {
                        this.inputItems[0].shrink(1);
                    }

                    this.active = true;
                    this.setChanged();
                }
            }
        }

    }

    public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!this.level.isClientSide()) {
            this.openMenu((ServerPlayer)player, (id, inventory) -> new AdvancedGeneratorMenu(id, inventory, this));
        }

        return InteractionResult.SUCCESS;
    }

    public void addSyncData(SyncedData data) {
        super.addSyncData(data);
        data.addShort(SyncedData.BAR, () -> this.fuelTicks == 0 ? 0 : Mth.clamp(Mth.ceil((double)this.fuelTicks * 14.0 / (double)this.maxFuelTicks), 0, 14));
    }
}