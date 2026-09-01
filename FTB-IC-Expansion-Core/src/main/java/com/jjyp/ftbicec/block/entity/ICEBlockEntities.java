package com.jjyp.ftbicec.block.entity;

import com.jjyp.ftbicec.block.ICEBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public interface ICEBlockEntities {
    DeferredRegister<BlockEntityType<?>> ICEG_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "ftbiceg");
    DeferredRegister<BlockEntityType<?>> ICEOP_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "ftbiceop");
    DeferredRegister<BlockEntityType<?>> ICEC_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "ftbicec");
    List<DeferredRegister<?>> REGISTERS_LIST = List.of(
        ICEG_REGISTRY,
        ICEOP_REGISTRY,
        ICEC_REGISTRY
    );
    Supplier<BlockEntityType<?>> LARGE_BLAST_FURNACE = register("large_blast_furnace", LargeBlastFurnaceBlockEntity::new, ICEBlocks.LARGE_BLAST_FURNACE, "ftbicec");
    static Supplier<BlockEntityType<?>> register(String id, BlockEntityType.BlockEntitySupplier<?> supplier, Supplier<Block> block, String mod_id) {
        System.out.println("Registering block entity: " + id + "; mod id: " + mod_id);
        DeferredRegister<BlockEntityType<?>> REGISTRY;

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

        return REGISTRY.register(id, () -> BlockEntityType.Builder.of(supplier, new Block[]{block.get()}).build(null));
    }
}
