package com.jjyp.ftbiceg.menu;

import com.jjyp.ftbiceg.block.entity.AdvancedGeothermalGeneratorBlockEntity;
import com.jjyp.ftbiceg.registry.ICEGRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class AdvancedGeothermalGeneratorMenu extends AbstractContainerMenu {
    private static final int MACHINE_SLOTS = 1;
    private static final int BATTERY_SLOT = 0;
    private static final int DATA_COUNT = 4;

    private final Container machine;
    private final ContainerData data;

    public AdvancedGeothermalGeneratorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(MACHINE_SLOTS), new SimpleContainerData(DATA_COUNT));
    }

    public AdvancedGeothermalGeneratorMenu(int containerId, Inventory inventory,
                                           AdvancedGeothermalGeneratorBlockEntity generator) {
        this(containerId, inventory, createMachineContainer(generator), createData(generator));
    }

    private AdvancedGeothermalGeneratorMenu(int containerId, Inventory inventory, Container machine, ContainerData data) {
        super(ICEGRegistries.ADVANCED_GEOTHERMAL_GENERATOR_MENU.get(), containerId);
        checkContainerSize(machine, MACHINE_SLOTS);
        checkContainerDataCount(data, DATA_COUNT);
        this.machine = machine;
        this.data = data;

        addSlot(new Slot(machine, BATTERY_SLOT, 62, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return machine.canPlaceItem(BATTERY_SLOT, stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addStandardInventorySlots(inventory, 8, 84);
        addDataSlots(data);
    }

    public int getEnergy() {
        return data.get(0);
    }

    public int getEnergyCapacity() {
        return data.get(1);
    }

    public int getFluidAmount() {
        return data.get(2);
    }

    public int getTankCapacity() {
        return data.get(3);
    }

    @Override
    public boolean stillValid(Player player) {
        return machine.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(stack, MACHINE_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (slots.get(BATTERY_SLOT).mayPlace(stack)) {
            if (!moveItemStackTo(stack, BATTERY_SLOT, BATTERY_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    private static Container createMachineContainer(AdvancedGeothermalGeneratorBlockEntity generator) {
        return new Container() {
            @Override
            public int getContainerSize() {
                return MACHINE_SLOTS;
            }

            @Override
            public boolean isEmpty() {
                return generator.getChargeBattery().isEmpty();
            }

            @Override
            public ItemStack getItem(int slot) {
                return slot == BATTERY_SLOT ? generator.getChargeBattery() : ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                ItemStack current = getItem(slot);
                if (current.isEmpty() || amount <= 0) {
                    return ItemStack.EMPTY;
                }
                ItemStack removed = current.split(Math.min(amount, current.getCount()));
                if (current.isEmpty()) {
                    setItem(slot, ItemStack.EMPTY);
                } else {
                    setChanged();
                }
                return removed;
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                ItemStack current = getItem(slot);
                if (current.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                ItemStack removed = current.copy();
                setItem(slot, ItemStack.EMPTY);
                return removed;
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if (slot != BATTERY_SLOT) {
                    return;
                }
                generator.setChargeBattery(stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
                setChanged();
            }

            @Override
            public void setChanged() {
                generator.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(generator, player);
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return slot == BATTERY_SLOT && generator.isChargeBattery(stack);
            }

            @Override
            public void clearContent() {
                generator.setChargeBattery(ItemStack.EMPTY);
                setChanged();
            }
        };
    }

    private static ContainerData createData(AdvancedGeothermalGeneratorBlockEntity generator) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> clampToInt(generator.getEnergy());
                    case 1 -> clampToInt(generator.getEnergyCapacity());
                    case 2 -> generator.getFluidAmount();
                    case 3 -> generator.getTankCapacity();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    private static int clampToInt(double value) {
        return (int) Math.max(0L, Math.min(Integer.MAX_VALUE, Math.round(value)));
    }
}
