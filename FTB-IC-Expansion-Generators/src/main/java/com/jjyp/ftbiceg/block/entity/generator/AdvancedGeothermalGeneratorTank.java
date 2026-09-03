package com.jjyp.ftbiceg.block.entity.generator;

import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.NotNull;

public class AdvancedGeothermalGeneratorTank implements IFluidHandler, IFluidTank {
    private static final int FLUID_CAPACITY = 24000;
    public final AdvancedGeothermalGeneratorBlockEntity generator;

    public AdvancedGeothermalGeneratorTank(AdvancedGeothermalGeneratorBlockEntity generator) {
        this.generator = generator;
    }

    public @NotNull FluidStack getFluid() {
        return this.generator.fluidAmount == 0 ? FluidStack.EMPTY : new FluidStack(Fluids.LAVA, this.generator.fluidAmount);
    }

    public int getFluidAmount() {
        return this.generator.fluidAmount;
    }

    public int getCapacity() {
        return FLUID_CAPACITY;
    }

    public boolean isFluidValid(FluidStack fluidStack) {
        return fluidStack.getFluid() == Fluids.LAVA;
    }

    public int getTanks() {
        return 1;
    }

    public @NotNull FluidStack getFluidInTank(int tank) {
        return this.getFluid();
    }

    public int getTankCapacity(int tank) {
        return FLUID_CAPACITY;
    }

    public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack) {
        return this.isFluidValid(fluidStack);
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !this.isFluidValid(resource)) {
            return 0;
        }
        int filled = Math.min(this.getCapacity() - this.generator.fluidAmount, resource.getAmount());
        if (filled > 0 && !action.simulate()) {
            this.generator.fluidAmount += filled;
            this.generator.setChanged();
        }
        return filled;
    }

    public @NotNull FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }
}
