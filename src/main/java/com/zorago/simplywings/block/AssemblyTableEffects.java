package com.zorago.simplywings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class AssemblyTableEffects {
    private static final int CLIMAX = AssemblyTableBlockEntity.ASSEMBLY_TICKS;
    private static final int WINGS_LENGTH = CLIMAX + 60;
    private static final Vector3f CLOUD = new Vector3f(0.85F, 0.95F, 1.0F);
    private static final Vector3f GOLD = new Vector3f(1.0F, 0.8F, 0.35F);
    private static final Vector3f VIOLET = new Vector3f(0.7F, 0.5F, 0.9F);

    public static void tick(Level level, BlockPos pos, BlockState state, AssemblyTableBlockEntity table) {
        Frame frame = new Frame(level, pos, state.getValue(AssemblyTableBlock.FACING));
        long time = level.getGameTime();
        idle(frame, time, table.fill());
        int t = (int) (time - table.animationStart());
        if (table.animation() == AssemblyTableBlockEntity.EVENT_WINGS && t <= WINGS_LENGTH) {
            wings(frame, t);
        }
    }

    private static void idle(Frame f, long time, float fill) {
        if (fill <= 0) {
            return;
        }
        RandomSource random = f.level.random;
        Vec3 top = f.at(8, 13, 8);
        int motes = 1 + (int) (fill * 5);
        double speed = 0.04 + 0.08 * fill;
        for (int i = 0; i < motes; i++) {
            double angle = time * speed + i * Mth.TWO_PI / motes;
            double radius = 0.62 + 0.06 * Math.sin(time * 0.05 + i);
            double y = top.y + 0.12 + 0.12 * Math.sin(time * 0.09 + i * 1.7);
            f.particle(dust(CLOUD, 0.45F + 0.35F * fill), top.x + Math.cos(angle) * radius, y, top.z + Math.sin(angle) * radius, 0, 0, 0);
        }
        if (random.nextFloat() < 0.05F + 0.25F * fill) {
            Vec3 tank = f.at(13, 18, 9.5);
            f.particle(ParticleTypes.END_ROD, tank.x, tank.y, tank.z,
                    (random.nextDouble() - 0.5) * 0.01, 0.01 + random.nextDouble() * 0.02, (random.nextDouble() - 0.5) * 0.01);
        }
        if (fill > 0.5F && random.nextFloat() < (fill - 0.5F) * 0.6F) {
            Vec3 emblem = f.emblem();
            double angle = random.nextDouble() * Mth.TWO_PI;
            f.particle(ParticleTypes.ENCHANT, emblem.x, emblem.y, emblem.z,
                    Math.cos(angle) * 1.5, random.nextDouble() * 0.6 - 0.8, Math.sin(angle) * 1.5);
        }
    }

    private static void wings(Frame f, int t) {
        Vec3 emblem = f.emblem();
        Vec3 base = f.at(8, 0, 8);
        if (t < CLIMAX) {
            charge(f, t, emblem, base);
        } else if (t == CLIMAX) {
            climax(f, emblem);
        } else {
            aftermath(f, t - CLIMAX, emblem);
        }
    }

    private static void charge(Frame f, int t, Vec3 emblem, Vec3 base) {
        RandomSource random = f.level.random;
        float p = t / (float) CLIMAX;
        for (int k = 0; k < 4; k++) {
            double angle = k * Mth.HALF_PI + t * 0.05;
            double sx = Math.cos(angle) * 2.4;
            double sz = Math.sin(angle) * 2.4;
            double sy = 1.2 + 0.3 * Math.sin(t * 0.2 + k);
            for (int i = 0; i < 2; i++) {
                f.particle(ParticleTypes.ENCHANT, emblem.x, emblem.y, emblem.z,
                        sx + random.nextGaussian() * 0.15, sy - (emblem.y - base.y) + random.nextGaussian() * 0.15, sz + random.nextGaussian() * 0.15);
            }
        }
        double radius = 1.5 - 0.9 * p;
        double height = 1.8 + 1.2 * p;
        for (int strand = 0; strand < 2; strand++) {
            for (int h = 0; h < 3; h++) {
                double angle = t * 0.25 + strand * Math.PI + h * 0.9;
                double y = base.y + 0.2 + ((t * 0.05 + h / 3.0) % 1.0) * height;
                f.particle(dust(strand == 0 ? GOLD : CLOUD, 0.8F + 0.6F * p),
                        base.x + Math.cos(angle) * radius, y, base.z + Math.sin(angle) * radius, 0, 0, 0);
            }
        }
        if (p > 0.4F) {
            int rods = 1 + (int) (p * 3);
            for (int i = 0; i < rods; i++) {
                f.particle(ParticleTypes.END_ROD, emblem.x + random.nextGaussian() * 0.05, emblem.y + 0.2, emblem.z + random.nextGaussian() * 0.05,
                        0, 0.08 + 0.1 * p, 0);
            }
        }
        if (t % 10 == 0) {
            ring(f, base.add(0, 0.05, 0), 1.2 + 0.3 * p, 24, dust(VIOLET, 1.0F));
            f.sound(emblem, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.0F, 0.5F + 1.2F * p);
        }
        if (t == 0) {
            f.sound(emblem, SoundEvents.BEACON_POWER_SELECT, 1.0F, 1.0F);
        }
        if (t == 20 || t == 40 || t == 60) {
            f.sound(emblem, SoundEvents.RESPAWN_ANCHOR_CHARGE, 0.8F, 0.8F + 0.3F * (t / 20.0F));
        }
        if (t == CLIMAX - 12) {
            f.sound(emblem, SoundEvents.BEACON_ACTIVATE, 1.0F, 1.4F);
        }
    }

    private static void climax(Frame f, Vec3 emblem) {
        f.level.addAlwaysVisibleParticle(ParticleTypes.FLASH, true, emblem.x, emblem.y, emblem.z, 0, 0, 0);
        f.level.addAlwaysVisibleParticle(ParticleTypes.FLASH, true, emblem.x, emblem.y + 0.5, emblem.z, 0, 0, 0);
        int count = 80;
        for (int i = 0; i < count; i++) {
            double y = 1 - 2 * (i + 0.5) / count;
            double r = Math.sqrt(1 - y * y);
            double theta = i * 2.399963229728653;
            f.particle(ParticleTypes.FIREWORK, emblem.x, emblem.y, emblem.z, Math.cos(theta) * r * 0.35, y * 0.35, Math.sin(theta) * r * 0.35);
        }
        for (int i = 0; i < 64; i++) {
            double angle = i * Mth.TWO_PI / 64;
            f.particle(ParticleTypes.END_ROD, emblem.x, emblem.y - 0.4, emblem.z, Math.cos(angle) * 0.35, 0, Math.sin(angle) * 0.35);
        }
        f.sound(emblem, SoundEvents.TOTEM_USE, 1.0F, 1.0F);
        f.sound(emblem, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 1.0F, 1.0F);
        f.sound(emblem, SoundEvents.ENDER_DRAGON_FLAP, 1.0F, 0.9F);
        f.sound(emblem, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 1.0F);
    }

    private static void aftermath(Frame f, int t, Vec3 emblem) {
        RandomSource random = f.level.random;
        float q = t / 60.0F;
        if (t == 4) {
            f.sound(emblem, SoundEvents.ENDER_DRAGON_FLAP, 1.0F, 1.1F);
        }
        if (t == 10) {
            f.sound(emblem, SoundEvents.PLAYER_LEVELUP, 0.8F, 1.2F);
        }
        if (q < 0.8F && t % 2 == 0) {
            double flap = Math.sin(t * 0.25) * 0.35;
            for (int side = -1; side <= 1; side += 2) {
                wingOutline(f, emblem, side, flap, 1.0F - q);
            }
        }
        if (random.nextFloat() < 1 - q) {
            for (int i = 0; i < 4; i++) {
                double angle = random.nextDouble() * Mth.TWO_PI;
                double radius = random.nextDouble() * 2.0;
                f.particle(ParticleTypes.END_ROD, emblem.x + Math.cos(angle) * radius, emblem.y + 2.5 + random.nextDouble(),
                        emblem.z + Math.sin(angle) * radius, 0, -0.03, 0);
            }
        }
    }

    private static void wingOutline(Frame f, Vec3 shoulder, int side, double flap, float strength) {
        int points = 12;
        double span = 2.2;
        float scale = 0.6F + 0.6F * strength;
        for (int i = 0; i <= points; i++) {
            double u = i / (double) points;
            double lead = 0.6 * Math.sin(Math.PI * u * 0.9) + 0.25 * u;
            double trail = -0.35 - 0.4 * u + 0.12 * Math.abs(Math.sin(u * Math.PI * 5));
            emitWingPoint(f, shoulder, side, flap, u * span, lead, dust(GOLD, scale));
            emitWingPoint(f, shoulder, side, flap, u * span, trail, dust(CLOUD, scale));
        }
    }

    private static void emitWingPoint(Frame f, Vec3 shoulder, int side, double flap, double lateral, double vertical, ParticleOptions options) {
        double cos = Math.cos(flap);
        double sin = Math.sin(flap);
        double l = lateral * cos - vertical * sin;
        double v = lateral * sin + vertical * cos + 0.3;
        Vec3 offset = f.rotate(side * l, 0);
        f.particle(options, shoulder.x + offset.x, shoulder.y + v, shoulder.z + offset.z, 0, 0, 0);
    }

    private static void ring(Frame f, Vec3 center, double radius, int count, ParticleOptions options) {
        for (int i = 0; i < count; i++) {
            double angle = i * Mth.TWO_PI / count;
            f.particle(options, center.x + Math.cos(angle) * radius, center.y, center.z + Math.sin(angle) * radius, 0, 0, 0);
        }
    }

    private static DustParticleOptions dust(Vector3f color, float scale) {
        return new DustParticleOptions(color, scale);
    }

    private record Frame(Level level, BlockPos pos, Direction facing) {
        Vec3 at(double x, double y, double z) {
            Vec3 offset = rotate(x / 16.0 - 0.5, z / 16.0 - 0.5);
            return new Vec3(pos.getX() + 0.5 + offset.x, pos.getY() + y / 16.0, pos.getZ() + 0.5 + offset.z);
        }

        Vec3 emblem() {
            return at(8, 18, 3);
        }

        Vec3 rotate(double dx, double dz) {
            int steps = switch (facing) {
                case WEST -> 1;
                case NORTH -> 2;
                case EAST -> 3;
                default -> 0;
            };
            for (int i = 0; i < steps; i++) {
                double x = -dz;
                dz = dx;
                dx = x;
            }
            return new Vec3(dx, 0, dz);
        }

        void particle(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
            level.addParticle(options, x, y, z, vx, vy, vz);
        }

        void sound(Vec3 at, SoundEvent sound, float volume, float pitch) {
            level.playLocalSound(at.x, at.y, at.z, sound, SoundSource.BLOCKS, volume, pitch, false);
        }
    }

    private AssemblyTableEffects() {
    }
}
