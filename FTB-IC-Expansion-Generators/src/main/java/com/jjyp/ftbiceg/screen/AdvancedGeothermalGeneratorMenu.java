package com.jjyp.ftbiceg.screen;

import com.jjyp.ftbicec.screen.ICEElectricBlockMenu;
import com.jjyp.ftbiceg.block.entity.generator.AdvancedGeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.screen.SimpleItemHandlerSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class AdvancedGeothermalGeneratorMenu extends ICEElectricBlockMenu<AdvancedGeothermalGeneratorBlockEntity> {
    public AdvancedGeothermalGeneratorMenu(int id, Inventory playerInv, AdvancedGeothermalGeneratorBlockEntity r) {
        super(ICEGMenus.ADVANCED_GEOTHERMAL_GENERATOR.get(), id, playerInv, r, null);
    }

    public AdvancedGeothermalGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, (AdvancedGeothermalGeneratorBlockEntity)playerInv.player.level.getBlockEntity(buf.readBlockPos()));
    }

    public void addBlockSlots(@Nullable Object extra) {
        this.addSlot(new SimpleItemHandlerSlot(this.entity.chargeBatteryInventory, 0, 62, 17));
        this.addSlot(new SimpleItemHandlerSlot(this.entity, 0, 62, 53));
    }
}
