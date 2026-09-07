package com.jjyp.ftbiceg.client.screen;

import com.jjyp.ftbiceg.menu.AdvancedGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class AdvancedGeneratorScreen extends AbstractContainerScreen<AdvancedGeneratorMenu> {
    private static final int PANEL = 0xFFC6C6C6;
    private static final int LIGHT = 0xFFFFFFFF;
    private static final int DARK = 0xFF555555;
    private static final int SLOT_BORDER = 0xFF373737;
    private static final int SLOT_INNER = 0xFF8B8B8B;
    private static final int TEXT = 0x404040;

    public AdvancedGeneratorScreen(AdvancedGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        drawPanel(graphics);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(this.font, Component.translatable(
            "gui.ftbiceg.energy", this.menu.getEnergy(), this.menu.getEnergyCapacity()
        ), 8, 18, TEXT, false);
        graphics.text(this.font, Component.translatable(
            "gui.ftbiceg.fuel", this.menu.getFuelTicks(), this.menu.getMaxFuelTicks()
        ), 8, 30, TEXT, false);
        graphics.text(this.font, Component.translatable("gui.ftbiceg.fuel_slot"), 24, 50, TEXT, false);
        graphics.text(this.font, Component.translatable("gui.ftbiceg.battery_slot"), 116, 50, TEXT, false);
    }

    private void drawPanel(GuiGraphicsExtractor graphics) {
        int left = this.leftPos;
        int top = this.topPos;
        graphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, PANEL);
        graphics.fill(left, top, left + this.imageWidth, top + 1, LIGHT);
        graphics.fill(left, top, left + 1, top + this.imageHeight, LIGHT);
        graphics.fill(left, top + this.imageHeight - 1, left + this.imageWidth, top + this.imageHeight, DARK);
        graphics.fill(left + this.imageWidth - 1, top, left + this.imageWidth, top + this.imageHeight, DARK);

        for (Slot slot : this.menu.slots) {
            int x = left + slot.x - 1;
            int y = top + slot.y - 1;
            graphics.fill(x, y, x + 18, y + 18, SLOT_BORDER);
            graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_INNER);
        }
    }
}
