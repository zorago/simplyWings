package com.zorago.simplywings.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WingStats(float stamina, float acceleration, float maxSpeed, float handling, float stabilization) {
    public static final WingStats ZERO = new WingStats(0, 0, 0, 0, 0);

    public static final Codec<WingStats> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.optionalFieldOf("stamina", 0f).forGetter(WingStats::stamina),
            Codec.FLOAT.optionalFieldOf("acceleration", 0f).forGetter(WingStats::acceleration),
            Codec.FLOAT.optionalFieldOf("max_speed", 0f).forGetter(WingStats::maxSpeed),
            Codec.FLOAT.optionalFieldOf("handling", 0f).forGetter(WingStats::handling),
            Codec.FLOAT.optionalFieldOf("stabilization", 0f).forGetter(WingStats::stabilization)
    ).apply(i, WingStats::new));

    public WingStats add(WingStats o) {
        return new WingStats(stamina + o.stamina, acceleration + o.acceleration, maxSpeed + o.maxSpeed,
                handling + o.handling, stabilization + o.stabilization);
    }
}
