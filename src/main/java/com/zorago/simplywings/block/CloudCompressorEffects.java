package com.zorago.simplywings.block;

import com.zorago.simplywings.sound.ModSounds;
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
    private static final java.util.Map<BlockPos, CompressorSound> SOUNDS = new java.util.HashMap<>();

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
        CompressorSound sound = SOUNDS.get(pos);
        if (sound == null || sound.isStopped()) {
            sound = new CompressorSound(pos.immutable(), cx, pos.getY() + 2.0, cz);
            SOUNDS.put(pos.immutable(), sound);
            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(sound);
        }
        sound.feed(speed, level.getGameTime());
        if (random.nextFloat() < 0.03F * speed) {
            level.playLocalSound(cx, pos.getY() + 1.5, cz, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.5F, 0.8F, false);
        }
    }

    private static final class CompressorSound extends net.minecraft.client.resources.sounds.AbstractTickableSoundInstance {
        private final BlockPos pos;
        private float target;
        private long fed;

        CompressorSound(BlockPos pos, double x, double y, double z) {
            super(ModSounds.COMPRESSOR_LOOP.get(), SoundSource.BLOCKS, net.minecraft.client.resources.sounds.SoundInstance.createUnseededRandom());
            this.pos = pos;
            this.x = x;
            this.y = y;
            this.z = z;
            looping = true;
            delay = 0;
            volume = 0.01f;
        }

        void feed(float speed, long time) {
            target = 0.25F + 0.35F * speed;
            pitch = 0.85F + 0.25F * speed;
            fed = time;
        }

        @Override
        public void tick() {
            net.minecraft.client.multiplayer.ClientLevel level = net.minecraft.client.Minecraft.getInstance().level;
            boolean alive = level != null && level.getGameTime() - fed < 10;
            volume += ((alive ? target : 0) - volume) * 0.1F;
            if (!alive && volume < 0.01F) {
                SOUNDS.remove(pos, this);
                stop();
            }
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }
    }

    private CloudCompressorEffects() {
    }
}
