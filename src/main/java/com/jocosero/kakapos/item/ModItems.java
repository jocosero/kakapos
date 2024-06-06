package com.jocosero.kakapos.item;

import com.jocosero.kakapos.Kakapos;
import com.jocosero.kakapos.entity.ModEntityTypes;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Kakapos.MOD_ID);

    public static final RegistryObject<Item> KAKAPO_SPAWN_EGG = ITEMS.register("kakapo_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.KAKAPO, 0xbd9746, 0x788c13, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
