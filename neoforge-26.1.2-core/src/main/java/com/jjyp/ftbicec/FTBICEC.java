package com.jjyp.ftbicec;

import com.jjyp.ftbicec.registry.ICECRegistries;
import com.nstut.ftbice.shared.ExpansionIds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ExpansionIds.CORE)
public final class FTBICEC {
    public static final String MODID = ExpansionIds.CORE;

    public FTBICEC(IEventBus eventBus) {
        ICECRegistries.BLOCKS.register(eventBus);
        ICECRegistries.ITEMS.register(eventBus);
        ICECRegistries.BLOCK_ENTITIES.register(eventBus);
        ICECRegistries.TABS.register(eventBus);
    }
}
