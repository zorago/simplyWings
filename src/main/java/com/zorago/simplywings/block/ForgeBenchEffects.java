package com.zorago.simplywings.block;

import com.zorago.simplywings.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public final class ForgeBenchEffects {
    private static final int COMPONENT_LENGTH = 24;
    private static final int ENERGY_LENGTH = 30;
    private static final Vector3f EMBER = new Vector3f(1.0F, 0.55F, 0.2F);
    private static final Vector3f GOLD = new Vector3f(1.0F, 0.8F, 0.35F);

    public static void tick(Level level, BlockPos pos, BlockState state, ForgeBenchBlockEntity bench) {
        RandomSource random = level.random;
        double cx = pos.getX() + 0.5;
        double top = pos.getY() + 15 / 16.0;
        double cz = pos.getZ() + 0.5;
        float fill = bench.fill();
        if (random.nextFloat() < 0.04F + 0.3F * fill) {
            level.addParticle(ParticleTypes.SMOKE, cx + (random.nextDouble() - 0.5) * 0.7, top + 0.05,
                    cz + (random.nextDouble() - 0.5) * 0.7, 0, 0.02, 0);
        }
        if (fill > 0 && random.nextFloat() < 0.1F + 0.3F * fill) {
            level.addParticle(new DustParticleOptions(EMBER, 0.5F + 0.5F * fill), cx + (random.nextDouble() - 0.5) * 0.9,
                    pos.getY() + 0.3 + random.nextDouble() * 0.4, cz + (random.nextDouble() - 0.5) * 0.9, 0, 0, 0);
        }
        int t = (int) (level.getGameTime() - bench.animationStart());
        if (bench.animation() == ForgeBenchBlockEntity.EVENT_COMPONENT && t <= COMPONENT_LENGTH) {
            component(level, random, cx, top, cz, t);
        } else if (bench.animation() == ForgeBenchBlockEntity.EVENT_ENERGY && t <= ENERGY_LENGTH) {
            energy(level, random, cx, top, cz, t);
        }
    }

    private static void component(Level level, RandomSource random, double cx, double top, double cz, int t) {
        if (t == 0 || t == 6 || t == 12) {
            level.playLocalSound(cx, top, cz, ModSounds.FORGE_HAMMER.get(), SoundSource.BLOCKS, 0.8F, 0.95F + t * 0.01F, false);
            for (int i = 0; i < 10; i++) {
                double angle = random.nextDouble() * Mth.TWO_PI;
                double speed = 0.1 + random.nextDouble() * 0.15;
                level.addParticle(ParticleTypes.CRIT, cx, top + 0.05, cz, Math.cos(angle) * speed, 0.15 + random.nextDouble() * 0.2, Math.sin(angle) * speed);
            }
            level.addParticle(ParticleTypes.LAVA, cx, top + 0.1, cz, 0, 0, 0);
        }
        if (t == 14) {
            level.playLocalSound(cx, top, cz, ModSounds.FORGE_QUENCH.get(), SoundSource.BLOCKS, 0.7F, 1.0F, false);
            for (int i = 0; i < 12; i++) {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, cx + (random.nextDouble() - 0.5) * 0.6, top + 0.1,
                        cz + (random.nextDouble() - 0.5) * 0.6, 0, 0.04 + random.nextDouble() * 0.03, 0);
            }
        }
        if (t > 14) {
            level.addParticle(ParticleTypes.SMOKE, cx + (random.nextDouble() - 0.5) * 0.5, top + 0.1, cz + (random.nextDouble() - 0.5) * 0.5, 0, 0.05, 0);
        }
    }

    private static void energy(Level level, RandomSource random, double cx, double top, double cz, int t) {
        if (t == 0) {
            level.playLocalSound(cx, top, cz, ModSounds.INFUSION.get(), SoundSource.BLOCKS, 0.8F, 1.1F, false);
            level.playLocalSound(cx, top, cz, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.5F, false);
        }
        float p = t / (float) ENERGY_LENGTH;
        for (int k = 0; k < 3; k++) {
            double angle = t * 0.4 + k * Mth.TWO_PI / 3;
            double radius = 0.7 * (1 - p);
            level.addParticle(new DustParticleOptions(GOLD, 0.8F), cx + Math.cos(angle) * radius, top + 0.1 + 0.6 * p,
                    cz + Math.sin(angle) * radius, 0, 0, 0);
        }
        if (t % 3 == 0) {
            level.addParticle(ParticleTypes.ENCHANT, cx, top + 0.6, cz, (random.nextDouble() - 0.5) * 2, -0.5, (random.nextDouble() - 0.5) * 2);
        }
    }

    private ForgeBenchEffects() {
    }
}
