package com.jocosero.kakapos;

import com.jocosero.kakapos.entity.ModEntityTypes;
import com.jocosero.kakapos.entity.client.KakapoRenderer;
import com.jocosero.kakapos.item.ModItems;
import com.jocosero.kakapos.sound.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Kakapos.MOD_ID)
public class Kakapos {
    public static final String MOD_ID = "kakapos";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Kakapos() {


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


//        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);

        ModEntityTypes.register(modEventBus);

        ModSounds.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);


    }

//    private void commonSetup(final FMLCommonSetupEvent event) {
//
//        event.enqueueWork(() -> {
//
//            SpawnPlacements.register(ModEntityTypes.KAKAPO.get(),
//                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                    Animal::checkAnimalSpawnRules);
//
//        });
//    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.KAKAPO_SPAWN_EGG.get());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntityTypes.KAKAPO.get(), KakapoRenderer::new);

        }
    }
}
