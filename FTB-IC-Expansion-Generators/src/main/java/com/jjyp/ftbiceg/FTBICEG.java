package com.jjyp.ftbiceg;

import com.jjyp.ftbiceg.block.entity.ICEGElectricBlocks;
import com.jjyp.ftbiceg.client.ICEGClient;
import com.jjyp.ftbiceg.screen.ICEGMenus;
import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
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
import dev.ftb.mods.ftbic.FTBICCommon;

import java.nio.file.Path;
import java.util.List;

@Mod(FTBICEG.MODID)
public class FTBICEG
{
    public static final String MODID = "ftbiceg";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static FTBICCommon PROXY;
    public static final List<DeferredRegister<?>> REGISTERS;
    public static final CreativeModeTab TAB;

    static {
        TAB = new CreativeModeTab("ftbiceg") {
            @OnlyIn(Dist.CLIENT)
            public ItemStack makeIcon() {
                return new ItemStack(ICEGElectricBlocks.ADVANCED_GENERATOR.item.get());
            }
        };
        REGISTERS = List.of(
                ICEGMenus.REGISTRY
        );
    }
    public FTBICEG()
    {
        PROXY = DistExecutor.safeRunForDist(() -> ICEGClient::new, () -> FTBICCommon::new);

        Path configPath = Platform.getConfigFolder().resolve("FTB IC Expansion\\ftbiceg-common.snbt");
        ICEGConfig.CONFIG.load(configPath);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        REGISTERS.forEach(e -> e.register(modEventBus));

        ICEGConfig.init();
        ICEGElectricBlocks.init();

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
