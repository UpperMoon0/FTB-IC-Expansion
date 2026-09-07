package com.jjyp.ftbiceg.integration.jade;

import com.jjyp.ftbicec.block.entity.ICEElectricBlockEntity;
import com.jjyp.ftbiceg.FTBICEG;
import com.jjyp.ftbiceg.block.entity.generator.AdvancedGeneratorBlockEntity;
import com.jjyp.ftbiceg.block.entity.generator.AdvancedGeothermalGeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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
    private static final GeneratorProvider PROVIDER = new GeneratorProvider();

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(PROVIDER, ICEElectricBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(PROVIDER, Block.class);
    }

    private static final class GeneratorProvider implements IServerDataProvider<ICEElectricBlockEntity>, IBlockComponentProvider {
        private static final ResourceLocation UID = new ResourceLocation(FTBICEG.MODID, "generator_status");
        private static final String ENERGY = "ftbiceg_energy";
        private static final String CAPACITY = "ftbiceg_capacity";
        private static final String FUEL = "ftbiceg_fuel";
        private static final String MAX_FUEL = "ftbiceg_max_fuel";
        private static final String FLUID = "ftbiceg_fluid";
        private static final String FLUID_CAPACITY = "ftbiceg_fluid_capacity";

        @Override
        public void appendServerData(CompoundTag data, ServerPlayer player, Level level, ICEElectricBlockEntity blockEntity, boolean showDetails) {
            if (!(blockEntity instanceof AdvancedGeneratorBlockEntity)
                && !(blockEntity instanceof AdvancedGeothermalGeneratorBlockEntity)) {
                return;
            }

            data.putDouble(ENERGY, blockEntity.getEnergy());
            data.putDouble(CAPACITY, blockEntity.getEnergyCapacity());
            if (blockEntity instanceof AdvancedGeneratorBlockEntity generator) {
                data.putInt(FUEL, generator.fuelTicks);
                data.putInt(MAX_FUEL, generator.maxFuelTicks);
            } else if (blockEntity instanceof AdvancedGeothermalGeneratorBlockEntity geothermal) {
                data.putInt(FLUID, geothermal.fluidAmount);
                data.putInt(FLUID_CAPACITY, geothermal.getTankCapacity());
            }
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains(CAPACITY)) {
                return;
            }

            tooltip.add(Component.translatable(
                "ftbiceg.jade.energy",
                Math.round(data.getDouble(ENERGY)),
                Math.round(data.getDouble(CAPACITY))
            ));
            if (data.contains(MAX_FUEL) && data.getInt(MAX_FUEL) > 0) {
                int remaining = data.getInt(FUEL);
                int max = data.getInt(MAX_FUEL);
                tooltip.add(Component.translatable("ftbiceg.jade.fuel", remaining, max));
            }
            if (data.contains(FLUID_CAPACITY)) {
                tooltip.add(Component.translatable(
                    "ftbiceg.jade.lava",
                    data.getInt(FLUID),
                    data.getInt(FLUID_CAPACITY)
                ));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}
