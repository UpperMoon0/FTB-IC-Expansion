package com.jjyp.ftbiceg.client.screen;

import dev.ftb.mods.ftbic.client.gui.ElectricBlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Small compatibility screen base that keeps expansion generators visually aligned with FTBIC.
 *
 * <p>The expansion menus are not {@code ElectricBlockMenu}s, so they cannot directly extend
 * FTBIC's {@link ElectricBlockScreen}. Reusing the exact base texture and widget UVs here keeps
 * the UI consistent without coupling the expansion machine implementation to FTBIC's menu type.</p>
 */
abstract class FtbicGeneratorScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private static final Identifier BASE_TEXTURE = ElectricBlockScreen.BASE_TEXTURE;
    private static final Identifier LAVA_STILL = Identifier.parse("minecraft:block/lava_still");

    protected int energyX = -1;
    protected int energyY = -1;

    protected FtbicGeneratorScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        drawBase(graphics);
        drawEnergyBar(graphics);
        extractMachineOverlays(graphics, mouseX, mouseY, partialTick);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        extractMachineTooltips(graphics, mouseX, mouseY);
    }

    protected abstract int getEnergy();

    protected abstract int getEnergyCapacity();

    protected void extractMachineOverlays(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    }

    protected void extractMachineTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (energyX < 0 || energyY < 0) {
            return;
        }

        int x = leftPos + energyX;
        int y = topPos + energyY;
        if (isIn(mouseX, mouseY, x, y, 14, 14)) {
            graphics.setTooltipForNextFrame(
                Component.translatable("gui.ftbiceg.energy", getEnergy(), getEnergyCapacity()),
                mouseX,
                mouseY
            );
        }
    }

    protected final void drawFuel(GuiGraphicsExtractor graphics, int x, int y, int fuelPixels) {
        int fuel = Mth.clamp(fuelPixels, 0, 14);
        if (fuel < 14) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 31F, 240F, 14, 14 - fuel, 256, 256);
        }
        if (fuel > 0) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BASE_TEXTURE,
                x,
                y + (14 - fuel),
                46F,
                240F + (14 - fuel),
                14,
                fuel,
                256,
                256
            );
        }
    }

    protected final void drawSlot(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 1F, 167F, 18, 18, 256, 256);
    }

    protected final void drawLavaTank(GuiGraphicsExtractor graphics, int x, int y, int amount, int capacity) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 49F, 167F, 18, 54, 256, 256);

        if (amount > 0 && capacity > 0) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                .getAtlasManager()
                .getAtlasOrThrow(AtlasIds.BLOCKS)
                .getSprite(LAVA_STILL);
            double fraction = Math.min(1D, amount / (double) capacity);
            int height = Mth.ceil(fraction * 52D);
            int rowsLeft = height;
            int currentY = y + 53;
            while (rowsLeft > 0) {
                int chunk = Math.min(16, rowsLeft);
                currentY -= chunk;
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + 1, currentY, 16, chunk);
                rowsLeft -= chunk;
            }
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 68F, 167F, 18, 54, 256, 256);
    }

    protected final void lavaTankTooltip(
        GuiGraphicsExtractor graphics,
        int x,
        int y,
        int mouseX,
        int mouseY,
        int amount,
        int capacity
    ) {
        if (isIn(mouseX, mouseY, x, y, 18, 54)) {
            graphics.setTooltipForNextFrame(
                Component.translatable("gui.ftbiceg.lava", amount, capacity),
                mouseX,
                mouseY
            );
        }
    }

    protected static boolean isIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private void drawBase(GuiGraphicsExtractor graphics) {
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BASE_TEXTURE,
            leftPos,
            topPos,
            0F,
            0F,
            imageWidth,
            imageHeight,
            256,
            256
        );
    }

    private void drawEnergyBar(GuiGraphicsExtractor graphics) {
        if (energyX < 0 || energyY < 0) {
            return;
        }

        int capacity = getEnergyCapacity();
        double fraction = capacity <= 0 ? 0D : Mth.clamp(getEnergy() / (double) capacity, 0D, 1D);
        drawEnergy(graphics, leftPos + energyX, topPos + energyY, Mth.ceil(fraction * 14D));
    }

    private void drawEnergy(GuiGraphicsExtractor graphics, int x, int y, int energyPixels) {
        int energy = Mth.clamp(energyPixels, 0, 14);
        if (energy < 14) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 1F, 240F, 14, 14 - energy, 256, 256);
        }
        if (energy > 0) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BASE_TEXTURE,
                x,
                y + (14 - energy),
                16F,
                240F + (14 - energy),
                14,
                energy,
                256,
                256
            );
        }
    }
}
