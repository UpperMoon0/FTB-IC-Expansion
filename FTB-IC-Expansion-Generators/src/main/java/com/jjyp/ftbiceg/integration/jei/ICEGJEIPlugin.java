package com.jjyp.ftbiceg.integration.jei;

import com.jjyp.ftbiceg.FTBICEG;
import com.jjyp.ftbiceg.block.entity.ICEGElectricBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class ICEGJEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(FTBICEG.MODID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
            new ItemStack(ICEGElectricBlocks.ADVANCED_GENERATOR.item.get()),
            RecipeTypes.FUELING
        );
    }
}
