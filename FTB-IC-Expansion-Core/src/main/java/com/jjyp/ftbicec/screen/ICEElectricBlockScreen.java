package com.jjyp.ftbicec.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.Optional;

public class ICEElectricBlockScreen <T extends ICEElectricBlockMenu<?>> extends AbstractContainerScreen<T> {
    public static final ResourceLocation BASE_TEXTURE = new ResourceLocation("ftbic", "textures/gui/base.png");
    public int energyX = -1;
    public int energyY = -1;

    public ICEElectricBlockScreen(T m, Inventory inv, Component c) {
        super(m, inv, c);
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        this.renderExtra(poseStack, mouseX, mouseY, delta);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    protected void renderExtra(PoseStack poseStack, int mouseX, int mouseY, float delta) {
    }

    public static boolean isIn(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);
        if (this.energyX != -1 && this.energyY != -1 && isIn(mouseX, mouseY, this.leftPos + this.energyX, this.topPos + this.energyY, 14, 14) && (this.menu).getCarried().isEmpty()) {
            double capacity = this.menu.data.get(SyncedData.ENERGY_CAPACITY);
            double energy = this.menu.data.get(SyncedData.ENERGY);
            this.renderTooltip(poseStack, Collections.singletonList(Component.literal("").append(FTBICUtils.formatEnergy(energy).withStyle(ChatFormatting.GRAY)).append(" / ").append(FTBICUtils.formatEnergy(capacity).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY)), Optional.empty(), mouseX, mouseY);
        }

    }

    public void drawArrow(PoseStack poseStack, int x, int y, int progress) {
        if (progress < 24) {
            this.blit(poseStack, x + progress, y, 87 + progress, 167, 24 - progress, 17);
        }

        if (progress > 0) {
            this.blit(poseStack, x, y, 87, 185, progress, 17);
        }

    }

    public int getEnergyType() {
        return 0;
    }

    public void drawEnergy(PoseStack poseStack, int x, int y, int energy) {
        switch (this.getEnergyType()) {
            case 1:
                if (energy < 14) {
                    this.blit(poseStack, x + energy, y, 91 + energy, 240, 14 - energy, 14);
                }

                if (energy > 0) {
                    this.blit(poseStack, x, y, 106, 240, energy, 14);
                }
                break;
            case 2:
                if (energy < 14) {
                    this.blit(poseStack, x + energy, y, 121 + energy, 240, 14 - energy, 14);
                }

                if (energy > 0) {
                    this.blit(poseStack, x, y, 136, 240, energy, 14);
                }
                break;
            default:
                if (energy < 14) {
                    this.blit(poseStack, x, y, 1, 240, 14, 14 - energy);
                }

                if (energy > 0) {
                    this.blit(poseStack, x, y + (14 - energy), 16, 240 + (14 - energy), 14, energy);
                }
        }

    }

    public void drawFuel(PoseStack poseStack, int x, int y, int fuel) {
        if (fuel < 14) {
            this.blit(poseStack, x, y, 31, 240, 14, 14 - fuel);
        }

        if (fuel > 0) {
            this.blit(poseStack, x, y + (14 - fuel), 46, 240 + (14 - fuel), 14, fuel);
        }

    }

    public void drawSun(PoseStack poseStack, int x, int y, int fuel) {
        if (fuel < 14) {
            this.blit(poseStack, x, y, 61, 240, 14, 14 - fuel);
        }

        if (fuel > 0) {
            this.blit(poseStack, x, y + (14 - fuel), 76, 240 + (14 - fuel), 14, fuel);
        }

    }

