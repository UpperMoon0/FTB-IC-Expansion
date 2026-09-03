package com.jjyp.ftbiceg;

import com.jjyp.ftbiceg.registry.ICEGRegistries;
import com.nstut.ftbice.shared.ExpansionIds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(ExpansionIds.GENERATORS)
public final class FTBICEG {
    public static final String MODID = ExpansionIds.GENERATORS;

    public FTBICEG(IEventBus eventBus, ModContainer container) {
        ICEGRegistries.BLOCKS.register(eventBus);
        ICEGRegistries.ITEMS.register(eventBus);
        ICEGRegistries.BLOCK_ENTITIES.register(eventBus);
        ICEGRegistries.TABS.register(eventBus);
        container.registerConfig(ModConfig.Type.COMMON, ICEGConfig.COMMON_SPEC);
    }
}
