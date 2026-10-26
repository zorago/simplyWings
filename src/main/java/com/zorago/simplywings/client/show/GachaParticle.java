package com.zorago.simplywings.client.show;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class GachaParticle extends TextureSheetParticle {
    public record Kind(float gravity, float friction, float size, int lifeMin, int lifeMax, boolean shrink) {
    }

    private final SpriteSet sprites;
    private final Kind kind;
    private final float startSize;
    private final float spin;

    protected GachaParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites, Kind kind) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.kind = kind;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.gravity = kind.gravity();
        this.friction = kind.friction();
        this.hasPhysics = false;
        this.lifetime = kind.lifeMin() + random.nextInt(Math.max(1, kind.lifeMax() - kind.lifeMin() + 1));
        this.quadSize = kind.size() * (0.7f + random.nextFloat() * 0.6f);
        this.startSize = quadSize;
        this.spin = (random.nextFloat() - 0.5f) * 0.3f;
        this.roll = random.nextFloat() * Mth.TWO_PI;
        this.oRoll = roll;
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (removed) {
            return;
        }
        setSpriteFromAge(sprites);
        oRoll = roll;
        roll += spin;
        float life = (float) age / lifetime;
        if (kind.shrink()) {
            quadSize = startSize * (1f - life * 0.7f);
        }
        alpha = life > 0.7f ? 1f - (life - 0.7f) / 0.3f : 1f;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static net.minecraft.client.particle.ParticleProvider<SimpleParticleType> provider(SpriteSet sprites, Kind kind) {
        return (type, level, x, y, z, vx, vy, vz) -> new GachaParticle(level, x, y, z, vx, vy, vz, sprites, kind);
    }
}
