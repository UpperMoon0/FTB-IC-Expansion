package com.jjyp.ftbiceg.integration.jei;

import com.jjyp.ftbiceg.FTBICEG;
import com.jjyp.ftbiceg.registry.ICEGRegistries;
import dev.ftb.mods.ftbic.integration.jei.GeothermalFuelCategory;
import dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeType;

@JeiPlugin
public final class ICEGJEIPlugin implements IModPlugin {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(FTBICEG.MODID, "jei");

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(basicGeneratorFuelType(), ICEGRegistries.ADVANCED_GENERATOR.get());
        registration.addCraftingStation(GeothermalFuelCategory.TYPE, ICEGRegistries.ADVANCED_GEOTHERMAL_GENERATOR.get());
    }

    @SuppressWarnings("unchecked")
    private static IRecipeHolderType<BasicGeneratorFuelRecipe> basicGeneratorFuelType() {
        return IRecipeType.create((RecipeType<BasicGeneratorFuelRecipe>)
            (RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get());
    }
}
