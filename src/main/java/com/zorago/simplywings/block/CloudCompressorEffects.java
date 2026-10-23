package com.zorago.simplywings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class CloudCompressorEffects {
    private static final Vector3f GOLD = new Vector3f(1.0F, 0.85F, 0.4F);

    public static void tick(Level level, BlockPos pos, CloudCompressorBlockEntity compressor) {
        float speed = compressor.speed();
        if (speed < 0.05F) {
            return;
        }
        RandomSource random = level.random;
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        if (random.nextFloat() < 0.35F * speed) {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, cx + 0.55 + (random.nextDouble() - 0.5) * 0.15, pos.getY() + 5.05,
                    cz - 0.55 + (random.nextDouble() - 0.5) * 0.15, 0, 0.05 + random.nextDouble() * 0.02, 0);
        }
        if (random.nextFloat() < 0.15F * speed) {
            double side = random.nextBoolean() ? 1.45 : -1.45;
            level.addParticle(ParticleTypes.CLOUD, cx + side, pos.getY() + 1.2 + random.nextDouble() * 0.6, cz,
                    side * 0.04, 0.02, (random.nextDouble() - 0.5) * 0.02);
        }
        if (random.nextFloat() < 0.4F * speed) {
            double angle = random.nextDouble() * Math.PI * 2;
            level.addParticle(new DustParticleOptions(GOLD, 0.7F), cx + Math.cos(angle) * 0.9, pos.getY() + 1.0 + random.nextDouble() * 2.5,
                    cz + Math.sin(angle) * 0.9, 0, 0.01, 0);
        }
        if (random.nextFloat() < 0.02F * speed) {
            level.playLocalSound(cx, pos.getY() + 2.0, cz, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.25F, 0.6F + random.nextFloat() * 0.2F, false);
        }
        if (random.nextFloat() < 0.03F * speed) {
            level.playLocalSound(cx, pos.getY() + 1.5, cz, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.5F, 0.8F, false);
        }
    }

    private CloudCompressorEffects() {
    }
}
