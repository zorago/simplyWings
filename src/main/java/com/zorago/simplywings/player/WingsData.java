package com.zorago.simplywings.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zorago.simplywings.data.EffectiveStats;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public final class WingsData {
    public static final Codec<WingsData> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("collection", List.of()).forGetter(d -> List.copyOf(d.collection)),
            ResourceLocation.CODEC.optionalFieldOf("equipped").forGetter(d -> d.equipped),
            Codec.FLOAT.optionalFieldOf("stamina", 0f).forGetter(d -> d.stamina),
            Codec.INT.optionalFieldOf("rescue_jumps", 0).forGetter(d -> d.rescueJumps)
    ).apply(i, WingsData::new));

    private final Set<ResourceLocation> collection;
    private Optional<ResourceLocation> equipped;
    private float stamina;
    private int rescueJumps;

    private FlightMode mode = FlightMode.NONE;
    private int flags;
    private Optional<EffectiveStats> stats;
    private float lastSentStamina = -1;
    private long lastFlapTick = Long.MIN_VALUE / 2;
    private long lastTravelTick = Long.MIN_VALUE / 2;

    public WingsData() {
        this(List.of(), Optional.empty(), 0f, 0);
    }

    private WingsData(List<ResourceLocation> collection, Optional<ResourceLocation> equipped, float stamina, int rescueJumps) {
        this.collection = new LinkedHashSet<>(collection);
        this.equipped = equipped;
        this.stamina = stamina;
        this.rescueJumps = rescueJumps;
    }

    public Set<ResourceLocation> collection() {
        return collection;
    }

    public boolean unlock(ResourceLocation id) {
        boolean added = collection.add(id);
        stats = null;
        return added;
    }

    public void clearCollection() {
        collection.clear();
        equipped = Optional.empty();
        stats = null;
    }

    public Optional<ResourceLocation> equipped() {
        return equipped;
    }

    public void equip(Optional<ResourceLocation> id) {
        equipped = id;
        stats = null;
    }

    public Optional<EffectiveStats> stats(RegistryAccess access) {
        if (stats == null) {
            stats = EffectiveStats.compute(access, collection, equipped);
        }
        return stats;
    }

    public float stamina() {
        return stamina;
    }

    public void setStamina(float stamina) {
        this.stamina = stamina;
    }

    public int rescueJumps() {
        return rescueJumps;
    }

    public void setRescueJumps(int rescueJumps) {
        this.rescueJumps = rescueJumps;
    }

    public FlightMode mode() {
        return mode;
    }

    public int flags() {
        return flags;
    }

    public void setMode(FlightMode mode, int flags) {
        this.mode = mode;
        this.flags = mode == FlightMode.TRAVEL ? flags : 0;
    }

    public float lastSentStamina() {
        return lastSentStamina;
    }

    public void setLastSentStamina(float value) {
        lastSentStamina = value;
    }

    public long lastFlapTick() {
        return lastFlapTick;
    }

    public void setLastFlapTick(long tick) {
        lastFlapTick = tick;
    }

    public long lastTravelTick() {
        return lastTravelTick;
    }

    public void setLastTravelTick(long tick) {
        lastTravelTick = tick;
    }
}
