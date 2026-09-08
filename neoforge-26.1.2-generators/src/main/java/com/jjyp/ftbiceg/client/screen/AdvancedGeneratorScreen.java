package com.jjyp.ftbiceg.client.screen;

import com.jjyp.ftbiceg.menu.AdvancedGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class AdvancedGeneratorScreen extends FtbicGeneratorScreen<AdvancedGeneratorMenu> {
    public AdvancedGeneratorScreen(AdvancedGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.energyX = 99;
        this.energyY = 27;
    }

    @Override
    protected int getEnergy() {
        return this.menu.getEnergy();
    }

    @Override
    protected int getEnergyCapacity() {
        return this.menu.getEnergyCapacity();
    }

    @Override
    protected void extractMachineOverlays(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int maxFuel = this.menu.getMaxFuelTicks();
        int fuelPixels = maxFuel <= 0
            ? 0
            : Math.round(14F * this.menu.getFuelTicks() / maxFuel);

        drawFuel(graphics, this.leftPos + 63, this.topPos + 27, fuelPixels);
        drawSlot(graphics, this.leftPos + 61, this.topPos + 43);
        drawSlot(graphics, this.leftPos + 97, this.topPos + 43);
    }

    @Override
    protected void extractMachineTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractMachineTooltips(graphics, mouseX, mouseY);
        int x = this.leftPos + 63;
        int y = this.topPos + 27;
        if (isIn(mouseX, mouseY, x, y, 14, 14)) {
            graphics.setTooltipForNextFrame(
                Component.translatable("gui.ftbiceg.fuel", this.menu.getFuelTicks(), this.menu.getMaxFuelTicks()),
                mouseX,
                mouseY
            );
        }
    }
}
