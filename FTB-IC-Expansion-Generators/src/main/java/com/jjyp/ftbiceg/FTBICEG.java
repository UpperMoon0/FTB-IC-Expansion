package com.jjyp.ftbiceg;

import com.jjyp.ftbicec.block.ICEBlocks;
import com.jjyp.ftbicec.block.entity.ICEBlockEntities;
import com.jjyp.ftbicec.item.ICEItems;
import com.jjyp.ftbiceg.block.entity.ICEGElectricBlocks;
import com.jjyp.ftbiceg.client.ICEGClient;
import com.jjyp.ftbiceg.screen.ICEGMenus;
import dev.architectury.platform.Platform;
import dev.ftb.mods.ftbic.FTBICCommon;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.nio.file.Path;
import java.util.List;

@Mod(FTBICEG.MODID)
public class FTBICEG {
    public static final String MODID = "ftbiceg";
    public static final CreativeModeTab TAB;
    public static final List<DeferredRegister<?>> REGISTERS;
    public static FTBICCommon PROXY;

    static {
        TAB = new CreativeModeTab(MODID) {
            @OnlyIn(Dist.CLIENT)
            public ItemStack makeIcon() {
                return new ItemStack(ICEGElectricBlocks.ADVANCED_GENERATOR.item.get());
            }
        };
        REGISTERS = List.of(
            ICEBlocks.ICEG_REGISTRY,
            ICEBlockEntities.ICEG_REGISTRY,
            ICEItems.ICEG_REGISTRY,
            ICEGMenus.REGISTRY
        );
    }

    public FTBICEG() {
        PROXY = DistExecutor.safeRunForDist(() -> ICEGClient::new, () -> FTBICCommon::new);

        Path configPath = Platform.getConfigFolder().resolve("FTB IC Expansion").resolve("ftbiceg-common.snbt");
        ICEGConfig.CONFIG.load(configPath);
        ICEGConfig.init();
        ICEGElectricBlocks.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTERS.forEach(register -> register.register(modEventBus));
        PROXY.init();
    }
}
