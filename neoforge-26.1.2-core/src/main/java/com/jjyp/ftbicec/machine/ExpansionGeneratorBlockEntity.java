package com.jjyp.ftbicec.machine;

import com.jjyp.ftbicec.compat.ftbic.FTBICGeneratorEnergyBridge;
import dev.ftb.mods.ftbic.util.ZapEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.DoubleSupplier;

/**
 * Expansion-owned generator state and lifecycle.
 *
 * <p>FTBIC's cable traversal/output implementation is intentionally isolated in
 * {@link FTBICGeneratorEnergyBridge}. Upstream does not expose an addon-safe generator superclass:
 * its generator constructor is coupled to FTBIC's own registry descriptor. Keeping that dependency
 * behind a version-specific bridge prevents upstream-private transport details from leaking into
 * expansion gameplay code.</p>
 */
public abstract class ExpansionGeneratorBlockEntity extends BlockEntity implements ZapEnergyHandler {
    private final DoubleSupplier capacitySupplier;
    private final DoubleSupplier transferRateSupplier;
    private final FTBICGeneratorEnergyBridge energyBridge;

    protected double energy;
    private boolean activeThisTick;

    protected ExpansionGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                            DoubleSupplier capacitySupplier, DoubleSupplier transferRateSupplier) {
        super(type, pos, state);
        this.capacitySupplier = capacitySupplier;
        this.transferRateSupplier = transferRateSupplier;
        this.energyBridge = new FTBICGeneratorEnergyBridge(this);
    }

    public final void serverTick() {
        if (level == null || level.isClientSide()) {
            return;
        }

        double capacity = getEnergyCapacity();
        if (energy > capacity) {
            energy = capacity;
            setChanged();
        }

        handleGeneration();
        energyBridge.pushEnergy();
        updateActiveState();
    }

    protected abstract void handleGeneration();

    protected final void markActive() {
        activeThisTick = true;
    }

    protected final double storedEnergy() {
        return energy;
    }

    protected final double addEnergy(double amount) {
        if (amount <= 0D) {
            return 0D;
        }
        double accepted = Math.min(amount, Math.max(0D, getEnergyCapacity() - energy));
        if (accepted > 0D) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }

    /**
     * Called only by the FTBIC compatibility bridge after a downstream consumer accepted power.
     */
    public final double consumeOutputEnergy(double amount) {
        if (amount <= 0D || energy <= 0D) {
            return 0D;
        }
        double consumed = Math.min(amount, energy);
        energy -= consumed;
        markActive();
        setChanged();
        return consumed;
    }

    private void updateActiveState() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        if (state.hasProperty(ExpansionGeneratorBlock.ACTIVE)
            && state.getValue(ExpansionGeneratorBlock.ACTIVE) != activeThisTick) {
            level.setBlock(worldPosition, state.setValue(ExpansionGeneratorBlock.ACTIVE, activeThisTick), 3);
        }
        activeThisTick = false;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (energy > 0D) {
            output.putDouble("Energy", energy);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = Math.min(input.getDoubleOr("Energy", 0D), getEnergyCapacity());
    }

    @Override
    public double getEnergyCapacity() {
        return Math.max(1D, capacitySupplier.getAsDouble());
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergyRaw(double value) {
        energy = Math.max(0D, Math.min(value, getEnergyCapacity()));
    }

    @Override
    public void energyChanged(double previousEnergy) {
        setChanged();
    }

    @Override
    public double getMaxInputEnergy() {
        return 0D;
    }

    @Override
    public double getMaxOutputEnergy() {
        return Math.max(1D, transferRateSupplier.getAsDouble());
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        return false;
    }

    public InteractionResult interactWithItem(Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public InteractionResult interactWithoutItem(Player player, BlockHitResult hit) {
        if (level != null && !level.isClientSide()) {
            player.sendSystemMessage(Component.literal(String.format("Energy: %.0f / %.0f Zaps", energy, getEnergyCapacity())));
        }
        return InteractionResult.SUCCESS;
    }
}
