package com.jjyp.ftbiceg.block;

import com.jjyp.ftbicec.machine.ExpansionGeneratorBlock;
import com.jjyp.ftbiceg.block.entity.AdvancedGeothermalGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class AdvancedGeothermalGeneratorBlock extends ExpansionGeneratorBlock {
    public AdvancedGeothermalGeneratorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.LAVA_BUCKET) && level.getBlockEntity(pos) instanceof AdvancedGeothermalGeneratorBlockEntity generator) {
            return generator.interactWithItem(player, hand, stack, hit);
        }
        return openGeneratorMenu(level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        return openGeneratorMenu(level, pos, player);
    }

    private static InteractionResult openGeneratorMenu(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AdvancedGeothermalGeneratorBlockEntity generator) {
            player.openMenu(generator);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedGeothermalGeneratorBlockEntity(pos, state);
    }
}
