package com.jjyp.ftbiceg.capability;

import com.jjyp.ftbicec.machine.ExpansionGeneratorBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/** Single charge-battery automation slot for generators without an item fuel slot. */
public final class GeneratorBatteryHandler extends SnapshotJournal<ItemStack> implements ResourceHandler<ItemResource> {
    private final ExpansionGeneratorBlockEntity blockEntity;

    public GeneratorBatteryHandler(ExpansionGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ItemResource getResource(int index) {
        return index == 0 ? ItemResource.of(blockEntity.getChargeBattery()) : ItemResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? blockEntity.getChargeBattery().getCount() : 0L;
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return index == 0 && !resource.isEmpty() ? 1L : 0L;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return index == 0 && !resource.isEmpty() && blockEntity.isChargeBattery(resource.toStack(1));
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!isValid(index, resource) || amount <= 0 || !blockEntity.getChargeBattery().isEmpty()) {
            return 0;
        }
        updateSnapshots(transaction);
        blockEntity.setChargeBattery(resource.toStack(1));
        return 1;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || resource.isEmpty() || amount <= 0) {
            return 0;
        }
        ItemStack existing = blockEntity.getChargeBattery();
        if (existing.isEmpty() || !resource.matches(existing)) {
            return 0;
        }
        updateSnapshots(transaction);
        blockEntity.setChargeBattery(ItemStack.EMPTY);
        return 1;
    }

    @Override
    protected ItemStack createSnapshot() {
        return blockEntity.getChargeBattery().copy();
    }

    @Override
    protected void revertToSnapshot(ItemStack snapshot) {
        blockEntity.setChargeBattery(snapshot.copy());
    }

    @Override
    protected void onRootCommit(ItemStack originalState) {
        blockEntity.setChanged();
    }
}
