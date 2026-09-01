package com.jjyp.ftbicec.block;

import com.jjyp.ftbicec.block.entity.ICEElectricBlockEntity;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BaseCableBlock;
import dev.ftb.mods.ftbic.block.BurntCableBlock;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ICECableBlock extends BaseCableBlock {
    public final EnergyTier tier;

    public ICECableBlock(EnergyTier _tier, int border, SoundType soundType) {
        super(border, soundType);
        this.tier = _tier;
    }

    /** @deprecated */
    @Deprecated
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (facingState.getBlock() instanceof BurntCableBlock) {
            return state;
        } else {
            boolean c = this.canCableConnectFrom(facingState, level, facingPos, facing.getOpposite());
            if (!level.isClientSide() && facingState.getBlock() != this && c != state.getValue(CONNECTION[facing.ordinal()])) {
                ICEElectricBlockEntity.electricNetworkUpdated(level, facingPos);
            }

            return state.setValue(CONNECTION[facing.ordinal()], c);
        }
    }

    private boolean canCableConnectFrom(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        if (state.getBlock() instanceof ICECableBlock) {
            return state.getBlock() == this;
        } else if (state.getBlock() instanceof ICEElectricBlock) {
            return true;
        } else {
            if (!state.isAir()) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof EnergyHandler) {
                    return true;
                }

                if (be != null && FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get() > 0.0) {
                    return be.getCapability(ForgeCapabilities.ENERGY, face).filter(IEnergyStorage::canReceive).isPresent();
                }
            }

            return false;
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Direction direction = var5[var7];
            BlockPos p = pos.relative(direction);
            BlockState s = world.getBlockState(p);
            if (this.canCableConnectFrom(s, world, p, direction.getOpposite())) {
                state = state.setValue(CONNECTION[direction.ordinal()], true);
            }
        }

        return state.setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
    }

    /** @deprecated */
    @Deprecated
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
        super.onPlace(state, level, pos, state1, b);
        if (!level.isClientSide() && !state.is(state1.getBlock())) {
            ICEElectricBlockEntity.electricNetworkUpdated(level, pos);
        }

    }

    /** @deprecated */
    @Deprecated
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
        super.onRemove(state, level, pos, state1, b);
        if (!level.isClientSide() && !state.is(state1.getBlock())) {
            ICEElectricBlockEntity.electricNetworkUpdated(level, pos);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("ftbic.max_input", new Object[]{FTBICUtils.formatEnergy(this.tier.transferRate.get()).append("/t").withStyle(ChatFormatting.GRAY)}).withStyle(ChatFormatting.DARK_GRAY));
        Double feRatio = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
        if (feRatio > 0.0) {
            list.add(Component.translatable("ftbic.zap_to_fe_conversion", new Object[]{FTBICConfig.ENERGY_FORMAT, feRatio}).withStyle(ChatFormatting.DARK_GRAY));
        }

    }
}
