package com.qevro.escalux.block;

import com.qevro.escalux.Escalux;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EscaluxBlocks {
    private EscaluxBlocks() {}
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Escalux.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Escalux.MOD_ID);

    public static final RegistryObject<Block> ESCALATOR_UP = register("escalator_up", new EscalatorBlock(EscalatorBlock.MotionDirection.UP));
    public static final RegistryObject<Block> ESCALATOR_DOWN = register("escalator_down", new EscalatorBlock(EscalatorBlock.MotionDirection.DOWN));

    private static RegistryObject<Block> register(String name, Block block) {
        RegistryObject<Block> object = BLOCKS.register(name, () -> block);
        ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties()));
        return object;
    }
}
