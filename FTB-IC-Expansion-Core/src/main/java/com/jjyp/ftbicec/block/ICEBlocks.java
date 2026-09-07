package com.jjyp.ftbicec.block;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface ICEBlocks {
    DeferredRegister<Block> ICEC_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "ftbicec");
    DeferredRegister<Block> ICEG_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "ftbiceg");

    static Supplier<Block> register(String id, Supplier<Block> block, String modId) {
        return registry(modId).register(id, block);
    }

    private static DeferredRegister<Block> registry(String modId) {
        return switch (modId) {
            case "ftbicec" -> ICEC_REGISTRY;
            case "ftbiceg" -> ICEG_REGISTRY;
            default -> throw new IllegalArgumentException("Unsupported FTB IC Expansion mod id: " + modId);
        };
    }
}
