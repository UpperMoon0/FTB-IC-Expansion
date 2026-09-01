package com.jjyp.ftbicec.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LargeBlastFurnaceScreen extends AbstractContainerScreen {
    private static final ResourceLocation texture = new ResourceLocation("ftbicec", "textures/gui/large_blast_furnace.png");
    public LargeBlastFurnaceScreen(LargeBlastFurnaceMenu containerMenu, Inventory inventory, Component component) {
        super(containerMenu, inventory, component);
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float delta) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, delta);
        this.renderTooltip(pose, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack pose, float mouseX, int mouseY, int partialTick) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, texture);
        blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
