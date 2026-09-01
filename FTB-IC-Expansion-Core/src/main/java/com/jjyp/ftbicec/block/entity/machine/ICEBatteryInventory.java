package com.jjyp.ftbicec.block.entity.machine;

import com.jjyp.ftbicec.block.entity.ICEElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ICEBatteryInventory extends ItemStackHandler {
    public final ICEElectricBlockEntity entity;
    public final boolean charge;

    public ICEBatteryInventory(ICEElectricBlockEntity e, boolean c) {
        super(1);
        this.entity = e;
        this.charge = c;
    }

    public void loadItem(ItemStack stack) {
        this.stacks.set(0, stack);
    }

    protected void onContentsChanged(int slot) {
        this.entity.setChanged();
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        label25: {
            if (stack.getItem() instanceof EnergyItemHandler) {
                if (this.charge) {
                    if (((EnergyItemHandler)stack.getItem()).canInsertEnergy()) {
                        break label25;
                    }
                } else if (((EnergyItemHandler)stack.getItem()).canExtractEnergy()) {
                    break label25;
                }
            }

            return false;
        }

        return true;
    }

    public int getSlotLimit(int slot) {
        return 1;
    }
}
