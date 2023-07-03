package com.jocosero.kakapos.entity;

import com.jocosero.kakapos.Kakapos;
import com.jocosero.kakapos.entity.custom.KakapoEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Kakapos.MOD_ID);

    public static final RegistryObject<EntityType<KakapoEntity>> KAKAPO =
            ENTITY_TYPES.register("kakapo",
                    () -> EntityType.Builder.of(KakapoEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 0.7f)
                            .build(new ResourceLocation(Kakapos.MOD_ID, "kakapo").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
