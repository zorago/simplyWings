package com.zorago.simplywings.data;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public record EffectiveStats(ResourceLocation wing, WingDefinition definition, WingStats scores) {
    public static Optional<EffectiveStats> compute(RegistryAccess access, Collection<ResourceLocation> collection,
                                                   Optional<ResourceLocation> equipped) {
        if (equipped.isEmpty()) {
            return Optional.empty();
        }
        Optional<WingDefinition> def = WingRegistries.get(access, equipped.get());
        if (def.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new EffectiveStats(equipped.get(), def.get(), total(access, collection, def.get())));
    }

    public static WingStats total(RegistryAccess access, Collection<ResourceLocation> collection, WingDefinition def) {
        WingStats total = def.equipped();
        for (ResourceLocation id : collection) {
            Optional<WingDefinition> owned = WingRegistries.get(access, id);
            if (owned.isPresent()) {
                total = total.add(owned.get().global());
            }
        }
        return total.capped().add(def.overcap());
    }

    private double value(StatType type) {
        return type.value(type.score(scores));
    }

    public float stamina() {
        return (float) Math.max(1, value(StatType.ENDURANCE));
    }

    public float acceleration() {
        return (float) value(StatType.ACCELERATION);
    }

    public float maxSpeed() {
        return (float) value(StatType.SPEED);
    }

    public float handling() {
        return (float) value(StatType.HANDLING);
    }

    public float stabilization() {
        return (float) value(StatType.STABILITY);
    }

    public float drag() {
        return (float) value(StatType.GLIDE);
    }

    public float minSpeed() {
        return (float) value(StatType.LIFT);
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
        return acceleration() / definition.flapRate();
    }
}
