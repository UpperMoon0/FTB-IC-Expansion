package com.jjyp.ftbiceg.client;

import com.jjyp.ftbiceg.screen.AdvancedGeneratorScreen;
import com.jjyp.ftbiceg.screen.AdvancedGeothermalGeneratorScreen;
import com.jjyp.ftbiceg.screen.ICEGMenus;
import dev.ftb.mods.ftbic.FTBICCommon;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ICEGClient extends FTBICCommon {
    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ICEGMenus.ADVANCED_GENERATOR.get(), AdvancedGeneratorScreen::new);
            MenuScreens.register(ICEGMenus.ADVANCED_GEOTHERMAL_GENERATOR.get(), AdvancedGeothermalGeneratorScreen::new);
        });
    }
}
