package com.jjyp.ftbicec.block;

import com.jjyp.ftbicec.FTBICEC;
import com.jjyp.ftbicec.block.entity.ICEBlockEntities;
import com.jjyp.ftbicec.item.ICEItems;
import dev.ftb.mods.ftblibrary.snbt.config.DoubleValue;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ICEElectricBlockInstance {
    public static ICEElectricBlockInstance current;
    public final String id;
    public String name;
    public boolean advanced = false;
    public DirectionProperty facingProperty;
    public boolean noModel;
    public boolean canBeActive;
    public boolean canBurn;
    public final Supplier<Block> block;
    public final Supplier<BlockItem> item;
    public final Supplier<BlockEntityType<?>> blockEntity;
    public Supplier<Double> energyCapacity;
    public Supplier<Double> maxEnergyOutput;
    public Supplier<Double> energyUsage;
    public boolean energyUsageIsPerTick;
    public Supplier<Double> maxEnergyInput;
    public boolean wip;
    public int inputItemCount;
    public int outputItemCount;
    public boolean tickClientSide;

    public ICEElectricBlockInstance(String id, BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier, String modId) {
        this(id, blockEntitySupplier, modId, FTBICEC.TAB);
    }

    public ICEElectricBlockInstance(String id, BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier, String modId, CreativeModeTab tab) {
        this.facingProperty = BlockStateProperties.HORIZONTAL_FACING;
        this.noModel = false;
        this.canBeActive = true;
        this.canBurn = false;
        this.energyCapacity = () -> 0.0;
        this.maxEnergyOutput = () -> 0.0;
        this.energyUsage = () -> 0.0;
        this.energyUsageIsPerTick = false;
        this.maxEnergyInput = () -> 0.0;
        this.wip = false;
        this.inputItemCount = 0;
        this.outputItemCount = 0;
        this.tickClientSide = false;
        this.id = id;
        this.name = Arrays.stream(id.split("_"))
            .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
            .collect(Collectors.joining(" "));
        this.block = ICEBlocks.register(id, () -> {
            current = this;
            ICEElectricBlock value = new ICEElectricBlock(this);
            current = null;
            return value;
        }, modId);
        this.item = ICEItems.blockItem(id, block, modId, tab);
        this.blockEntity = ICEBlockEntities.register(id, blockEntitySupplier, block, modId);
    }

    public ICEElectricBlockInstance advanced() { advanced = true; return this; }
    public ICEElectricBlockInstance name(String value) { name = value; return this; }
    public ICEElectricBlockInstance noRotation() { facingProperty = null; return this; }
    public ICEElectricBlockInstance rotate3D() { facingProperty = BlockStateProperties.FACING; return this; }
    public ICEElectricBlockInstance noModel() { noModel = true; return this; }
    public ICEElectricBlockInstance cantBeActive() { canBeActive = false; return this; }
    public ICEElectricBlockInstance canBurn() { canBurn = true; return this; }
    public ICEElectricBlockInstance maxEnergyOutput(Supplier<Double> value) { maxEnergyOutput = value; return this; }
    public ICEElectricBlockInstance maxEnergyOutput(DoubleValue value) { Objects.requireNonNull(value); maxEnergyOutput = value::get; return this; }
    public ICEElectricBlockInstance energyCapacity(Supplier<Double> value) { energyCapacity = value; return this; }
    public ICEElectricBlockInstance energyCapacity(DoubleValue value) { Objects.requireNonNull(value); energyCapacity = value::get; return this; }
    public ICEElectricBlockInstance energyUsage(Supplier<Double> value) { energyUsage = value; return this; }
    public ICEElectricBlockInstance energyUsage(DoubleValue value) { Objects.requireNonNull(value); energyUsage = value::get; return this; }
    public ICEElectricBlockInstance maxEnergyInput(Supplier<Double> value) { maxEnergyInput = value; return this; }
    public ICEElectricBlockInstance maxEnergyInput(DoubleValue value) { Objects.requireNonNull(value); maxEnergyInput = value::get; return this; }
    public ICEElectricBlockInstance wip() { wip = true; return this; }
    public ICEElectricBlockInstance energyUsageIsntPerTick() { energyUsageIsPerTick = false; return this; }
    public ICEElectricBlockInstance io(int inItems, int outItems) { inputItemCount = inItems; outputItemCount = outItems; return this; }
    public ICEElectricBlockInstance tickClientSide() { tickClientSide = true; return this; }
}
