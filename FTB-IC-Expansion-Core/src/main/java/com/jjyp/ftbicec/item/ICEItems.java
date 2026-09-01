package com.jjyp.ftbicec.item;

import com.jjyp.ftbicec.FTBICEC;
import com.jjyp.ftbicec.block.ICEBlocks;
import com.jjyp.ftbiceg.FTBICEG;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
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

    List<DeferredRegister<?>> REGISTERS_LIST = List.of(
            ICEG_REGISTRY,
            ICEOP_REGISTRY,
            ICEC_REGISTRY
    );

    Supplier<BlockItem> FIREBRICKS = blockItem("firebricks", ICEBlocks.FIREBRICKS, "ftbicec");
    Supplier<BlockItem> LARGE_BLAST_FURNACE = blockItem("large_blast_furnace", ICEBlocks.LARGE_BLAST_FURNACE, "ftbicec");
    static Supplier<Item> register(String id, Supplier<Item> item, String mod_id) {
        System.out.println("Registering item: " + id + "; mod id: " + mod_id);
        DeferredRegister<Item> REGISTRY;

        switch (mod_id)
        {
            case "ftbiceg": {
                if (ModList.get().isLoaded("ftbiceg")) {
                    REGISTRY = ICEG_REGISTRY;
                    break;
                }
            }
            case "ftbiceop": {
                if (ModList.get().isLoaded("ftbiceop")) {
                    REGISTRY = ICEOP_REGISTRY;
                    break;
                }
            }
            default: {
                REGISTRY = ICEC_REGISTRY;
                break;
            }
        }

        return REGISTRY.register(id, item);
    }

    static Supplier<BlockItem> blockItem(String id, Supplier<Block> sup, String mod_id) {
        System.out.println("Registering blockItem: " + id + "; mod id: " + mod_id);

        DeferredRegister<Item> REGISTRY;
        CreativeModeTab TAB;

        switch (mod_id)
        {
            case "ftbiceg": {
                if (ModList.get().isLoaded("ftbiceg")) {
                    REGISTRY = ICEG_REGISTRY;
                    TAB = FTBICEG.TAB;
                    break;
                }
            }
            case "ftbiceop": {
                if (ModList.get().isLoaded("ftbiceop")) {
                    REGISTRY = ICEOP_REGISTRY;
                    TAB = FTBICEC.TAB;
                    break;
                }
            }
            default: {
                REGISTRY = ICEC_REGISTRY;
                TAB = FTBICEC.TAB;
                break;
            }
        }

        return REGISTRY.register(id, () -> new BlockItem(sup.get(), (new Item.Properties()).tab(TAB)));
    }
}
