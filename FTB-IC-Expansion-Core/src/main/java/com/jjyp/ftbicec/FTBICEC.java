package com.jjyp.ftbicec;

import com.jjyp.ftbicec.block.ICEBlocks;
import com.jjyp.ftbicec.block.entity.ICEBlockEntities;
import com.jjyp.ftbicec.client.ICECClient;
import com.jjyp.ftbicec.item.ICEItems;
import com.jjyp.ftbicec.screen.ICECMenus;
import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftbic.FTBICCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(FTBICEC.MODID)
public class FTBICEC
{
    public static boolean debug = true;
    public static final String MODID = "ftbicec";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();
    public static final CreativeModeTab TAB;
    public static FTBICCommon PROXY;

    static {
        TAB = new CreativeModeTab("ftbicec") {
            @OnlyIn(Dist.CLIENT)
            public ItemStack makeIcon() {
                return new ItemStack(ICEItems.LARGE_BLAST_FURNACE.get());
            }
        };
        REGISTERS.addAll(ICEBlocks.REGISTERS_LIST);
        REGISTERS.addAll(ICEBlockEntities.REGISTERS_LIST);
        REGISTERS.addAll(ICEItems.REGISTERS_LIST);
        REGISTERS.add(ICECMenus.REGISTRY);
    }
    public FTBICEC()
    {
        PROXY = DistExecutor.safeRunForDist(() -> ICECClient::new, () -> FTBICCommon::new);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        REGISTERS.forEach(e -> e.register(modEventBus));

        PROXY.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
