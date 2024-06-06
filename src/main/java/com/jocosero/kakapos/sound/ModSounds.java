package com.jocosero.kakapos.sound;

import com.jocosero.kakapos.Kakapos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Kakapos.MOD_ID);

    public static final RegistryObject<SoundEvent> KAKAPO_AMBIENT = registerSoundEvents("kakapo_ambient");

    public static final RegistryObject<SoundEvent> KAKAPO_HURT = registerSoundEvents("kakapo_hurt");

    public static final RegistryObject<SoundEvent> KAKAPO_DEATH = registerSoundEvents("kakapo_death");

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kakapos.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
