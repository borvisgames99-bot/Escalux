package com.qevro.escalux;

import com.qevro.escalux.block.EscaluxBlocks;
import com.qevro.escalux.config.EscaluxConfig;
import com.qevro.escalux.compat.CreateCompat;
import com.qevro.escalux.item.EscaluxItems;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Escalux.MOD_ID)
public class Escalux {
    public static final String MOD_ID = "escalux";

    public Escalux() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EscaluxBlocks.BLOCKS.register(bus);
        EscaluxBlocks.ITEMS.register(bus);
        EscaluxItems.ITEMS.register(bus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EscaluxConfig.SPEC);
        CreateCompat.initialize();
    }
}
