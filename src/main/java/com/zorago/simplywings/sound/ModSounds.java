package com.zorago.simplywings.sound;

import com.zorago.simplywings.SimplyWings;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, SimplyWings.MOD_ID);

    public static final Supplier<SoundEvent> NIGHT_DIVE = sound("night_fury.dive");
    public static final Supplier<SoundEvent> NIGHT_DASH = sound("night_fury.dash");
    public static final Supplier<SoundEvent> LIGHT_DIVE = sound("light_fury.dive");
    public static final Supplier<SoundEvent> LIGHT_CHARGE = sound("light_fury.charge");
    public static final Supplier<SoundEvent> LIGHT_BLAST = sound("light_fury.blast");

    private static Supplier<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(SimplyWings.id(name)));
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }

    private ModSounds() {
    }
}
