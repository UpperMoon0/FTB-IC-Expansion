package com.jjyp.ftbiceg.screen;

import com.jjyp.ftbicec.screen.ICEElectricBlockScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;


public class AdvancedGeothermalGeneratorScreen extends ICEElectricBlockScreen<AdvancedGeothermalGeneratorMenu> {
    public AdvancedGeothermalGeneratorScreen(AdvancedGeothermalGeneratorMenu m, Inventory inv, Component c) {
        super(m, inv, c);
        this.energyX = 63;
        this.energyY = 36;
    }

    protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        super.renderBg(poseStack, delta, mouseX, mouseY);
        this.drawTank(poseStack, this.leftPos + 97, this.topPos + 16, new FluidStack(Fluids.LAVA, this.menu.data.get(SyncedData.BAR)), 24000);
        this.drawSlot(poseStack, this.leftPos + 61, this.topPos + 16);
        this.drawSlot(poseStack, this.leftPos + 61, this.topPos + 52);
    }
}
