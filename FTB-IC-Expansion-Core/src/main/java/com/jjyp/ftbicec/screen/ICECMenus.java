package com.jjyp.ftbicec.screen;

import com.jjyp.ftbicec.FTBICEC;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ICECMenus {
    DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "ftbicec");
    RegistryObject<MenuType<LargeBlastFurnaceMenu>> LARGE_BLAST_FURNACE = REGISTRY.register("large_blast_furnace", () -> new MenuType(LargeBlastFurnaceMenu::new));

    /*
    static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, IContainerFactory<T> factory) {
        if (FTBICEC.debug)
            System.out.println("Registering menu: " + id + " from ftbicec");

        return REGISTRY.register(id, () -> new MenuType(factory));
    }
    */
}
