package com.jjyp.ftbicec.block.entity;

import com.jjyp.ftbicec.block.LargeBlastFurnaceBlock;
import com.jjyp.ftbicec.registry.ICECRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class LargeBlastFurnaceBlockEntity extends BlockEntity {
    private static final int IGNORE = -1;
    private static final int AIR = 0;
    private static final int BRICK = 1;

    // Base layout faces SOUTH. Layer 1 contains the controller at the middle of the south edge.
    private static final int[][][] PATTERN = {
        {
            {BRICK, BRICK, BRICK},
            {BRICK, BRICK, BRICK},
            {BRICK, BRICK, BRICK}
        },
        {
            {BRICK, BRICK, BRICK},
            {BRICK, AIR, BRICK},
            {BRICK, IGNORE, BRICK}
        },
        {
            {BRICK, BRICK, BRICK},
            {BRICK, AIR, BRICK},
            {BRICK, BRICK, BRICK}
        },
        {
            {BRICK, BRICK, BRICK},
            {BRICK, AIR, BRICK},
            {BRICK, BRICK, BRICK}
        }
    };

    private int checkCooldown;
    private boolean structureFormed;

    public LargeBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ICECRegistries.LARGE_BLAST_FURNACE_BE.get(), pos, state);
    }

    public void serverTick() {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (checkCooldown-- > 0) {
            return;
        }
        checkCooldown = 20;

        boolean formed = checkStructure();
        if (formed != structureFormed) {
            structureFormed = formed;
            BlockState state = getBlockState();
            if (state.hasProperty(LargeBlastFurnaceBlock.ACTIVE)
                && state.getValue(LargeBlastFurnaceBlock.ACTIVE) != formed) {
                level.setBlock(worldPosition, state.setValue(LargeBlastFurnaceBlock.ACTIVE, formed), 3);
            }
            setChanged();
        }
    }

    public boolean isStructureFormed() {
        return structureFormed;
    }

    public Component structureStatus() {
        return Component.translatable(structureFormed
            ? "message.ftbicec.large_blast_furnace.formed"
            : "message.ftbicec.large_blast_furnace.incomplete");
    }

    private boolean checkStructure() {
        if (level == null) {
            return false;
        }
        Direction facing = getBlockState().getValue(LargeBlastFurnaceBlock.FACING);
        Block firebricks = ICECRegistries.FIREBRICKS.get();

        for (int y = 0; y < PATTERN.length; y++) {
            for (int z = 0; z < PATTERN[y].length; z++) {
                for (int x = 0; x < PATTERN[y][z].length; x++) {
                    int expected = PATTERN[y][z][x];
                    if (expected == IGNORE) {
                        continue;
                    }

                    int baseX = x - 1;
                    int baseZ = z - 2;
                    int worldX;
                    int worldZ;
                    switch (facing) {
                        case NORTH -> {
                            worldX = -baseX;
                            worldZ = -baseZ;
                        }
                        case EAST -> {
                            worldX = baseZ;
                            worldZ = -baseX;
                        }
                        case WEST -> {
                            worldX = -baseZ;
                            worldZ = baseX;
                        }
                        default -> {
                            worldX = baseX;
                            worldZ = baseZ;
                        }
                    }

                    BlockState found = level.getBlockState(worldPosition.offset(worldX, y - 1, worldZ));
                    if (expected == AIR) {
                        if (!found.is(Blocks.AIR)) {
                            return false;
                        }
                    } else if (!found.is(firebricks)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
