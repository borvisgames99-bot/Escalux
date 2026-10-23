package com.qevro.escalux.item;

import com.qevro.escalux.Escalux;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EscaluxItems {
    private EscaluxItems() {}
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Escalux.MOD_ID);
    public static final RegistryObject<Item> MAINTENANCE_KEY = ITEMS.register("maintenance_key",
            () -> new Item(new Item.Properties().stacksTo(1)));
}
