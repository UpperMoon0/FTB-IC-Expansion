package com.jjyp.ftbicec.item;

import com.jjyp.ftbicec.FTBICEC;
import com.jjyp.ftbicec.block.ICEBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface ICEItems {
    DeferredRegister<Item> ICEC_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "ftbicec");
    DeferredRegister<Item> ICEG_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "ftbiceg");

    Supplier<BlockItem> FIREBRICKS = blockItem("firebricks", ICEBlocks.FIREBRICKS, "ftbicec", FTBICEC.TAB);

    static Supplier<Item> register(String id, Supplier<Item> item, String modId) {
        return registry(modId).register(id, item);
    }

    static Supplier<BlockItem> blockItem(String id, Supplier<Block> block, String modId, CreativeModeTab tab) {
        return registry(modId).register(id, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    private static DeferredRegister<Item> registry(String modId) {
        return switch (modId) {
            case "ftbicec" -> ICEC_REGISTRY;
            case "ftbiceg" -> ICEG_REGISTRY;
            default -> throw new IllegalArgumentException("Unsupported FTB IC Expansion mod id: " + modId);
        };
    }
}
