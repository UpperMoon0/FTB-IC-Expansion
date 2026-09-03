package com.jjyp.ftbicec.block.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface ICEBlockEntities {
    DeferredRegister<BlockEntityType<?>> ICEC_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "ftbicec");
    DeferredRegister<BlockEntityType<?>> ICEG_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "ftbiceg");

    static Supplier<BlockEntityType<?>> register(String id, BlockEntityType.BlockEntitySupplier<?> supplier, Supplier<Block> block, String modId) {
        return registry(modId).register(id, () -> BlockEntityType.Builder.of(supplier, new Block[]{block.get()}).build(null));
    }

    private static DeferredRegister<BlockEntityType<?>> registry(String modId) {
        return switch (modId) {
            case "ftbicec" -> ICEC_REGISTRY;
            case "ftbiceg" -> ICEG_REGISTRY;
            default -> throw new IllegalArgumentException("Unsupported FTB IC Expansion mod id: " + modId);
        };
    }
}
