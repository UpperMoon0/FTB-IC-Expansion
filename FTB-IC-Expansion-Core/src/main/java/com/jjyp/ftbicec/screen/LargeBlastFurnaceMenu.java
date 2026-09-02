package com.jjyp.ftbicec.screen;

import com.jjyp.ftbicec.block.ICEBlocks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class LargeBlastFurnaceMenu extends AbstractContainerMenu {
    public final ContainerLevelAccess access;

    public LargeBlastFurnaceMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public LargeBlastFurnaceMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ICECMenus.LARGE_BLAST_FURNACE.get(), containerId);
        this.access = access;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ICEBlocks.LARGE_BLAST_FURNACE.get());
    }
}
