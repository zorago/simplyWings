package com.zorago.simplywings.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WingStats(float endurance, float acceleration, float speed, float handling, float stability, float glide,
                        float lift) {
    public static final WingStats ZERO = new WingStats(0, 0, 0, 0, 0, 0, 0);

    public static final Codec<WingStats> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.optionalFieldOf("endurance", 0f).forGetter(WingStats::endurance),
            Codec.FLOAT.optionalFieldOf("acceleration", 0f).forGetter(WingStats::acceleration),
            Codec.FLOAT.optionalFieldOf("speed", 0f).forGetter(WingStats::speed),
            Codec.FLOAT.optionalFieldOf("handling", 0f).forGetter(WingStats::handling),
            Codec.FLOAT.optionalFieldOf("stability", 0f).forGetter(WingStats::stability),
            Codec.FLOAT.optionalFieldOf("glide", 0f).forGetter(WingStats::glide),
            Codec.FLOAT.optionalFieldOf("lift", 0f).forGetter(WingStats::lift)
    ).apply(i, WingStats::new));

    public WingStats add(WingStats o) {
        return new WingStats(endurance + o.endurance, acceleration + o.acceleration, speed + o.speed,
                handling + o.handling, stability + o.stability, glide + o.glide, lift + o.lift);
    }

    public WingStats capped() {
        return new WingStats(cap(endurance), cap(acceleration), cap(speed), cap(handling), cap(stability), cap(glide),
                cap(lift));
    }

    private static float cap(float v) {
        return Math.max(0f, Math.min(StatType.MAX_SCORE, v));
    }
}
