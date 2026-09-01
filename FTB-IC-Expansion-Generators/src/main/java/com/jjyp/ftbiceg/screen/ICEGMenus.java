package com.jjyp.ftbiceg.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ICEGMenus {
    DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "ftbiceg");
    RegistryObject<MenuType<AdvancedGeneratorMenu>> ADVANCED_GENERATOR = register("advanced_generator", AdvancedGeneratorMenu::new);
    RegistryObject<MenuType<AdvancedGeothermalGeneratorMenu>> ADVANCED_GEOTHERMAL_GENERATOR = register("advanced_geothermal_generator", AdvancedGeothermalGeneratorMenu::new);
    static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, IContainerFactory<T> factory) {
        System.out.println("Registering menu: " + id);
        return REGISTRY.register(id, () -> new MenuType(factory));
    }
}
