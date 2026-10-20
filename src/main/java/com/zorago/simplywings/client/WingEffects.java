package com.zorago.simplywings.client;

import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.config.ClientConfig;
import com.zorago.simplywings.player.FlightMode;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class WingEffects {
    private static final double MAX_DISTANCE_SQR = 64 * 64;
    private static final float TRAVEL_DENSITY = 1f;
    private static final float HOVER_DENSITY = 0.25f;
    private static final float IDLE_DENSITY = 0.1f;
    private static final ParticleOptions FEATHER = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.FEATHER));
    private static final ParticleOptions GOLD = new DustParticleOptions(new Vector3f(1f, 0.82f, 0.35f), 1.2f);
    private static final ParticleOptions EMBER = new DustParticleOptions(new Vector3f(1f, 0.35f, 0.08f), 0.9f);
    private static final ParticleOptions SPARK = new DustParticleOptions(new Vector3f(1f, 0.75f, 0.2f), 0.7f);
    private static final ParticleOptions FOAM = new DustParticleOptions(new Vector3f(0.75f, 0.9f, 1f), 0.8f);
    private static final ParticleOptions AMBER = new DustParticleOptions(new Vector3f(0.95f, 0.6f, 0.15f), 0.6f);
    private static final ParticleOptions WISP = new DustParticleOptions(new Vector3f(0.92f, 0.97f, 1f), 0.7f);
    private static final ParticleOptions AURORA_GREEN = new DustParticleOptions(new Vector3f(0.3f, 1f, 0.6f), 0.9f);
    private static final ParticleOptions AURORA_VIOLET = new DustParticleOptions(new Vector3f(0.7f, 0.4f, 1f), 0.9f);
    private static final ParticleOptions SHADE = new DustParticleOptions(new Vector3f(0.08f, 0.04f, 0.12f), 1.3f);
    private static final ParticleOptions STAR = new DustParticleOptions(new Vector3f(0.9f, 0.9f, 1f), 0.5f);
    private static final ParticleOptions AMETHYST_DUST = new DustParticleOptions(new Vector3f(0.75f, 0.5f, 1f), 0.6f);
    private static final ParticleOptions DIRT_DUST = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.COARSE_DIRT.defaultBlockState());
    private static final ParticleOptions STONE_CHIP = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState());
    private static final ParticleOptions MOSS_CHIP = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MOSS_BLOCK.defaultBlockState());

    private record Frame(Vec3 center, Vec3 side, Vec3 back, Vec3 up, Vec3 velocity) {
        Vec3 alongWing(double sign, double t) {
            return center.add(side.scale(sign * t));
        }
    }

    private static final class Emitter {
        private final Level level;
        private final RandomSource random;
        private final float density;
        private final Vec3 eye;
        private final Vec3 look;

        Emitter(Player player, FlightMode mode) {
            this.level = player.level();
            this.random = player.getRandom();
            this.density = mode == FlightMode.TRAVEL ? TRAVEL_DENSITY
                    : mode == FlightMode.STABILIZED ? HOVER_DENSITY : IDLE_DENSITY;
            Minecraft mc = Minecraft.getInstance();
            if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
                Camera camera = mc.gameRenderer.getMainCamera();
                this.eye = camera.getPosition();
                this.look = new Vec3(camera.getLookVector());
            } else {
                this.eye = null;
                this.look = null;
            }
        }

        boolean chance(float base) {
            return random.nextFloat() < base * density;
        }

        int count(int full) {
            float scaled = full * density;
            int n = (int) scaled;
            return random.nextFloat() < scaled - n ? n + 1 : n;
        }

        void add(ParticleOptions options, Vec3 p, double vx, double vy, double vz) {
            if (eye != null) {
                Vec3 rel = p.subtract(eye);
                if (rel.dot(look) > -0.6 || vx * look.x + vy * look.y + vz * look.z > 0.02) {
                    return;
                }
            }
            level.addParticle(options, p.x, p.y, p.z, vx, vy, vz);
        }

        void add(ParticleOptions options, Vec3 p, Vec3 v) {
            add(options, p, v.x, v.y, v.z);
        }

        void sound(Player player, SoundEvent sound, float volume, float pitch) {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS,
                    volume * (0.4f + 0.6f * density), pitch, false);
        }
    }

    private static boolean active(Player player) {
        Minecraft mc = Minecraft.getInstance();
        return ClientConfig.PARTICLES.get() && !player.isInvisible()
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
        Emitter e = new Emitter(player, mode);
        RandomSource random = player.getRandom();
        boolean travel = mode == FlightMode.TRAVEL;
        Frame f = frame(player, mode, style.span());
        double sign = random.nextBoolean() ? 1 : -1;
        double speed = f.velocity.length() * 20;
        switch (style.effects()) {
            case ANGEL -> {
                if (e.chance(0.16f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.2));
                    e.add(FEATHER, p, f.velocity.x * 0.3, -0.03, f.velocity.z * 0.3);
                }
                if (e.chance(0.25f)) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(0, random.nextDouble() * 0.3, 0);
                    e.add(ParticleTypes.END_ROD, p, 0, 0.01, 0);
                }
            }
            case DEMON -> {
                if (e.chance(0.5f)) {
                    Vec3 p = f.alongWing(sign, 0.4 + random.nextDouble() * 0.6).add(f.back.scale(0.25));
                    e.add(EMBER, p, 0, 0.02, 0);
                }
                if (e.chance(0.2f)) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.3));
                    e.add(ParticleTypes.SMALL_FLAME, p, 0, 0.015, 0);
                }
                if (e.chance(0.14f)) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.4));
                    e.add(ParticleTypes.SMOKE, p, 0, 0.02, 0);
                }
            }
            case SERAPH -> {
                if (e.chance(0.5f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.up.scale(random.nextDouble() * 0.4 - 0.2));
                    e.add(GOLD, p, 0, 0.01, 0);
                }
                if (travel && speed > 25) {
                    for (int s = -1; s <= 1; s += 2) {
                        e.add(ParticleTypes.END_ROD, f.alongWing(s, 1.0).add(f.back.scale(0.3)), 0, 0, 0);
                    }
                }
            }
            case PSYCHE -> {
                for (int i = e.count(3); i > 0; i--) {
                    double s = random.nextBoolean() ? 1 : -1;
                    Vec3 p = f.alongWing(s, 0.2 + random.nextDouble() * 0.8)
                            .add(f.up.scale(random.nextDouble() * 0.9 - 0.3)).add(f.back.scale(0.15));
                    e.add(ParticleTypes.SOUL_FIRE_FLAME, p, 0, 0.012, 0);
                }
                if (e.chance(0.25f)) {
                    Vec3 p = f.center.add(f.side.scale(sign * random.nextDouble())).add(f.up.scale(0.6));
                    e.add(ParticleTypes.SOUL, p, 0, 0.02, 0);
                }
            }
            case THRUSTERS -> {
                if (mode == FlightMode.STABILIZED || (flags & FlightMode.FLAG_THRUST) != 0) {
                    Vec3 exhaust = travel ? f.back.scale(0.25) : new Vec3(0, -0.25, 0);
                    for (int s = -1; s <= 1; s += 2) {
                        Vec3 nozzle = f.center.add(f.side.normalize().scale(0.35 * s)).add(f.back.scale(0.1));
                        if (e.chance(1f)) {
                            e.add(ParticleTypes.SMALL_FLAME, nozzle, exhaust);
                        }
                        if (e.chance(0.33f)) {
                            e.add(ParticleTypes.SMOKE, nozzle, exhaust.scale(0.5));
                        }
                    }
                }
            }
            case FIRE -> {
                for (int i = e.count(2); i > 0; i--) {
                    double s = random.nextBoolean() ? 1 : -1;
                    Vec3 p = f.alongWing(s, 0.2 + random.nextDouble() * 0.8).add(f.back.scale(0.2));
                    e.add(ParticleTypes.FLAME, p, f.back.scale(0.02).add(0, 0.02, 0));
                }
                if (e.chance(0.4f)) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.3));
                    e.add(SPARK, p, 0, 0.05, 0);
                }
                if (travel && speed > 20 && e.chance(0.5f)) {
                    e.add(ParticleTypes.SMOKE, f.center.add(f.back.scale(0.8)), 0, 0.01, 0);
                }
            }
            case WATER -> {
                if (e.chance(0.4f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.15));
                    e.add(ParticleTypes.DRIPPING_WATER, p, 0, 0, 0);
                }
                if (travel && e.chance(0.6f)) {
                    Vec3 p = f.alongWing(sign, 0.7 + random.nextDouble() * 0.3).add(f.back.scale(0.3));
                    e.add(FOAM, p, f.back.scale(0.03));
                }
                if (travel && speed > 18 && e.chance(0.5f)) {
                    Vec3 p = f.alongWing(sign, 1.0).add(f.back.scale(0.4));
                    e.add(ParticleTypes.SPLASH, p, 0, 0, 0);
                }
            }
            case EARTH -> {
                if (e.chance(0.3f)) {
                    Vec3 p = f.alongWing(sign, 0.2 + random.nextDouble() * 0.8).add(f.back.scale(0.2));
                    e.add(DIRT_DUST, p, 0, 0, 0);
                }
                if (travel && e.chance(0.25f)) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.4));
                    e.add(STONE_CHIP, p, f.back.scale(0.05));
                }
            }
            case AIR -> {
                if (e.chance(0.5f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.3));
                    e.add(WISP, p, f.back.scale(0.08));
                }
                if (travel && speed > 20) {
                    for (int s = -1; s <= 1; s += 2) {
                        if (e.chance(0.6f)) {
                            e.add(ParticleTypes.WHITE_ASH, f.alongWing(s, 1.0).add(f.back.scale(0.4)), f.back.scale(0.1));
                        }
                    }
                }
            }
            case STORM -> {
                if (e.chance(0.5f)) {
                    Vec3 p = f.alongWing(sign, 0.2 + random.nextDouble() * 0.8).add(f.back.scale(0.2));
                    e.add(ParticleTypes.ELECTRIC_SPARK, p, f.back.scale(0.05));
                }
                if (travel && speed > 20 && e.chance(0.4f)) {
                    e.add(ParticleTypes.ELECTRIC_SPARK, f.alongWing(sign, 1.0).add(f.back.scale(0.4)), f.back.scale(0.1));
                }
            }
            case AURORA -> {
                if (e.chance(0.6f)) {
                    Vec3 p = f.alongWing(sign, 0.2 + random.nextDouble() * 0.8)
                            .add(f.up.scale(random.nextDouble() * 0.6)).add(f.back.scale(0.2));
                    e.add(random.nextBoolean() ? AURORA_GREEN : AURORA_VIOLET, p, f.back.scale(0.02).add(0, 0.01, 0));
                }
            }
            case SHADOW -> {
                if (e.chance(0.6f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.25));
                    e.add(SHADE, p, f.back.scale(0.03).add(0, 0.01, 0));
                }
                if (travel && e.chance(0.3f)) {
                    e.add(ParticleTypes.SQUID_INK, f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.5)), f.back.scale(0.05));
                }
            }
            case NEBULA -> {
                if (e.chance(0.5f)) {
                    Vec3 p = f.alongWing(sign, 0.2 + random.nextDouble() * 0.8).add(f.up.scale(random.nextDouble() * 0.4 - 0.2));
                    e.add(STAR, p, 0, 0, 0);
                }
                if (travel && speed > 25 && e.chance(0.4f)) {
                    e.add(ParticleTypes.END_ROD, f.alongWing(sign, 1.0).add(f.back.scale(0.4)), 0, 0, 0);
                }
            }
            case FOREST -> {
                if (e.chance(0.3f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.2));
                    e.add(ParticleTypes.CHERRY_LEAVES, p, 0, 0, 0);
                }
                if (e.chance(0.15f)) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.2));
                    e.add(ParticleTypes.SPORE_BLOSSOM_AIR, p, 0, 0, 0);
                }
            }
            case AMETHYST -> {
                if (e.chance(0.15f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.2));
                    e.add(AMETHYST_DUST, p, 0, 0.01, 0);
                }
            }
            case COPPER -> {
                if (travel && e.chance(0.2f)) {
                    Vec3 p = f.alongWing(sign, random.nextDouble()).add(f.back.scale(0.3));
                    e.add(ParticleTypes.SCRAPE, p, f.back.scale(0.05));
                }
            }
            case FOSSIL -> {
                if (e.chance(0.2f)) {
                    Vec3 p = f.alongWing(sign, 0.3 + random.nextDouble() * 0.7).add(f.back.scale(0.2));
                    e.add(AMBER, p, 0, -0.01, 0);
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
        Emitter e = new Emitter(player, mode);
        RandomSource random = player.getRandom();
        Frame f = frame(player, mode, style.span());
        switch (style.effects()) {
            case ANGEL -> {
                for (int i = e.count(10); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble());
                    e.add(FEATHER, p, f.back.scale(0.1 + random.nextDouble() * 0.1).add(0, -0.05, 0));
                }
                for (int i = e.count(4); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, 0.6 + random.nextDouble() * 0.4);
                    e.add(ParticleTypes.CLOUD, p, f.back.scale(0.1));
                }
                e.sound(player, SoundEvents.AMETHYST_BLOCK_CHIME, 0.5f, 1.4f);
            }
            case DEMON -> {
                for (int i = e.count(16); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble());
                    e.add(ParticleTypes.FLAME, p, f.back.scale(0.15 + random.nextDouble() * 0.15).add(0, 0.02, 0));
                }
                for (int i = e.count(6); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.4));
                    e.add(ParticleTypes.LARGE_SMOKE, p, f.back.x * 0.08, 0.02, f.back.z * 0.08);
                }
                if (e.chance(1f)) {
                    e.add(ParticleTypes.LAVA, f.center.add(f.back.scale(0.5)), 0, 0, 0);
                }
                e.sound(player, SoundEvents.BLAZE_SHOOT, 0.45f, 0.55f);
            }
            case SERAPH -> {
                int ring = e.count(24);
                for (int i = 0; i < ring; i++) {
                    double angle = i / (double) ring * Math.PI * 2;
                    Vec3 offset = f.side.normalize().scale(Math.cos(angle) * 1.3).add(f.up.scale(Math.sin(angle) * 1.3));
                    e.add(ParticleTypes.END_ROD, f.center.add(f.back.scale(0.4)).add(offset), f.back.scale(0.12));
                }
                for (int i = e.count(8); i > 0; i--) {
                    e.add(GOLD, f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()), 0, 0.02, 0);
                }
                e.sound(player, SoundEvents.BEACON_POWER_SELECT, 0.35f, 1.6f);
            }
            case PSYCHE -> {
                for (int i = e.count(14); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.up.scale(random.nextDouble() * 0.6));
                    e.add(ParticleTypes.SOUL_FIRE_FLAME, p, f.back.x * 0.06, 0.03, f.back.z * 0.06);
                }
                e.sound(player, SoundEvents.SOUL_ESCAPE.value(), 0.6f, 1.2f);
            }
            case FIRE -> {
                for (int i = e.count(14); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble());
                    e.add(ParticleTypes.FLAME, p, f.back.scale(0.12 + random.nextDouble() * 0.12).add(0, 0.03, 0));
                }
                for (int i = e.count(6); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.3));
                    e.add(SPARK, p, f.back.scale(0.1).add(0, 0.08, 0));
                }
                e.sound(player, SoundEvents.FIRECHARGE_USE, 0.35f, 1.3f);
            }
            case WATER -> {
                for (int i = e.count(14); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, 0.3 + random.nextDouble() * 0.7);
                    e.add(ParticleTypes.SPLASH, p, f.back.scale(0.1).add(0, 0.1, 0));
                }
                for (int i = e.count(6); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.2));
                    e.add(ParticleTypes.FALLING_WATER, p, 0, 0, 0);
                }
                e.sound(player, SoundEvents.BOAT_PADDLE_WATER, 0.5f, 0.9f);
            }
            case EARTH -> {
                for (int i = e.count(12); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.2));
                    e.add(i % 4 == 0 ? MOSS_CHIP : STONE_CHIP, p, f.back.scale(0.1).add(0, 0.05, 0));
                }
                for (int i = e.count(4); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.4));
                    e.add(ParticleTypes.POOF, p, f.back.scale(0.04));
                }
                e.sound(player, SoundEvents.BASALT_BREAK, 0.6f, 0.6f);
            }
            case AIR -> {
                for (int s = -1; s <= 1; s += 2) {
                    if (e.chance(1f)) {
                        e.add(ParticleTypes.SMALL_GUST, f.alongWing(s, 0.9).add(f.back.scale(0.5)), 0, 0, 0);
                    }
                }
                for (int i = e.count(10); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.3));
                    e.add(WISP, p, f.back.scale(0.2));
                }
                e.sound(player, SoundEvents.BREEZE_IDLE_AIR, 0.6f, 1.2f);
            }
            case STORM -> {
                for (int i = e.count(14); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble());
                    e.add(ParticleTypes.ELECTRIC_SPARK, p, f.back.scale(0.2 + random.nextDouble() * 0.2));
                }
                e.sound(player, SoundEvents.LIGHTNING_BOLT_IMPACT, 0.25f, 1.8f);
            }
            case AURORA -> {
                for (int i = e.count(16); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.up.scale(random.nextDouble() * 0.5));
                    e.add(i % 3 == 0 ? AURORA_VIOLET : AURORA_GREEN, p, f.back.scale(0.08).add(0, 0.03, 0));
                }
                e.sound(player, SoundEvents.AMETHYST_BLOCK_RESONATE, 0.5f, 1.2f);
            }
            case SHADOW -> {
                for (int i = e.count(12); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.3));
                    e.add(SHADE, p, f.back.scale(0.12));
                }
                for (int i = e.count(4); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.5));
                    e.add(ParticleTypes.LARGE_SMOKE, p, f.back.scale(0.06));
                }
                e.sound(player, SoundEvents.ENDERMAN_TELEPORT, 0.25f, 0.6f);
            }
            case NEBULA -> {
                for (int i = e.count(12); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble());
                    e.add(ParticleTypes.END_ROD, p, f.back.scale(0.06 + random.nextDouble() * 0.06));
                }
                for (int i = e.count(8); i > 0; i--) {
                    e.add(STAR, f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.3)), 0, 0, 0);
                }
                e.sound(player, SoundEvents.AMETHYST_CLUSTER_BREAK, 0.4f, 1.5f);
            }
            case FOREST -> {
                for (int i = e.count(12); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.2));
                    e.add(ParticleTypes.CHERRY_LEAVES, p, 0, 0, 0);
                }
                e.sound(player, SoundEvents.AZALEA_LEAVES_BREAK, 0.6f, 0.9f);
            }
            case FEATHERS -> {
                for (int i = e.count(3); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, 0.5 + random.nextDouble() * 0.5).add(f.back.scale(0.2));
                    e.add(FEATHER, p, f.back.scale(0.08).add(0, -0.04, 0));
                }
                e.sound(player, SoundEvents.PARROT_FLY, 0.4f, 0.8f);
            }
            case AMETHYST -> {
                for (int i = e.count(8); i > 0; i--) {
                    e.add(AMETHYST_DUST, f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()), f.back.scale(0.08));
                }
                e.sound(player, SoundEvents.AMETHYST_BLOCK_CHIME, 0.5f, 1.0f);
            }
            case COPPER -> {
                for (int i = e.count(6); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()).add(f.back.scale(0.2));
                    e.add(ParticleTypes.SCRAPE, p, f.back.scale(0.1));
                }
                e.sound(player, SoundEvents.COPPER_HIT, 0.6f, 0.8f);
            }
            case FOSSIL -> {
                for (int i = e.count(6); i > 0; i--) {
                    e.add(AMBER, f.alongWing(i % 2 == 0 ? 1 : -1, random.nextDouble()), f.back.scale(0.05));
                }
            }
            case PTERO -> {
                for (int i = e.count(4); i > 0; i--) {
                    Vec3 p = f.alongWing(i % 2 == 0 ? 1 : -1, 0.8 + random.nextDouble() * 0.2).add(f.back.scale(0.3));
                    e.add(ParticleTypes.POOF, p, f.back.scale(0.05));
                }
                e.sound(player, SoundEvents.PHANTOM_FLAP, 0.7f, 0.6f);
            }
            default -> {
            }
        }
    }

    private WingEffects() {
    }
}
