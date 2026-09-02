package com.jjyp.ftbiceg.registry;

import com.jjyp.ftbiceg.FTBICEG;
import com.jjyp.ftbiceg.block.AdvancedGeneratorBlock;
import com.jjyp.ftbiceg.block.AdvancedGeothermalGeneratorBlock;
import com.jjyp.ftbiceg.block.entity.AdvancedGeneratorBlockEntity;
import com.jjyp.ftbiceg.block.entity.AdvancedGeothermalGeneratorBlockEntity;
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

public final class ICEGRegistries {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FTBICEG.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FTBICEG.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FTBICEG.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FTBICEG.MODID);

    public static final DeferredBlock<AdvancedGeneratorBlock> ADVANCED_GENERATOR = BLOCKS.register(
        "advanced_generator",
        name -> new AdvancedGeneratorBlock(blockProperties(name))
    );
    public static final DeferredBlock<AdvancedGeothermalGeneratorBlock> ADVANCED_GEOTHERMAL_GENERATOR = BLOCKS.register(
        "advanced_geothermal_generator",
        name -> new AdvancedGeothermalGeneratorBlock(blockProperties(name))
    );

    public static final DeferredItem<BlockItem> ADVANCED_GENERATOR_ITEM = ITEMS.register(
        "advanced_generator",
        name -> new BlockItem(ADVANCED_GENERATOR.get(), itemProperties(name))
    );
    public static final DeferredItem<BlockItem> ADVANCED_GEOTHERMAL_GENERATOR_ITEM = ITEMS.register(
        "advanced_geothermal_generator",
        name -> new BlockItem(ADVANCED_GEOTHERMAL_GENERATOR.get(), itemProperties(name))
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ADVANCED_GENERATOR_BE = BLOCK_ENTITIES.register(
        "advanced_generator",
        () -> new BlockEntityType<>(AdvancedGeneratorBlockEntity::new, Set.of(ADVANCED_GENERATOR.get()))
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ADVANCED_GEOTHERMAL_GENERATOR_BE = BLOCK_ENTITIES.register(
        "advanced_geothermal_generator",
        () -> new BlockEntityType<>(AdvancedGeothermalGeneratorBlockEntity::new, Set.of(ADVANCED_GEOTHERMAL_GENERATOR.get()))
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(
        "ftbiceg",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ftbiceg"))
            .icon(() -> new ItemStack(ADVANCED_GENERATOR_ITEM.get()))
            .displayItems((parameters, output) -> {
                output.accept(ADVANCED_GENERATOR_ITEM.get());
                output.accept(ADVANCED_GEOTHERMAL_GENERATOR_ITEM.get());
            })
            .build()
    );

    private static BlockBehaviour.Properties blockProperties(Identifier name) {
        return BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, name))
            .sound(SoundType.METAL);
    }

    private static Item.Properties itemProperties(Identifier name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name));
    }

    private ICEGRegistries() {
    }
}
