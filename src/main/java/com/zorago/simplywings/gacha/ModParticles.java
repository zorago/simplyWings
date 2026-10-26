package com.zorago.simplywings.gacha;

import com.zorago.simplywings.SimplyWings;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, SimplyWings.MOD_ID);
    public static final Map<String, Supplier<SimpleParticleType>> GACHA = new LinkedHashMap<>();

    static {
        for (String kind : new String[]{"ember", "wisp", "pebble", "drop", "spark", "gust", "mote", "shard", "feather", "smoke", "flare"}) {
            GACHA.put(kind, PARTICLES.register("gacha_" + kind, () -> new SimpleParticleType(true)));
        }
    }

    public static SimpleParticleType get(String kind) {
        return GACHA.get(kind).get();
    }

    public static void register(IEventBus modBus) {
        PARTICLES.register(modBus);
    }

    private ModParticles() {
    }
}
