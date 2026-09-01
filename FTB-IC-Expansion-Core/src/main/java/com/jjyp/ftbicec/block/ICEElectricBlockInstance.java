package com.jjyp.ftbicec.block;

import com.jjyp.ftbicec.block.entity.ICEBlockEntities;
import com.jjyp.ftbicec.item.ICEItems;
import dev.ftb.mods.ftblibrary.snbt.config.DoubleValue;
import net.minecraft.world.item.BlockItem;
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

    public ICEElectricBlockInstance(String i, BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier, String mod_id) {
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
        this.id = i;
        this.name = Arrays.stream(this.id.split("_")).map((s) -> {
            char var10000 = Character.toUpperCase(s.charAt(0));
            return "" + var10000 + s.substring(1);
        }).collect(Collectors.joining(" "));
        this.block = ICEBlocks.register(id, () -> {
            current = this;
            ICEElectricBlock b = new ICEElectricBlock(this);
            current = null;
            return b;
        }, mod_id);
        this.item = ICEItems.blockItem(this.id, this.block, mod_id);
        this.blockEntity = ICEBlockEntities.register(this.id, blockEntitySupplier, this.block, mod_id);
    }

    public ICEElectricBlockInstance advanced() {
        this.advanced = true;
        return this;
    }

    public ICEElectricBlockInstance name(String n) {
        this.name = n;
        return this;
    }

    public ICEElectricBlockInstance noRotation() {
        this.facingProperty = null;
        return this;
    }

    public ICEElectricBlockInstance rotate3D() {
        this.facingProperty = BlockStateProperties.FACING;
        return this;
    }

    public ICEElectricBlockInstance noModel() {
        this.noModel = true;
        return this;
    }

    public ICEElectricBlockInstance cantBeActive() {
        this.canBeActive = false;
        return this;
    }

    public ICEElectricBlockInstance canBurn() {
        this.canBurn = true;
        return this;
    }

    public ICEElectricBlockInstance maxEnergyOutput(Supplier<Double> d) {
        this.maxEnergyOutput = d;
        return this;
    }

    public ICEElectricBlockInstance maxEnergyOutput(DoubleValue d) {
        Objects.requireNonNull(d);
        this.maxEnergyOutput = d::get;
        return this;
    }

    public ICEElectricBlockInstance energyCapacity(Supplier<Double> d) {
        this.energyCapacity = d;
        return this;
    }

    public ICEElectricBlockInstance energyCapacity(DoubleValue d) {
        Objects.requireNonNull(d);
        this.energyCapacity = d::get;
        return this;
    }

    public ICEElectricBlockInstance energyUsage(Supplier<Double> d) {
        this.energyUsage = d;
        return this;
    }

    public ICEElectricBlockInstance energyUsage(DoubleValue d) {
        Objects.requireNonNull(d);
        this.energyUsage = d::get;
        return this;
    }

    public ICEElectricBlockInstance maxEnergyInput(Supplier<Double> d) {
        this.maxEnergyInput = d;
        return this;
    }

    public ICEElectricBlockInstance maxEnergyInput(DoubleValue d) {
        Objects.requireNonNull(d);
        this.maxEnergyInput = d::get;
        return this;
    }

    public ICEElectricBlockInstance wip() {
        this.wip = true;
        return this;
    }

    public ICEElectricBlockInstance energyUsageIsntPerTick() {
        this.energyUsageIsPerTick = false;
        return this;
    }

    public ICEElectricBlockInstance io(int inItems, int outItems) {
        this.inputItemCount = inItems;
        this.outputItemCount = outItems;
        return this;
    }

    public ICEElectricBlockInstance tickClientSide() {
        this.tickClientSide = true;
        return this;
    }
}