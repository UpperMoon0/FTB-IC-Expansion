//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jjyp.ftbicec.block;

import com.jjyp.ftbicec.block.entity.ICEElectricBlockEntity;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.SprayPaintable;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class ICEElectricBlock extends Block implements EntityBlock, SprayPaintable {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public final ICEElectricBlockInstance electricBlockInstance;

    public ICEElectricBlock(ICEElectricBlockInstance m) {
        super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).requiresCorrectToolForDrops());
        this.electricBlockInstance = m;
        BlockState state = (BlockState)((BlockState)this.getStateDefinition().any()).setValue(SprayPaintable.DARK, false);
        if (m.facingProperty != null) {
            state = (BlockState)state.setValue(this.electricBlockInstance.facingProperty, Direction.SOUTH);
        }

        if (this.electricBlockInstance.canBeActive) {
            state = (BlockState)state.setValue(ACTIVE, false);
        }

        this.registerDefaultState(state);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ((BlockEntityType)this.electricBlockInstance.blockEntity.get()).create(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !this.electricBlockInstance.tickClientSide && level.isClientSide() ? null : ICEElectricBlockEntity::ticker;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SprayPaintable.DARK});
        if (ICEElectricBlockInstance.current.facingProperty != null) {
            builder.add(new Property[]{ICEElectricBlockInstance.current.facingProperty});
        }

        if (ICEElectricBlockInstance.current.canBeActive) {
            builder.add(new Property[]{ACTIVE});
        }

    }

    /** @deprecated */
    @Deprecated
    public BlockState rotate(BlockState state, Rotation rotation) {
        return this.electricBlockInstance.facingProperty == null ? state : (BlockState)state.setValue(this.electricBlockInstance.facingProperty, rotation.rotate((Direction)state.getValue(this.electricBlockInstance.facingProperty)));
    }

    /** @deprecated */
    @Deprecated
    public BlockState mirror(BlockState state, Mirror mirror) {
        return this.electricBlockInstance.facingProperty == null ? state : state.rotate(mirror.getRotation((Direction)state.getValue(this.electricBlockInstance.facingProperty)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext arg) {
        if (this.electricBlockInstance.facingProperty == null) {
            return this.defaultBlockState();
        } else {
            return this.electricBlockInstance.facingProperty == BlockStateProperties.HORIZONTAL_FACING ? (BlockState)this.defaultBlockState().setValue(this.electricBlockInstance.facingProperty, arg.getHorizontalDirection().getOpposite()) : (BlockState)this.defaultBlockState().setValue(this.electricBlockInstance.facingProperty, arg.getNearestLookingDirection().getOpposite());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
        boolean active = this.electricBlockInstance.canBeActive && (Boolean)state.getValue(ACTIVE);
        if (active || this.electricBlockInstance.canBurn) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ICEElectricBlockEntity) {
                ICEElectricBlockEntity electricBlockEntity = (ICEElectricBlockEntity)entity;
                double x = (double)pos.getX();
                double y = (double)pos.getY();
                double z = (double)pos.getZ();
                if (electricBlockEntity.isBurnt()) {
                    if (r.nextInt(10) == 0) {
                        level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + r.nextFloat(), r.nextFloat() * 0.7F + 0.3F, false);
                    }

                    for(int i = 0; i < 5; ++i) {
                        level.addParticle(ParticleTypes.SMOKE, x + (double)r.nextFloat(), y + 1.0, z + (double)r.nextFloat(), 0.0, 0.0, 0.0);
                        level.addParticle(ParticleTypes.SMOKE, x, y + 0.05 + (double)r.nextFloat(), z + (double)r.nextFloat(), 0.0, 0.0, 0.0);
                        level.addParticle(ParticleTypes.SMOKE, x + 1.0, y + 0.05 + (double)r.nextFloat(), z + (double)r.nextFloat(), 0.0, 0.0, 0.0);
                        level.addParticle(ParticleTypes.SMOKE, x + (double)r.nextFloat(), y + 0.05 + (double)r.nextFloat(), z, 0.0, 0.0, 0.0);
                        level.addParticle(ParticleTypes.SMOKE, x + (double)r.nextFloat(), y + 0.05 + (double)r.nextFloat(), z + 1.0, 0.0, 0.0, 0.0);
                        if (r.nextInt(5) == 0) {
                            level.addParticle(ParticleTypes.FLAME, x, y + 0.05 + (double)r.nextFloat(), z + (double)r.nextFloat(), 0.0, 0.0, 0.0);
                        }

                        if (r.nextInt(5) == 0) {
                            level.addParticle(ParticleTypes.FLAME, x + 1.0, y + 0.05 + (double)r.nextFloat(), z + (double)r.nextFloat(), 0.0, 0.0, 0.0);
                        }

                        if (r.nextInt(5) == 0) {
                            level.addParticle(ParticleTypes.FLAME, x + (double)r.nextFloat(), y + 0.05 + (double)r.nextFloat(), z, 0.0, 0.0, 0.0);
                        }

                        if (r.nextInt(5) == 0) {
                            level.addParticle(ParticleTypes.FLAME, x + (double)r.nextFloat(), y + 0.05 + (double)r.nextFloat(), z + 1.0, 0.0, 0.0, 0.0);
                        }
                    }

                    level.addParticle(ParticleTypes.FLAME, x + (double)r.nextFloat(), y + 1.1, z + (double)r.nextFloat(), 0.0, 0.0, 0.0);
                    level.addParticle(ParticleTypes.LARGE_SMOKE, x + 0.5, y + 1.0, z + 0.5, 0.0, 0.0, 0.0);
                } else if (active) {
                    electricBlockEntity.spawnActiveParticles(level, x, y, z, state, r);
                }
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
        super.onPlace(state, level, pos, state1, b);
        if (!level.isClientSide() && !state.is(state1.getBlock())) {
            ICEElectricBlockEntity.electricNetworkUpdated(level, pos);
        }

    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ICEElectricBlockEntity) {
            ((ICEElectricBlockEntity)blockEntity).onPlacedBy(entity, stack);
        }

    }

    /** @deprecated */
    @Deprecated
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
        if (!level.isClientSide() && !state.is(state1.getBlock())) {
            ICEElectricBlockEntity.electricNetworkUpdated(level, pos);
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ICEElectricBlockEntity) {
                ((ICEElectricBlockEntity)entity).onBroken(level, pos);
            }

            level.updateNeighbourForOutputSignal(pos, this);
        }

        super.onRemove(state, level, pos, state1, b);
    }

    /** @deprecated */
    @Deprecated
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos1, boolean b) {
        super.neighborChanged(state, level, pos, block, pos1, b);
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ICEElectricBlockEntity) {
                ((ICEElectricBlockEntity)entity).neighborChanged(pos1, block);
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ICEElectricBlockEntity electricBlockEntity) {
            if (electricBlockEntity.isBurnt()) {
                if (player.getItemInHand(hand).getItem() == FTBICItems.FUSE.item.get()) {
                    electricBlockEntity.setBurnt(false);
                    level.playSound(player, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
                    if (!level.isClientSide() && !player.isCreative()) {
                        player.getItemInHand(hand).shrink(1);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide());
                } else {
                    if (!level.isClientSide()) {
                        player.displayClientMessage(Component.translatable("ftbic.fuse_info"), true);
                    }

                    return InteractionResult.SUCCESS;
                }
            } else {
                return electricBlockEntity.rightClick(player, hand, hit);
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> list, TooltipFlag flag) {
        if (this.electricBlockInstance.wip) {
            list.add(Component.literal("WIP!").withStyle(ChatFormatting.RED));
        }

        if ((Double)this.electricBlockInstance.maxEnergyOutput.get() > 0.0) {
            list.add(Component.translatable("ftbic.energy_output", new Object[]{FTBICUtils.formatEnergy((Double)this.electricBlockInstance.maxEnergyOutput.get()).append("/t").withStyle(ChatFormatting.GRAY)}).withStyle(ChatFormatting.DARK_GRAY));
            Double feRatio = (Double)FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
            if (feRatio > 0.0) {
                list.add(Component.translatable("ftbic.zap_to_fe_conversion", new Object[]{FTBICConfig.ENERGY_FORMAT, feRatio}).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        if ((Double)this.electricBlockInstance.energyUsage.get() > 0.0) {
            if (this.electricBlockInstance.energyUsageIsPerTick) {
                list.add(Component.translatable("ftbic.energy_usage", new Object[]{FTBICUtils.formatEnergy((Double)this.electricBlockInstance.energyUsage.get()).append("/t").withStyle(ChatFormatting.GRAY)}).withStyle(ChatFormatting.DARK_GRAY));
            } else {
                list.add(Component.translatable("ftbic.energy_usage", new Object[]{FTBICUtils.formatEnergy((Double)this.electricBlockInstance.energyUsage.get()).withStyle(ChatFormatting.GRAY)}).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        if ((Double)this.electricBlockInstance.maxEnergyInput.get() > 0.0) {
            list.add(Component.translatable("ftbic.max_input", new Object[]{FTBICUtils.formatEnergy((Double)this.electricBlockInstance.maxEnergyInput.get()).append("/t").withStyle(ChatFormatting.GRAY)}).withStyle(ChatFormatting.DARK_GRAY));
        }

        if ((Double)this.electricBlockInstance.energyCapacity.get() > 0.0 && Screen.hasShiftDown()) {
            list.add(Component.translatable("ftbic.energy_capacity", new Object[]{FTBICUtils.formatEnergy((Double)this.electricBlockInstance.energyCapacity.get()).withStyle(ChatFormatting.GRAY)}).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", 10)) {
            CompoundTag tag = stack.getTag().getCompound("BlockEntityTag").copy();
            tag.remove("x");
            tag.remove("y");
            tag.remove("z");
            tag.remove("id");
            tag.remove("Inventory");
            tag.remove("Upgrades");
            tag.remove("Battery");
            tag.remove("ChargeBattery");
            tag.remove("PlacerId");
            tag.remove("PlacerName");
            Iterator var6 = tag.getAllKeys().iterator();

            while(var6.hasNext()) {
                String key = (String)var6.next();
                list.add(Component.literal("- " + key + ": " + tag.get(key)).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

    }

    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ServerPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ICEElectricBlockEntity) {
                ((ICEElectricBlockEntity)blockEntity).stepOn((ServerPlayer)entity);
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ICEElectricBlockEntity) {
                return ((ICEElectricBlockEntity)entity).getRedstoneOutputSignalEnergyStorage();
            }
        }

        return 0;
    }

    /** @deprecated */
    @Deprecated
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
}
