package com.jocosero.kakapos.event;

import com.jocosero.kakapos.Kakapos;
import com.jocosero.kakapos.entity.ModEntityTypes;
import com.jocosero.kakapos.entity.custom.KakapoEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kakapos.MOD_ID)
public class ModEvents {


    @Mod.EventBusSubscriber(modid = Kakapos.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {

            event.put(ModEntityTypes.KAKAPO.get(), KakapoEntity.setAttributes());
        }


    }

}
