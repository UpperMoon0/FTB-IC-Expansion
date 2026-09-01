package com.jjyp.ftbiceg.block.entity;

import com.jjyp.ftbicec.block.ICEElectricBlockInstance;
import com.jjyp.ftbiceg.block.entity.generator.AdvancedGeothermalGeneratorBlockEntity;
import com.jjyp.ftbiceg.ICEGConfig;
import com.jjyp.ftbiceg.block.entity.generator.AdvancedGeneratorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface ICEGElectricBlocks {
    ICEElectricBlockInstance ADVANCED_GENERATOR = register("advanced_generator", AdvancedGeneratorBlockEntity::new)
            .energyCapacity(ICEGConfig.MACHINES.ADVANCED_GENERATOR_CAPACITY)
            .maxEnergyOutput(ICEGConfig.MACHINES.ADVANCED_GENERATOR_OUTPUT)
            .io(1, 0);
    ICEElectricBlockInstance ADVANCED_GEOTHERMAL_GENERATOR = register("advanced_geothermal_generator", AdvancedGeothermalGeneratorBlockEntity::new)
            .energyCapacity(ICEGConfig.MACHINES.ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY)
            .maxEnergyOutput(ICEGConfig.MACHINES.ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT)
            .io(1, 1);
    static ICEElectricBlockInstance register(String id, BlockEntityType.BlockEntitySupplier<BlockEntity> supplier) {
        return new ICEElectricBlockInstance(id, supplier, "ftbiceg");
    }

    static void init() {
    }
}
