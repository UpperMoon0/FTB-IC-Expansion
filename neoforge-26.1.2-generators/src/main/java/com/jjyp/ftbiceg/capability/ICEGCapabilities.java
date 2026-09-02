package com.jjyp.ftbiceg.capability;

import com.jjyp.ftbicec.machine.ExpansionEnergyHandler;
import com.jjyp.ftbiceg.FTBICEG;
import com.jjyp.ftbiceg.block.entity.AdvancedGeneratorBlockEntity;
import com.jjyp.ftbiceg.block.entity.AdvancedGeothermalGeneratorBlockEntity;
import com.jjyp.ftbiceg.registry.ICEGRegistries;
import dev.ftb.mods.ftbic.util.FTBICCapabilities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = FTBICEG.MODID)
public final class ICEGCapabilities {
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        @SuppressWarnings("unchecked")
        BlockEntityType<AdvancedGeneratorBlockEntity> generatorType =
            (BlockEntityType<AdvancedGeneratorBlockEntity>) (Object) ICEGRegistries.ADVANCED_GENERATOR_BE.get();
        @SuppressWarnings("unchecked")
        BlockEntityType<AdvancedGeothermalGeneratorBlockEntity> geothermalType =
            (BlockEntityType<AdvancedGeothermalGeneratorBlockEntity>) (Object) ICEGRegistries.ADVANCED_GEOTHERMAL_GENERATOR_BE.get();

        event.registerBlockEntity(FTBICCapabilities.ZAP_ENERGY_BLOCK, generatorType, (be, side) -> be);
        event.registerBlockEntity(FTBICCapabilities.ZAP_ENERGY_BLOCK, geothermalType, (be, side) -> be);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, generatorType, (be, side) -> new ExpansionEnergyHandler(be));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, geothermalType, (be, side) -> new ExpansionEnergyHandler(be));
        event.registerBlockEntity(Capabilities.Item.BLOCK, generatorType, (be, side) -> new GeneratorFuelHandler(be));
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, geothermalType, (be, side) -> new AdvancedGeothermalTankHandler(be));
    }

    private ICEGCapabilities() {
    }
}
