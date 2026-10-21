package com.zorago.simplywings.client;

import com.zorago.simplywings.data.EffectiveStats;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public final class ClientWings {
    private static final Set<ResourceLocation> COLLECTION = new LinkedHashSet<>();
    private static Optional<ResourceLocation> equipped = Optional.empty();
    private static Optional<ResourceLocation> appearance = Optional.empty();
    private static float stamina;
    private static int rescueJumps;
    private static Optional<EffectiveStats> stats;

    static void apply(List<ResourceLocation> collection, Optional<ResourceLocation> equippedWing,
                      Optional<ResourceLocation> appearanceWing, float staminaValue, int rescue) {
        COLLECTION.clear();
        COLLECTION.addAll(collection);
        equipped = equippedWing;
        appearance = appearanceWing;
        stamina = staminaValue;
        rescueJumps = rescue;
        stats = null;
    }

    static void reset() {
        apply(List.of(), Optional.empty(), Optional.empty(), 0, 0);
    }

    public static Optional<EffectiveStats> stats() {
        if (stats == null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
                return Optional.empty();
            }
            stats = EffectiveStats.compute(mc.level.registryAccess(), COLLECTION, equipped);
        }
        return stats;
    }

    public static Set<ResourceLocation> collection() {
        return COLLECTION;
    }

    public static Optional<ResourceLocation> equipped() {
        return equipped;
    }

    public static Optional<ResourceLocation> appearance() {
        return appearance;
    }

    public static float stamina() {
        return stamina;
    }

    public static void setStamina(float value) {
        stamina = value;
    }

    public static int rescueJumps() {
        return rescueJumps;
    }

    public static void setRescueJumps(int value) {
        rescueJumps = value;
    }

    private ClientWings() {
    }
}
