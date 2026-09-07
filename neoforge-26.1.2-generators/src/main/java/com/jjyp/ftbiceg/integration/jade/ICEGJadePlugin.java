package com.jjyp.ftbiceg.integration.jade;

import com.jjyp.ftbicec.machine.ExpansionGeneratorBlockEntity;
import com.jjyp.ftbiceg.FTBICEG;
import com.jjyp.ftbiceg.block.entity.AdvancedGeneratorBlockEntity;
import com.jjyp.ftbiceg.block.entity.AdvancedGeothermalGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public final class ICEGJadePlugin implements IWailaPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(FTBICEG.MODID, "generator_status");
    private static final String ENERGY = "ftbiceg_energy";
    private static final String CAPACITY = "ftbiceg_capacity";
    private static final String FUEL = "ftbiceg_fuel";
    private static final String MAX_FUEL = "ftbiceg_max_fuel";
    private static final String FLUID = "ftbiceg_fluid";
    private static final String FLUID_CAPACITY = "ftbiceg_fluid_capacity";

    private static final GeneratorDataProvider DATA_PROVIDER = new GeneratorDataProvider();
    private static final GeneratorComponentProvider COMPONENT_PROVIDER = new GeneratorComponentProvider();

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DATA_PROVIDER, ExpansionGeneratorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(COMPONENT_PROVIDER, Block.class);
    }

    private static final class GeneratorDataProvider implements IServerDataProvider<BlockAccessor> {
        @Override
        public Identifier getUid() {
            return UID;
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!(accessor.getBlockEntity() instanceof ExpansionGeneratorBlockEntity generator)) {
                return;
            }
            data.putDouble(ENERGY, generator.getEnergy());
            data.putDouble(CAPACITY, generator.getEnergyCapacity());
            if (generator instanceof AdvancedGeneratorBlockEntity advanced) {
                data.putInt(FUEL, advanced.getFuelTicks());
                data.putInt(MAX_FUEL, advanced.getMaxFuelTicks());
            } else if (generator instanceof AdvancedGeothermalGeneratorBlockEntity geothermal) {
                data.putInt(FLUID, geothermal.getFluidAmount());
                data.putInt(FLUID_CAPACITY, geothermal.getTankCapacity());
            }
        }
    }

    private static final class GeneratorComponentProvider implements IBlockComponentProvider {
        @Override
        public Identifier getUid() {
            return UID;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains(CAPACITY)) {
                return;
            }
            tooltip.add(Component.translatable(
                "ftbiceg.jade.energy",
                Math.round(data.getDoubleOr(ENERGY, 0D)),
                Math.round(data.getDoubleOr(CAPACITY, 0D))
            ));
            int maxFuel = data.getIntOr(MAX_FUEL, 0);
            if (maxFuel > 0) {
                tooltip.add(Component.translatable(
                    "ftbiceg.jade.fuel",
                    data.getIntOr(FUEL, 0),
                    maxFuel
                ));
            }
            if (data.contains(FLUID_CAPACITY)) {
                tooltip.add(Component.translatable(
                    "ftbiceg.jade.lava",
                    data.getIntOr(FLUID, 0),
                    data.getIntOr(FLUID_CAPACITY, 0)
                ));
            }
        }
    }
}
