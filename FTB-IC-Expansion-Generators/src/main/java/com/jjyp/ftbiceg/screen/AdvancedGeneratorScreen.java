package com.jjyp.ftbiceg.screen;

import com.jjyp.ftbicec.screen.ICEElectricBlockScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedGeneratorScreen extends ICEElectricBlockScreen<AdvancedGeneratorMenu> {
    public AdvancedGeneratorScreen(AdvancedGeneratorMenu m, Inventory inv, Component c) {
        super(m, inv, c);
        this.energyX = 99;
        this.energyY = 27;
    }

    protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        super.renderBg(poseStack, delta, mouseX, mouseY);
        this.drawFuel(poseStack, this.leftPos + 63, this.topPos + 27, this.menu.data.get(SyncedData.BAR));
        this.drawSlot(poseStack, this.leftPos + 61, this.topPos + 43);
        this.drawSlot(poseStack, this.leftPos + 97, this.topPos + 43);
    }
}
