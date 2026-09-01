package com.jjyp.ftbicec.block.entity;

import com.jjyp.ftbicec.block.ICEBlocks;
import com.jjyp.ftbicec.util.MultiblockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.jjyp.ftbicec.block.LargeBlastFurnaceBlock.FACING;

public class LargeBlastFurnaceBlockEntity extends BlockEntity {
    private static int checkStructCD = 0;
    public LargeBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ICEBlockEntities.LARGE_BLAST_FURNACE.get(), pos, state);
    }
    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!level.isClientSide()) {
            if (checkStructCD > 0)
                checkStructCD--;
            else {
                if (MultiblockHelper.checkForMultiblock(level, blockPos, blockState, getPattern(), 2))
                    System.out.println("Structure Formed " + blockState.getValue(FACING));
                checkStructCD = 20;
            }
        }
    }

    private static Block[][][] getPattern()
    {
        Block a = ICEBlocks.FIREBRICKS.get(),
              b = Blocks.AIR;

        Block[][][] pattern =
        {
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

        return pattern;
    }
}
