package com.jjyp.ftbicec.screen;

import com.jjyp.ftbicec.block.ICEBlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class LargeBlastFurnaceMenu extends AbstractContainerMenu {
    public final ContainerLevelAccess access;
    // Client menu constructor
    public LargeBlastFurnaceMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    // Server menu constructor
    public LargeBlastFurnaceMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ICECMenus.LARGE_BLAST_FURNACE.get(), containerId);
        this.access = access;
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ICEBlocks.LARGE_BLAST_FURNACE.get());
    }
}
