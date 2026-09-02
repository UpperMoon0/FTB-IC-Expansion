package com.jjyp.ftbicec.block.entity;

import com.jjyp.ftbicec.block.ICEBlocks;
import com.jjyp.ftbicec.util.MultiblockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LargeBlastFurnaceBlockEntity extends BlockEntity {
    private int checkStructCooldown = 0;
    private boolean structureFormed = false;

    public LargeBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ICEBlockEntities.LARGE_BLAST_FURNACE.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T blockEntity) {
        if (level.isClientSide() || !(blockEntity instanceof LargeBlastFurnaceBlockEntity furnace)) {
            return;
        }

        if (furnace.checkStructCooldown > 0) {
            furnace.checkStructCooldown--;
            return;
        }

        boolean formed = MultiblockHelper.checkForMultiblock(level, blockPos, blockState, getPattern(), 2);
        if (formed != furnace.structureFormed) {
            furnace.structureFormed = formed;
            furnace.setChanged();
        }
        furnace.checkStructCooldown = 20;
    }

    public boolean isStructureFormed() {
        return structureFormed;
    }

    private static Block[][][] getPattern() {
        Block a = ICEBlocks.FIREBRICKS.get();
        Block b = Blocks.AIR;

        return new Block[][][] {
            {
                {a, a, a},
                {a, b, a},
                {a, a, a}
            },
            {
                {a, a, a},
                {a, b, a},
                {a, a, a}
            },
            {
                {a, a, a},
                {a, b, a},
                {a, null, a}
            },
            {
                {a, a, a},
                {a, a, a},
                {a, a, a}
            }
        };
    }
}
