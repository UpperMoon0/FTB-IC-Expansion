package com.jjyp.ftbiceg.screen;

import com.jjyp.ftbicec.screen.ICEElectricBlockMenu;
import com.jjyp.ftbiceg.block.entity.generator.AdvancedGeneratorBlockEntity;
import dev.ftb.mods.ftbic.screen.SimpleItemHandlerSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class AdvancedGeneratorMenu extends ICEElectricBlockMenu<AdvancedGeneratorBlockEntity> {
    public AdvancedGeneratorMenu(int id, Inventory playerInv, AdvancedGeneratorBlockEntity r) {
        super(ICEGMenus.ADVANCED_GENERATOR.get(), id, playerInv, r, null);
    }

    public AdvancedGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, (AdvancedGeneratorBlockEntity)playerInv.player.level.getBlockEntity(buf.readBlockPos()));
    }

    public void addBlockSlots(@Nullable Object extra) {
        this.addSlot(new SimpleItemHandlerSlot(this.entity.chargeBatteryInventory, 0, 98, 44));
        this.addSlot(new SimpleItemHandlerSlot(this.entity, 0, 62, 44));
    }
}
