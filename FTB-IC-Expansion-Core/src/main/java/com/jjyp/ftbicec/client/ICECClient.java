package com.jjyp.ftbicec.client;

import com.jjyp.ftbicec.screen.ICECMenus;
import com.jjyp.ftbicec.screen.LargeBlastFurnaceMenu;
import com.jjyp.ftbicec.screen.LargeBlastFurnaceScreen;
import dev.ftb.mods.ftbic.FTBICCommon;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ICECClient extends FTBICCommon {
    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }
    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> MenuScreens.register(ICECMenus.LARGE_BLAST_FURNACE.get(), LargeBlastFurnaceScreen::new)
        );
    }
}
