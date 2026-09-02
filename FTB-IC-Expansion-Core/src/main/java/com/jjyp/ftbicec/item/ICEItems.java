package com.jjyp.ftbicec.item;

import com.jjyp.ftbicec.FTBICEC;
import com.jjyp.ftbicec.block.ICEBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public interface ICEItems {
    DeferredRegister<Item> ICEC_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "ftbicec");
    DeferredRegister<Item> ICEG_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "ftbiceg");
    DeferredRegister<Item> ICEOP_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "ftbiceop");

    List<DeferredRegister<?>> REGISTERS_LIST = List.of(ICEG_REGISTRY, ICEOP_REGISTRY, ICEC_REGISTRY);

    Supplier<BlockItem> FIREBRICKS = blockItem("firebricks", ICEBlocks.FIREBRICKS, "ftbicec", FTBICEC.TAB);
    Supplier<BlockItem> LARGE_BLAST_FURNACE = blockItem("large_blast_furnace", ICEBlocks.LARGE_BLAST_FURNACE, "ftbicec", FTBICEC.TAB);

    static Supplier<Item> register(String id, Supplier<Item> item, String modId) {
        return registry(modId).register(id, item);
    }

    static Supplier<BlockItem> blockItem(String id, Supplier<Block> block, String modId, CreativeModeTab tab) {
        return registry(modId).register(id, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    private static DeferredRegister<Item> registry(String modId) {
        switch (modId) {
            case "ftbiceg":
                if (ModList.get().isLoaded("ftbiceg")) return ICEG_REGISTRY;
                break;
            case "ftbiceop":
                if (ModList.get().isLoaded("ftbiceop")) return ICEOP_REGISTRY;
                break;
            default:
                break;
        }
        return ICEC_REGISTRY;
    }
}
