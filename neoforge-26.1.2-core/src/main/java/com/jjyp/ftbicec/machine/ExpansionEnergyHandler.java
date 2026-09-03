package com.jjyp.ftbicec.machine;

import dev.ftb.mods.ftbic.util.ZapFEConversion;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class ExpansionEnergyHandler extends SnapshotJournal<Double> implements EnergyHandler {
    private final ExpansionGeneratorBlockEntity blockEntity;

    public ExpansionEnergyHandler(ExpansionGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public long getAmountAsLong() {
        double fe = blockEntity.getEnergy() * ZapFEConversion.rate();
        return fe >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) fe;
    }

    @Override
    public long getCapacityAsLong() {
        double fe = blockEntity.getEnergyCapacity() * ZapFEConversion.rate();
        return fe >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) fe;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (amount <= 0 || blockEntity.getEnergy() <= 0D) {
            return 0;
        }
        double asZaps = ZapFEConversion.feToZapsFloor(amount);
        double extracted = Math.min(Math.min(asZaps, blockEntity.getMaxOutputEnergy()), blockEntity.getEnergy());
        if (extracted <= 0D) {
            return 0;
        }
        updateSnapshots(transaction);
        blockEntity.setEnergyRaw(blockEntity.getEnergy() - extracted);
        return ZapFEConversion.zapsToFEFloor(extracted);
    }

    @Override
    protected Double createSnapshot() {
        return blockEntity.getEnergy();
    }

    @Override
    protected void revertToSnapshot(Double snapshot) {
        blockEntity.setEnergyRaw(snapshot);
    }

    @Override
    protected void onRootCommit(Double originalState) {
        blockEntity.setChanged();
    }
}
