package com.jjyp.ftbiceg.client.screen;

import com.jjyp.ftbiceg.menu.AdvancedGeothermalGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class AdvancedGeothermalGeneratorScreen extends FtbicGeneratorScreen<AdvancedGeothermalGeneratorMenu> {
    public AdvancedGeothermalGeneratorScreen(AdvancedGeothermalGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.energyX = 63;
        this.energyY = 36;
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
        drawSlot(graphics, this.leftPos + 61, this.topPos + 16);
        drawLavaTank(
            graphics,
            this.leftPos + 97,
            this.topPos + 16,
            this.menu.getFluidAmount(),
            this.menu.getTankCapacity()
        );
    }

    @Override
    protected void extractMachineTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractMachineTooltips(graphics, mouseX, mouseY);
        lavaTankTooltip(
            graphics,
            this.leftPos + 97,
            this.topPos + 16,
            mouseX,
            mouseY,
            this.menu.getFluidAmount(),
            this.menu.getTankCapacity()
        );
    }
}
