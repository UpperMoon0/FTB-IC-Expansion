package com.jjyp.ftbicec.registry;

import com.jjyp.ftbicec.FTBICEC;
import com.jjyp.ftbicec.block.LargeBlastFurnaceBlock;
import com.jjyp.ftbicec.block.entity.LargeBlastFurnaceBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class ICECRegistries {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FTBICEC.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FTBICEC.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FTBICEC.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FTBICEC.MODID);

    public static final DeferredBlock<Block> FIREBRICKS = BLOCKS.register(
        "firebricks",
        name -> new Block(blockProperties(name).sound(SoundType.STONE).strength(2F, 6F).requiresCorrectToolForDrops())
    );
    public static final DeferredBlock<LargeBlastFurnaceBlock> LARGE_BLAST_FURNACE = BLOCKS.register(
        "large_blast_furnace",
        name -> new LargeBlastFurnaceBlock(blockProperties(name).sound(SoundType.STONE).strength(2F, 6F).requiresCorrectToolForDrops())
    );

    public static final DeferredItem<BlockItem> FIREBRICKS_ITEM = ITEMS.register(
        "firebricks", name -> new BlockItem(FIREBRICKS.get(), itemProperties(name))
    );
    public static final DeferredItem<BlockItem> LARGE_BLAST_FURNACE_ITEM = ITEMS.register(
        "large_blast_furnace", name -> new BlockItem(LARGE_BLAST_FURNACE.get(), itemProperties(name))
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> LARGE_BLAST_FURNACE_BE = BLOCK_ENTITIES.register(
        "large_blast_furnace",
        () -> new BlockEntityType<>(LargeBlastFurnaceBlockEntity::new, Set.of(LARGE_BLAST_FURNACE.get()))
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(
        "ftbicec",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ftbicec"))
            .icon(() -> new ItemStack(LARGE_BLAST_FURNACE_ITEM.get()))
            .displayItems((parameters, output) -> {
                output.accept(FIREBRICKS_ITEM.get());
                output.accept(LARGE_BLAST_FURNACE_ITEM.get());
            })
            .build()
    );

    private static BlockBehaviour.Properties blockProperties(Identifier name) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, name));
    }

    private static Item.Properties itemProperties(Identifier name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name));
    }

    private ICECRegistries() {
    }
}
