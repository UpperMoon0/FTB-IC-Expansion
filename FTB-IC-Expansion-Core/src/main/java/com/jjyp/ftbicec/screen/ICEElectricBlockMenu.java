package com.jjyp.ftbicec.screen;

import com.jjyp.ftbicec.block.entity.ICEElectricBlockEntity;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ICEElectricBlockMenu <E extends ICEElectricBlockEntity> extends AbstractContainerMenu {
    public final E entity;
    public final Player player;
    public final SyncedData data;
    private final int slotCount;

    public ICEElectricBlockMenu(MenuType<?> type, int id, Inventory playerInv, E r, @Nullable Object extra) {
        super(type, id);
        this.entity = r;
        this.player = playerInv.player;
        this.data = new SyncedData();
        int prevSlotCount = this.slots.size();
        this.addBlockSlots(extra);
        this.slotCount = this.slots.size() - prevSlotCount;
        int playerSlotOffset = this.getPlayerSlotOffset();

        int x;
        for(x = 0; x < 3; ++x) {
            for(int y = 0; y < 9; ++y) {
                this.addSlot(new Slot(playerInv, y + x * 9 + 9, 8 + y * 18, playerSlotOffset + x * 18));
            }
        }

        for(x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, playerSlotOffset + 58));
        }

        this.entity.addSyncData(this.data);
        this.data.setup();
        this.addDataSlots(this.data);
    }

    public int getPlayerSlotOffset() {
        return 84;
    }

    public void addBlockSlots(@Nullable Object extra) {
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot.hasItem()) {
            ItemStack stack2 = slot.getItem();
            stack = stack2.copy();
            if (slotId < this.slotCount) {
                if (!this.moveItemStackTo(stack2, this.slotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack2, 0, this.slotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (stack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    public boolean stillValid(@NotNull Player player) {
        return !this.entity.isRemoved() && !this.entity.isBurnt();
    }

    public void broadcastChanges() {
        if (this.entity.hasLevel() && !Objects.requireNonNull(this.entity.getLevel()).isClientSide()) {
            this.data.update();
        }

        super.broadcastChanges();
    }

    public boolean clickMenuButton(@NotNull Player player, int button) {
        return false;
    }
}
