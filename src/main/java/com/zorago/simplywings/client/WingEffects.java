package com.zorago.simplywings.client;

import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.config.ClientConfig;
import com.zorago.simplywings.player.FlightMode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class WingEffects {
    private static final double MAX_DISTANCE_SQR = 64 * 64;
    private static final ParticleOptions FEATHER = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.FEATHER));
    private static final ParticleOptions GOLD = new DustParticleOptions(new Vector3f(1f, 0.82f, 0.35f), 1.2f);
    private static final ParticleOptions EMBER = new DustParticleOptions(new Vector3f(1f, 0.35f, 0.08f), 0.9f);

    private record Frame(Vec3 center, Vec3 side, Vec3 back, Vec3 up, Vec3 velocity) {
        Vec3 alongWing(double sign, double t) {
            return center.add(side.scale(sign * t));
        }
    }

    private static boolean active(Player player) {
        Minecraft mc = Minecraft.getInstance();
        return ClientConfig.PARTICLES.get()
                && mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(player.position()) <= MAX_DISTANCE_SQR;
    }

    private static Frame frame(Player player, FlightMode mode, float span) {
        double yaw = Math.toRadians(player.yBodyRot);
        Vec3 forward = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
        Vec3 side = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw)).scale(span);
        Vec3 velocity = player.position().subtract(player.xo, player.yo, player.zo);
        if (mode == FlightMode.TRAVEL) {
            Vec3 look = player.getLookAngle();
            Vec3 up = look.cross(side.normalize()).normalize();
            if (up.y < 0) {
                up = up.scale(-1);
            }
            Vec3 center = player.position().add(0, player.getBbHeight() * 0.5, 0).add(up.scale(0.25));
            return new Frame(center, side, look.scale(-1), up, velocity);
        }
        Vec3 center = player.position().add(0, player.getBbHeight() * 0.72, 0).subtract(forward.scale(0.3));
        return new Frame(center, side, forward.scale(-1), new Vec3(0, 1, 0), velocity);
    }

    static void tick(Player player, WingStyle style, FlightMode mode, int flags) {
        if (style.effects() == WingStyle.Effects.NONE || !active(player)) {
            return;
        }
        Level level = player.level();
        RandomSource random = player.getRandom();
        boolean flying = mode.flying();
        boolean grounded = player.onGround();
        Frame f = frame(player, mode, style.span());
        double sign = random.nextBoolean() ? 1 : -1;
        switch (style.effects()) {
            case ANGEL -> {
                if (flying && random.nextInt(6) == 0) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.2));
                    level.addParticle(FEATHER, p.x, p.y, p.z, f.velocity.x * 0.3, -0.03, f.velocity.z * 0.3);
                }
                if (random.nextInt(flying ? 4 : 14) == 0) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(0, random.nextDouble() * 0.3, 0);
                    level.addParticle(ParticleTypes.END_ROD, p.x, p.y, p.z, 0, 0.01, 0);
                }
            }
            case DEMON -> {
                int rate = flying ? 2 : 8;
                if (random.nextInt(rate) == 0) {
                    Vec3 p = f.alongWing(sign, 0.4 + random.nextDouble() * 0.6).add(f.back.scale(0.25));
                    level.addParticle(EMBER, p.x, p.y, p.z, 0, 0.02, 0);
                }
                if (flying && random.nextInt(5) == 0) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.3));
                    level.addParticle(ParticleTypes.SMALL_FLAME, p.x, p.y, p.z, 0, 0.015, 0);
                }
                if (flying && random.nextInt(7) == 0) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.4));
                    level.addParticle(ParticleTypes.SMOKE, p.x, p.y, p.z, 0, 0.02, 0);
                }
            }
            case SERAPH -> {
                if (random.nextInt(flying ? 2 : 8) == 0) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.up.scale(random.nextDouble() * 0.4 - 0.2));
                    level.addParticle(GOLD, p.x, p.y, p.z, 0, 0.01, 0);
                }
                double speed = f.velocity.length() * 20;
                if (mode == FlightMode.TRAVEL && speed > 25) {
                    for (int s = -1; s <= 1; s += 2) {
                        Vec3 tip = f.alongWing(s, 1.0).add(f.back.scale(0.3));
                        level.addParticle(ParticleTypes.END_ROD, tip.x, tip.y, tip.z, 0, 0, 0);
                    }
                }
            }
            case PSYCHE -> {
                for (int i = 0; i < (flying ? 3 : 2); i++) {
                    double s = random.nextBoolean() ? 1 : -1;
                    Vec3 p = f.alongWing(s, 0.2 + random.nextDouble() * 0.8)
                            .add(f.up.scale(random.nextDouble() * 0.9 - 0.3)).add(f.back.scale(0.15));
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, p.x, p.y, p.z, 0, 0.012, 0);
                }
                if (random.nextInt(4) == 0) {
                    Vec3 p = f.center.add(f.side.scale(sign * random.nextDouble())).add(f.up.scale(0.6));
                    level.addParticle(ParticleTypes.SOUL, p.x, p.y, p.z, 0, 0.02, 0);
                }
            }
            case THRUSTERS -> {
                if (mode == FlightMode.STABILIZED || (flags & FlightMode.FLAG_THRUST) != 0) {
                    Vec3 exhaust = mode == FlightMode.TRAVEL ? f.back.scale(0.25) : new Vec3(0, -0.25, 0);
                    for (int s = -1; s <= 1; s += 2) {
                        Vec3 nozzle = f.center.add(f.side.normalize().scale(0.35 * s)).add(f.back.scale(0.1));
                        level.addParticle(ParticleTypes.SMALL_FLAME, nozzle.x, nozzle.y, nozzle.z, exhaust.x, exhaust.y, exhaust.z);
                        if (random.nextInt(3) == 0) {
                            level.addParticle(ParticleTypes.SMOKE, nozzle.x, nozzle.y, nozzle.z,
                                    exhaust.x * 0.5, exhaust.y * 0.5, exhaust.z * 0.5);
                        }
                    }
                }
            }
            default -> {
            }
        }
    }

    public static void strike(Player player, WingStyle style, FlightMode mode) {
        if (style.effects() == WingStyle.Effects.NONE || !active(player)) {
            return;
        }
        Level level = player.level();
        RandomSource random = player.getRandom();
        Frame f = frame(player, mode, style.span());
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        switch (style.effects()) {
            case ANGEL -> {
                for (int i = 0; i < 10; i++) {
                    double s = i % 2 == 0 ? 1 : -1;
                    Vec3 p = f.alongWing(s, random.nextDouble());
                    Vec3 v = f.back.scale(0.1 + random.nextDouble() * 0.1).add(0, -0.05, 0);
                    level.addParticle(FEATHER, p.x, p.y, p.z, v.x, v.y, v.z);
                }
                for (int i = 0; i < 4; i++) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, 0.6 + random.nextDouble() * 0.4);
                    level.addParticle(ParticleTypes.CLOUD, p.x, p.y, p.z, f.back.x * 0.1, f.back.y * 0.1, f.back.z * 0.1);
                }
                level.playLocalSound(x, y, z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.5f, 1.4f, false);
            }
            case DEMON -> {
                for (int i = 0; i < 16; i++) {
                    double s = i % 2 == 0 ? 1 : -1;
                    Vec3 p = f.alongWing(s, random.nextDouble());
                    Vec3 v = f.back.scale(0.15 + random.nextDouble() * 0.15);
                    level.addParticle(ParticleTypes.FLAME, p.x, p.y, p.z, v.x, v.y + 0.02, v.z);
                }
                for (int i = 0; i < 6; i++) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.4));
                    level.addParticle(ParticleTypes.LARGE_SMOKE, p.x, p.y, p.z, f.back.x * 0.08, 0.02, f.back.z * 0.08);
                }
                Vec3 c = f.center.add(f.back.scale(0.5));
                level.addParticle(ParticleTypes.LAVA, c.x, c.y, c.z, 0, 0, 0);
                level.playLocalSound(x, y, z, SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.45f, 0.55f, false);
            }
            case SERAPH -> {
                for (int i = 0; i < 24; i++) {
                    double angle = i / 24.0 * Math.PI * 2;
                    Vec3 ring = f.side.normalize().scale(Math.cos(angle) * 1.3).add(f.up.scale(Math.sin(angle) * 1.3));
                    Vec3 p = f.center.add(f.back.scale(0.4)).add(ring);
                    level.addParticle(ParticleTypes.END_ROD, p.x, p.y, p.z, f.back.x * 0.12, f.back.y * 0.12, f.back.z * 0.12);
                }
                for (int i = 0; i < 8; i++) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble());
                    level.addParticle(GOLD, p.x, p.y, p.z, 0, 0.02, 0);
                }
                level.playLocalSound(x, y, z, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.35f, 1.6f, false);
            }
            case PSYCHE -> {
                for (int i = 0; i < 14; i++) {
                    double s = i % 2 == 0 ? 1 : -1;
                    Vec3 p = f.alongWing(s, random.nextDouble()).add(f.up.scale(random.nextDouble() * 0.6));
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, p.x, p.y, p.z, f.back.x * 0.06, 0.03, f.back.z * 0.06);
                }
                level.playLocalSound(x, y, z, SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 0.6f, 1.2f, false);
            }
            default -> {
            }
        }
    }

    private WingEffects() {
    }
}
