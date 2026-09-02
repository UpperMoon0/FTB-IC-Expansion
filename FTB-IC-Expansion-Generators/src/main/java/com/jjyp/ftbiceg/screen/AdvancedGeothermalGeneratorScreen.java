package com.jjyp.ftbiceg.screen;

import com.jjyp.ftbicec.screen.ICEElectricBlockScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class AdvancedGeothermalGeneratorScreen extends ICEElectricBlockScreen<AdvancedGeothermalGeneratorMenu> {
    public AdvancedGeothermalGeneratorScreen(AdvancedGeothermalGeneratorMenu m, Inventory inv, Component c) {
        super(m, inv, c);
        this.energyX = 63;
        this.energyY = 36;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, this.getScreenTexture());
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.setShaderTexture(0, BASE_TEXTURE);
        if (this.energyX != -1 && this.energyY != -1) {
            int energy = Mth.ceil(this.menu.data.get(SyncedData.ENERGY) * 14.0 / this.menu.data.get(SyncedData.ENERGY_CAPACITY));
            this.drawEnergy(poseStack, this.leftPos + this.energyX, this.topPos + this.energyY, energy);
        }
        this.drawTank(poseStack, this.leftPos + 97, this.topPos + 16, new FluidStack(Fluids.LAVA, this.menu.data.get(SyncedData.BAR)), 24000);
        this.drawSlot(poseStack, this.leftPos + 61, this.topPos + 16);
        this.drawSlot(poseStack, this.leftPos + 61, this.topPos + 52);
    }
}