    public void drawSlot(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 1, 167, 18, 18);
    }

    public void drawLockedSlot(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 20, 167, 18, 18);
    }

    public void drawLargeSlot(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 1, 186, 26, 26);
    }

    public void drawCombinedSlot(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 1, 213, 47, 26);
    }

    public void drawTank(PoseStack poseStack, int x, int y, FluidStack fluid, int capacity) {
        this.blit(poseStack, x, y, 49, 167, 18, 54);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        double d = (double)fluid.getAmount() / (double)capacity;
        int h = Mth.ceil(d * 52.0);
        if (h > 0) {
            IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType());
            TextureAtlasSprite sprite = (TextureAtlasSprite)this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(renderProps.getStillTexture(fluid));
            int color = renderProps.getTintColor(fluid);
            int r = color >> 16 & 255;
            int g = color >> 8 & 255;
            int b = color >> 0 & 255;
            float u0 = sprite.getU0();
            float v0 = sprite.getV0();
            float u1 = sprite.getU1();
            float v1 = sprite.getV1();
            Matrix4f m = poseStack.last().pose();
            BufferBuilder lv = Tesselator.getInstance().getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            lv.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            lv.vertex(m, (float)x + 1.0F, (float)y + 53.0F - (float)h, 0.0F).color(r, g, b, 255).uv(u0, v0).endVertex();
            lv.vertex(m, (float)x + 1.0F, (float)y + 53.0F, 0.0F).color(r, g, b, 255).uv(u0, v1).endVertex();
            lv.vertex(m, (float)x + 17.0F, (float)y + 53.0F, 0.0F).color(r, g, b, 255).uv(u1, v1).endVertex();
            lv.vertex(m, (float)x + 17.0F, (float)y + 53.0F - (float)h, 0.0F).color(r, g, b, 255).uv(u1, v0).endVertex();
            BufferUploader.drawWithShader(lv.end());
        }

        RenderSystem.setShaderTexture(0, BASE_TEXTURE);
        this.blit(poseStack, x, y, 68, 167, 18, 54);
    }

    public void drawFluidSlot(PoseStack poseStack, int x, int y, FluidStack fluid) {
        this.drawSlot(poseStack, x, y);
        if (!fluid.isEmpty()) {
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(fluid.getFluid());
            TextureAtlasSprite sprite = (TextureAtlasSprite)this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(renderProps.getStillTexture(fluid));
            int color = renderProps.getTintColor(fluid);
            int r = color >> 16 & 255;
            int g = color >> 8 & 255;
            int b = color >> 0 & 255;
            float u0 = sprite.getU0();
            float v0 = sprite.getV0();
            float u1 = sprite.getU1();
            float v1 = sprite.getV1();
            Matrix4f m = poseStack.last().pose();
            BufferBuilder lv = Tesselator.getInstance().getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            lv.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            lv.vertex(m, (float)x + 1.0F, (float)y + 1.0F, 0.0F).color(r, g, b, 255).uv(u0, v0).endVertex();
            lv.vertex(m, (float)x + 1.0F, (float)y + 17.0F, 0.0F).color(r, g, b, 255).uv(u0, v1).endVertex();
            lv.vertex(m, (float)x + 17.0F, (float)y + 17.0F, 0.0F).color(r, g, b, 255).uv(u1, v1).endVertex();
            lv.vertex(m, (float)x + 17.0F, (float)y + 1.0F, 0.0F).color(r, g, b, 255).uv(u1, v0).endVertex();
            BufferUploader.drawWithShader(lv.end());
            RenderSystem.setShaderTexture(0, BASE_TEXTURE);
        }

    }

    public void drawProgressBar(PoseStack poseStack, int x, int y, int progress) {
        if (progress < 26) {
            this.blit(poseStack, x + progress, y, 87 + progress, 203, 26 - progress, 3);
        }

        if (progress > 0) {
            this.blit(poseStack, x, y, 87, 207, progress, 3);
        }

    }

    public void drawArrowDown(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 112, 186, 14, 14);
    }

    public void drawButtonPause(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 112, 167, 18, 18);
    }

    public void drawButtonStart(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 131, 167, 18, 18);
    }

    public void drawButtonFrame(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 150, 167, 18, 18);
    }

    public void drawPauseButton(PoseStack poseStack, int x, int y, int mouseX, int mouseY, boolean paused) {
        if (paused) {
            this.drawButtonStart(poseStack, x, y);
        } else {
            this.drawButtonPause(poseStack, x, y);
        }

        if (isIn(mouseX, mouseY, x, y, 18, 18)) {
            this.drawButtonFrame(poseStack, x, y);
        }

    }

    public void drawSmallButtonPause(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 114, 201, 9, 10);
    }

    public void drawSmallButtonStart(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 124, 201, 9, 10);
    }

    public void drawSmallButtonFrame(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 134, 201, 9, 10);
    }

    public void drawSmallButtonQuestion(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 144, 201, 9, 10);
    }

    public void drawSmallButtonRedstoneDisabled(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 154, 201, 9, 10);
    }

    public void drawSmallButtonRedstoneEnabled(PoseStack poseStack, int x, int y) {
        this.blit(poseStack, x, y, 164, 201, 9, 10);
    }

    public void drawSmallPauseButton(PoseStack poseStack, int x, int y, int mouseX, int mouseY, boolean paused) {
        if (paused) {
            this.drawSmallButtonStart(poseStack, x, y);
        } else {
            this.drawSmallButtonPause(poseStack, x, y);
        }

        if (isIn(mouseX, mouseY, x, y, 9, 10)) {
            this.drawSmallButtonFrame(poseStack, x, y);
        }

    }

    public void drawSmallRedstoneButton(PoseStack poseStack, int x, int y, int mouseX, int mouseY, boolean allowRedstoneControl) {
        if (allowRedstoneControl) {
            this.drawSmallButtonRedstoneEnabled(poseStack, x, y);
        } else {
            this.drawSmallButtonRedstoneDisabled(poseStack, x, y);
        }

        if (isIn(mouseX, mouseY, x, y, 9, 10)) {
            this.drawSmallButtonFrame(poseStack, x, y);
        }

    }

    public void drawSmallQuestionButton(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        this.drawSmallButtonQuestion(poseStack, x, y);
        if (isIn(mouseX, mouseY, x, y, 9, 10)) {
            this.drawSmallButtonFrame(poseStack, x, y);
        }

    }

    public void drawNuclearBar(PoseStack poseStack, int x, int y, boolean active) {
        this.blit(poseStack, x, y, 87, 218, 54, 10);
        if (active) {
            this.blit(poseStack, x, y, 87, 229, 54, 10);
        }

    }

    public void drawHeatBar(PoseStack poseStack, int x, int y, float heat) {
        this.blit(poseStack, x, y, 142, 218, 54, 10);
        if (heat > 0.0F) {
            this.blit(poseStack, x + 1, y, 143, 229, Mth.ceil(heat * 52.0F), 10);
        }

    }

    public ResourceLocation getScreenTexture() {
        return BASE_TEXTURE;
    }

    protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, this.getScreenTexture());
        int x = this.leftPos;
        int y = this.topPos;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.setShaderTexture(0, BASE_TEXTURE);
        if (this.energyX != -1 && this.energyY != -1) {
            int e = Mth.ceil(this.menu.data.get(SyncedData.ENERGY) * 14.0 / this.menu.data.get(SyncedData.ENERGY_CAPACITY));
            this.drawEnergy(poseStack, this.leftPos + this.energyX, this.topPos + this.energyY, e);
        }

    }
}
