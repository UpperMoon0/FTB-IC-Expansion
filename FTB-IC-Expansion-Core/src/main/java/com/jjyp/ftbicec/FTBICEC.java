package com.jjyp.ftbicec;

import com.jjyp.ftbicec.block.ICEBlocks;
import com.jjyp.ftbicec.block.entity.ICEBlockEntities;
import com.jjyp.ftbicec.client.ICECClient;
import com.jjyp.ftbicec.item.ICEItems;
import com.jjyp.ftbicec.screen.ICECMenus;
import dev.ftb.mods.ftbic.FTBICCommon;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.List;

@Mod(FTBICEC.MODID)
public class FTBICEC {
    public static final String MODID = "ftbicec";
    public static final CreativeModeTab TAB;
    public static final List<DeferredRegister<?>> REGISTERS;
    public static FTBICCommon PROXY;

    static {
        TAB = new CreativeModeTab(MODID) {
            @OnlyIn(Dist.CLIENT)
            public ItemStack makeIcon() {
                return new ItemStack(ICEItems.LARGE_BLAST_FURNACE.get());
            }
        };
        REGISTERS = List.of(
            ICEBlocks.ICEC_REGISTRY,
            ICEBlockEntities.ICEC_REGISTRY,
            ICEItems.ICEC_REGISTRY,
            ICECMenus.REGISTRY
        );
    }

    public FTBICEC() {
        PROXY = DistExecutor.safeRunForDist(() -> ICECClient::new, () -> FTBICCommon::new);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        REGISTERS.forEach(register -> register.register(modEventBus));
        PROXY.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Reserved for cross-feature setup.
    }
}
