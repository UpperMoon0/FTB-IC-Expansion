package com.jjyp.ftbiceg.block;

import com.jjyp.ftbicec.machine.ExpansionGeneratorBlock;
import com.jjyp.ftbiceg.block.entity.AdvancedGeothermalGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class AdvancedGeothermalGeneratorBlock extends ExpansionGeneratorBlock {
    public AdvancedGeothermalGeneratorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedGeothermalGeneratorBlockEntity(pos, state);
    }
}
