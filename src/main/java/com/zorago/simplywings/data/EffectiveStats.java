package com.zorago.simplywings.data;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public record EffectiveStats(ResourceLocation wing, WingDefinition definition, float stamina, float acceleration,
                             float maxSpeed, float handling, float stabilization) {
    public static final float MAX_STABILIZATION = 10f;

    public static Optional<EffectiveStats> compute(RegistryAccess access, Collection<ResourceLocation> collection,
                                                   Optional<ResourceLocation> equipped) {
        if (equipped.isEmpty()) {
            return Optional.empty();
        }
        Optional<WingDefinition> def = WingRegistries.get(access, equipped.get());
        if (def.isEmpty()) {
            return Optional.empty();
        }
        WingStats total = def.get().equipped();
        for (ResourceLocation id : collection) {
            Optional<WingDefinition> owned = WingRegistries.get(access, id);
            if (owned.isPresent()) {
                total = total.add(owned.get().global());
            }
        }
        return Optional.of(new EffectiveStats(equipped.get(), def.get(), Math.max(1f, total.stamina()),
                total.acceleration(), total.maxSpeed(), total.handling(),
                Math.min(MAX_STABILIZATION, Math.max(0f, total.stabilization()))));
    }

    public float minSpeed() {
        return definition.stallSpeed();
    }

    public float flapRate() {
        return definition.flapRate();
    }

    public float hoverRate() {
        return definition.hoverRate();
    }

    public float hoverSpeed() {
        return definition.hoverSpeed();
    }

    public float beatGain() {
        return acceleration / definition.flapRate();
    }
}
