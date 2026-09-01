package com.jjyp.ftbicec.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public interface ICEBlocks {
    DeferredRegister<Block> ICEC_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "ftbicec");
    DeferredRegister<Block> ICEG_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "ftbiceg");
    DeferredRegister<Block> ICEOP_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "ftbiceop");
    List<DeferredRegister<?>> REGISTERS_LIST = List.of(
            ICEG_REGISTRY,
            ICEOP_REGISTRY,
            ICEC_REGISTRY
    );
    Supplier<Block> FIREBRICKS = ICEC_REGISTRY.register("firebricks", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2F, 6F).requiresCorrectToolForDrops()));
    Supplier<Block> LARGE_BLAST_FURNACE = ICEC_REGISTRY.register("large_blast_furnace", LargeBlastFurnaceBlock::new);
    static Supplier<Block> register(String id, Supplier<Block> block, String mod_id) {
        System.out.println("Registering block: " + id + "; mod id: " + mod_id);
        DeferredRegister<Block> REGISTRY;

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

        return REGISTRY.register(id, block);
    }
}
