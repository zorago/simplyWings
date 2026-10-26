package com.zorago.simplywings.client;

import com.zorago.simplywings.data.FurySet;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.sound.ModSounds;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class FuryEffects {
    private static final int SCREAM_COOLDOWN_TICKS = 60;
    private static final double BLAST_DISTANCE = 30;
    private static final ParticleOptions GHOST = new DustParticleOptions(new Vector3f(0.85f, 0.92f, 1f), 0.6f);
    private static final ParticleOptions PLASMA = new DustParticleOptions(new Vector3f(0.55f, 0.75f, 1f), 1.4f);
    private static final IntSet CHARGING = new IntOpenHashSet();
    private static final IntSet TUCKED = new IntOpenHashSet();
    private static final Int2IntMap LAST_SCREAM = new Int2IntOpenHashMap();

    static void tick(Player player, WingVisuals.Visual visual) {
        Optional<ResourceLocation> wing = visual.equipped;
        int id = player.getId();
        if (!FurySet.isFury(wing)) {
            CHARGING.remove(id);
            TUCKED.remove(id);
            return;
        }
        boolean night = FurySet.isNight(wing);
        boolean local = player == Minecraft.getInstance().player;
        boolean tucked = local ? ClientFlight.terminalDive()
                : visual.mode == FlightMode.TRAVEL && visual.has(FlightMode.FLAG_TUCK);
        if (!tucked) {
            TUCKED.remove(id);
        } else if (TUCKED.add(id) && player.tickCount - LAST_SCREAM.getOrDefault(id, -1000) > SCREAM_COOLDOWN_TICKS) {
            LAST_SCREAM.put(id, player.tickCount);
            SoundEvent scream = night ? ModSounds.NIGHT_DIVE.get() : ModSounds.LIGHT_DIVE.get();
            if (local) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(scream, 1f, 1f));
            } else {
                follow(player, scream, 1.5f);
            }
        }
        if (CHARGING.contains(id)) {
            if (night) {
                chargeNight(player);
            } else {
                chargeLight(player);
            }
        }
    }

    static void onEvent(Player player, int kind) {
        int id = player.getId();
        WingVisuals.Visual visual = WingVisuals.of(player);
        boolean night = visual != null && FurySet.isNight(visual.equipped);
        switch (kind) {
            case Payloads.FuryEvent.CHARGE_START -> {
                CHARGING.add(id);
                if (night) {
                    follow(player, ModSounds.NIGHT_CHARGE.get(), 1f);
                    follow(player, ModSounds.NIGHT_CRACKLE.get(), 0.8f);
                } else {
                    follow(player, ModSounds.LIGHT_CHARGE.get(), 1f);
                }
            }
            case Payloads.FuryEvent.CHARGE_STOP -> CHARGING.remove(id);
            case Payloads.FuryEvent.DASH -> {
                CHARGING.remove(id);
                if (player == Minecraft.getInstance().player) {
                    return;
                }
                if (night) {
                    dashNight(player);
                } else {
                    dashLight(player);
                }
            }
            default -> {
            }
        }
    }

    static void dashLocal(Player player) {
        CHARGING.remove(player.getId());
        WingVisuals.Visual visual = WingVisuals.of(player);
        if (visual != null && FurySet.isNight(visual.equipped)) {
            dashNight(player);
        } else {
            dashLight(player);
        }
    }

    static void ghost(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player || !player.isInvisible() || mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        RandomSource random = player.getRandom();
        if (random.nextInt(3) == 0) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 0.9;
            double y = player.getY() + random.nextDouble() * player.getBbHeight();
            double z = player.getZ() + (random.nextDouble() - 0.5) * 0.9;
            player.level().addParticle(GHOST, x, y, z, 0, 0.01, 0);
        }
    }

    static void clear() {
        CHARGING.clear();
        TUCKED.clear();
        LAST_SCREAM.clear();
    }

    private static void follow(Player player, SoundEvent sound, float volume) {
        Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(sound, SoundSource.PLAYERS, volume,
                1f, player, player.getRandom().nextLong()));
    }

    private static void playDash(Player player, SoundEvent sound) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1f, 1f));
        } else {
            player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS,
                    1f, 1f, false);
        }
    }

    private static Vec3 mouth(Player player) {
        return player.getEyePosition().add(player.getLookAngle().scale(0.7)).add(0, -0.15, 0);
    }

    private static void chargeNight(Player player) {
        Level level = player.level();
        RandomSource random = player.getRandom();
        for (int i = 0; i < 3; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 1.2;
            double y = player.getY() + random.nextDouble() * player.getBbHeight();
            double z = player.getZ() + (random.nextDouble() - 0.5) * 1.2;
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z,
                    (random.nextDouble() - 0.5) * 0.3, (random.nextDouble() - 0.5) * 0.3, (random.nextDouble() - 0.5) * 0.3);
        }
    }

    private static void chargeLight(Player player) {
        Level level = player.level();
        RandomSource random = player.getRandom();
        Vec3 mouth = mouth(player);
        for (int i = 0; i < 2; i++) {
            Vec3 offset = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).normalize().scale(1.4);
            Vec3 from = mouth.add(offset);
            Vec3 v = offset.scale(-0.12);
            level.addParticle(random.nextBoolean() ? ParticleTypes.END_ROD : PLASMA, from.x, from.y, from.z, v.x, v.y, v.z);
        }
    }

    private static void dashNight(Player player) {
        Level level = player.level();
        RandomSource random = player.getRandom();
        playDash(player, ModSounds.NIGHT_INVISIBLE.get());
        Vec3 look = player.getLookAngle();
        for (int i = 0; i < 40; i++) {
            Vec3 p = player.position().add(0, player.getBbHeight() * 0.5, 0).add(look.scale(random.nextDouble() * BLAST_DISTANCE))
                    .add(random.nextGaussian() * 0.6, random.nextGaussian() * 0.6, random.nextGaussian() * 0.6);
            level.addParticle(i % 4 == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, p.x, p.y, p.z, 0, 0.02, 0);
        }
    }

    private static void dashLight(Player player) {
        Level level = player.level();
        RandomSource random = player.getRandom();
        Vec3 look = player.getLookAngle();
        Vec3 mouth = mouth(player);
        for (int i = 0; i < 60; i++) {
            Vec3 p = mouth.add(look.scale(3 + i * (BLAST_DISTANCE - 3) / 60.0));
            Vec3 v = look.scale(0.8);
            level.addParticle(i % 2 == 0 ? ParticleTypes.END_ROD : PLASMA, p.x, p.y, p.z, v.x, v.y, v.z);
        }
        Vec3 blast = mouth.add(look.scale(BLAST_DISTANCE));
        for (int i = 0; i < 3; i++) {
            Vec3 b = blast.add(look.scale(i * 2.0));
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, b.x, b.y, b.z, 0, 0, 0);
        }
        for (int i = 0; i < 50; i++) {
            Vec3 v = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).scale(0.4);
            level.addParticle(i % 3 == 0 ? ParticleTypes.SOUL_FIRE_FLAME : PLASMA, blast.x, blast.y, blast.z, v.x, v.y, v.z);
        }
        playDash(player, ModSounds.LIGHT_BLAST.get());
        playDash(player, ModSounds.LIGHT_INVISIBLE.get());
    }

    private FuryEffects() {
    }
}
