package com.jjyp.ftbiceg.capability;

import com.jjyp.ftbiceg.block.entity.AdvancedGeneratorBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class GeneratorFuelHandler extends SnapshotJournal<ItemStack> implements ResourceHandler<ItemResource> {
    private final AdvancedGeneratorBlockEntity blockEntity;

    public GeneratorFuelHandler(AdvancedGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ItemResource getResource(int index) {
        return index == 0 ? ItemResource.of(blockEntity.getFuel()) : ItemResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? blockEntity.getFuel().getCount() : 0L;
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return index == 0 && !resource.isEmpty() ? Math.min(64, resource.getMaxStackSize()) : 0L;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return index == 0 && !resource.isEmpty() && blockEntity.isFuel(resource.toStack(1));
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!isValid(index, resource) || amount <= 0) {
            return 0;
        }
        ItemStack existing = blockEntity.getFuel();
        if (!existing.isEmpty() && !resource.matches(existing)) {
            return 0;
        }
        int limit = (int) getCapacityAsLong(index, resource);
        int room = limit - existing.getCount();
        int inserted = Math.min(amount, room);
        if (inserted <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        if (existing.isEmpty()) {
            blockEntity.setFuel(resource.toStack(inserted));
        } else {
            ItemStack grown = existing.copy();
            grown.grow(inserted);
            blockEntity.setFuel(grown);
        }
        return inserted;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || resource.isEmpty() || amount <= 0) {
            return 0;
        }
        ItemStack existing = blockEntity.getFuel();
        if (existing.isEmpty() || !resource.matches(existing)) {
            return 0;
        }
        int extracted = Math.min(amount, existing.getCount());
        updateSnapshots(transaction);
        ItemStack remaining = existing.copy();
        remaining.shrink(extracted);
        blockEntity.setFuel(remaining.isEmpty() ? ItemStack.EMPTY : remaining);
        return extracted;
    }

    @Override
    protected ItemStack createSnapshot() {
        return blockEntity.getFuel().copy();
    }

    @Override
    protected void revertToSnapshot(ItemStack snapshot) {
        blockEntity.setFuel(snapshot.copy());
    }

    @Override
    protected void onRootCommit(ItemStack originalState) {
        blockEntity.setChanged();
    }
}
