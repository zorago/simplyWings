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
    public static final Supplier<SoundEvent> NIGHT_INVISIBLE = sound("night_fury.invisible");
    public static final Supplier<SoundEvent> LIGHT_INVISIBLE = sound("light_fury.invisible");
    public static final Supplier<SoundEvent> LIGHT_DIVE = sound("light_fury.dive");
    public static final Supplier<SoundEvent> LIGHT_CHARGE = sound("light_fury.charge");
    public static final Supplier<SoundEvent> LIGHT_BLAST = sound("light_fury.blast");
    public static final Supplier<SoundEvent> TAKEOFF_LAUNCH = sound("flight.takeoff_launch");
    public static final Supplier<SoundEvent> GACHA_OFFER = sound("gacha.offer");
    public static final Supplier<SoundEvent> GACHA_IGNITE = sound("gacha.ignite");
    public static final Supplier<SoundEvent> GACHA_RUNE_CHARGE = sound("gacha.rune_charge");
    public static final Supplier<SoundEvent> GACHA_BEAM = sound("gacha.beam");
    public static final Supplier<SoundEvent> GACHA_SHOCKWAVE = sound("gacha.shockwave");
    public static final Supplier<SoundEvent> GACHA_BLAST = sound("gacha.blast");
    public static final Supplier<SoundEvent> GACHA_IMPLOSION = sound("gacha.implosion");
    public static final Supplier<SoundEvent> GACHA_PUFF = sound("gacha.puff");
    public static final Supplier<SoundEvent> GACHA_BELL = sound("gacha.bell");
    public static final Supplier<SoundEvent> GACHA_CHIME = sound("gacha.chime");
    public static final Supplier<SoundEvent> GACHA_TWINKLE = sound("gacha.twinkle");
    public static final Supplier<SoundEvent> CARD_DEAL = sound("ui.card_deal");
    public static final Supplier<SoundEvent> CARD_FLIP = sound("ui.card_flip");
    public static final Supplier<SoundEvent> CARD_CONFIRM = sound("ui.confirm");
    public static final Supplier<SoundEvent> DENIED = sound("ui.denied");

    private static Supplier<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(SimplyWings.id(name)));
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }

    private ModSounds() {
    }
}
