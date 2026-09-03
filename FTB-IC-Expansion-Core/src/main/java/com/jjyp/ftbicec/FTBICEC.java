package com.jjyp.ftbicec;

import com.jjyp.ftbicec.block.ICEBlocks;
import com.jjyp.ftbicec.item.ICEItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
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

    static {
        TAB = new CreativeModeTab(MODID) {
            @OnlyIn(Dist.CLIENT)
            public ItemStack makeIcon() {
                return new ItemStack(ICEItems.FIREBRICKS.get());
            }
        };
        REGISTERS = List.of(
            ICEBlocks.ICEC_REGISTRY,
            ICEItems.ICEC_REGISTRY
        );
    }

    public FTBICEC() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        REGISTERS.forEach(register -> register.register(modEventBus));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Reserved for cross-feature setup.
    }
}
