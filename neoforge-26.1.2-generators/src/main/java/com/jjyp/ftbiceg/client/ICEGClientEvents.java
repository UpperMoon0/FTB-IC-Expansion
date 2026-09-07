package com.jjyp.ftbiceg.client;

import com.jjyp.ftbiceg.FTBICEG;
import com.jjyp.ftbiceg.client.screen.AdvancedGeneratorScreen;
import com.jjyp.ftbiceg.client.screen.AdvancedGeothermalGeneratorScreen;
import com.jjyp.ftbiceg.registry.ICEGRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = FTBICEG.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ICEGClientEvents {
    private ICEGClientEvents() {
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ICEGRegistries.ADVANCED_GENERATOR_MENU.get(), AdvancedGeneratorScreen::new);
        event.register(ICEGRegistries.ADVANCED_GEOTHERMAL_GENERATOR_MENU.get(), AdvancedGeothermalGeneratorScreen::new);
    }
}
