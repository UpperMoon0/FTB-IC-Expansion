package com.jjyp.ftbiceg.capability;

import com.jjyp.ftbiceg.block.entity.AdvancedGeothermalGeneratorBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class AdvancedGeothermalTankHandler extends SnapshotJournal<Integer> implements ResourceHandler<FluidResource> {
    private final AdvancedGeothermalGeneratorBlockEntity blockEntity;

    public AdvancedGeothermalTankHandler(AdvancedGeothermalGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        return index == 0 && blockEntity.getFluidAmount() > 0 ? FluidResource.of(Fluids.LAVA) : FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? blockEntity.getFluidAmount() : 0L;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return index == 0 ? blockEntity.getTankCapacity() : 0L;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return index == 0 && (resource.isEmpty() || resource.getFluid() == Fluids.LAVA);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || resource.isEmpty() || resource.getFluid() != Fluids.LAVA || amount <= 0) {
            return 0;
        }
        int inserted = Math.min(amount, blockEntity.getTankCapacity() - blockEntity.getFluidAmount());
        if (inserted <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        blockEntity.setFluidAmount(blockEntity.getFluidAmount() + inserted);
        return inserted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return 0;
    }

    @Override
    protected Integer createSnapshot() {
        return blockEntity.getFluidAmount();
    }

    @Override
    protected void revertToSnapshot(Integer snapshot) {
        blockEntity.setFluidAmount(snapshot);
    }

    @Override
    protected void onRootCommit(Integer originalState) {
        blockEntity.setChanged();
    }
}
