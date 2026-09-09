package com.jjyp.ftbiceg.capability;

import com.jjyp.ftbiceg.block.entity.AdvancedGeneratorBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Two-slot automation view matching the legacy/upstream generator shape:
 * slot 0 = fuel, slot 1 = chargeable battery.
 */
public final class GeneratorFuelHandler extends SnapshotJournal<GeneratorFuelHandler.Snapshot> implements ResourceHandler<ItemResource> {
    private final AdvancedGeneratorBlockEntity blockEntity;

    public GeneratorFuelHandler(AdvancedGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public ItemResource getResource(int index) {
        return switch (index) {
            case 0 -> ItemResource.of(blockEntity.getFuel());
            case 1 -> ItemResource.of(blockEntity.getChargeBattery());
            default -> ItemResource.EMPTY;
        };
    }

    @Override
    public long getAmountAsLong(int index) {
        return switch (index) {
            case 0 -> blockEntity.getFuel().getCount();
            case 1 -> blockEntity.getChargeBattery().getCount();
            default -> 0L;
        };
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        if (resource.isEmpty()) {
            return 0L;
        }
        return switch (index) {
            case 0 -> Math.min(64, resource.getMaxStackSize());
            case 1 -> 1L;
            default -> 0L;
        };
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        if (resource.isEmpty()) {
            return false;
        }
        ItemStack stack = resource.toStack(1);
        return switch (index) {
            case 0 -> blockEntity.isFuel(stack);
            case 1 -> blockEntity.isChargeBattery(stack);
            default -> false;
        };
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!isValid(index, resource) || amount <= 0) {
            return 0;
        }

        if (index == 1) {
            if (!blockEntity.getChargeBattery().isEmpty()) {
                return 0;
            }
            updateSnapshots(transaction);
            blockEntity.setChargeBattery(resource.toStack(1));
            return 1;
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
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }

        ItemStack existing = index == 0 ? blockEntity.getFuel()
            : index == 1 ? blockEntity.getChargeBattery()
            : ItemStack.EMPTY;
        if (existing.isEmpty() || !resource.matches(existing)) {
            return 0;
        }

        int extracted = Math.min(amount, existing.getCount());
        updateSnapshots(transaction);
        ItemStack remaining = existing.copy();
        remaining.shrink(extracted);
        if (index == 0) {
            blockEntity.setFuel(remaining.isEmpty() ? ItemStack.EMPTY : remaining);
        } else {
            blockEntity.setChargeBattery(remaining.isEmpty() ? ItemStack.EMPTY : remaining);
        }
        return extracted;
    }

    @Override
    protected Snapshot createSnapshot() {
        return new Snapshot(blockEntity.getFuel().copy(), blockEntity.getChargeBattery().copy());
    }

    @Override
    protected void revertToSnapshot(Snapshot snapshot) {
        blockEntity.setFuel(snapshot.fuel().copy());
        blockEntity.setChargeBattery(snapshot.battery().copy());
    }

    @Override
    protected void onRootCommit(Snapshot originalState) {
        blockEntity.setChanged();
    }

    record Snapshot(ItemStack fuel, ItemStack battery) {
    }
}
