package com.zorago.simplywings.client;

import java.util.function.BooleanSupplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public final class FadingSound extends AbstractTickableSoundInstance {
    private final float base;
    private final int fadeTicks;
    private final BooleanSupplier keep;
    private int fading = -1;

    public FadingSound(SoundEvent event, SoundSource source, float volume, float pitch, Vec3 at, float fadeSeconds, BooleanSupplier keep) {
        super(event, source, SoundInstance.createUnseededRandom());
        this.volume = volume;
        this.base = volume;
        this.pitch = pitch;
        this.fadeTicks = Math.max(1, Math.round(fadeSeconds * 20));
        this.keep = keep;
        if (at == null) {
            this.relative = true;
            this.attenuation = Attenuation.NONE;
        } else {
            this.x = at.x;
            this.y = at.y;
            this.z = at.z;
        }
    }

    public void fadeOut() {
        if (fading < 0) {
            fading = 0;
        }
    }

    @Override
    public void tick() {
        if (fading < 0 && !keep.getAsBoolean()) {
            fading = 0;
        }
        if (fading >= 0) {
            fading++;
            volume = base * Math.max(0f, 1f - fading / (float) fadeTicks);
            if (fading >= fadeTicks) {
                stop();
            }
        }
    }
}
