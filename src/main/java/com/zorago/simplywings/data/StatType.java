package com.zorago.simplywings.data;

import com.zorago.simplywings.config.ServerConfig;
import java.util.function.ToDoubleFunction;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum StatType {
    ENDURANCE("endurance", true, WingStats::endurance, ServerConfig.ENDURANCE_MIN, ServerConfig.ENDURANCE_MAX),
    ACCELERATION("acceleration", true, WingStats::acceleration, ServerConfig.ACCELERATION_MIN, ServerConfig.ACCELERATION_MAX),
    SPEED("speed", true, WingStats::speed, ServerConfig.SPEED_MIN, ServerConfig.SPEED_MAX),
    HANDLING("handling", true, WingStats::handling, ServerConfig.HANDLING_MIN, ServerConfig.HANDLING_MAX),
    STABILITY("stability", true, WingStats::stability, ServerConfig.STABILITY_MIN, ServerConfig.STABILITY_MAX),
    GLIDE("glide", false, WingStats::glide, ServerConfig.GLIDE_MIN, ServerConfig.GLIDE_MAX),
    LIFT("lift", false, WingStats::lift, ServerConfig.LIFT_MIN, ServerConfig.LIFT_MAX);

    public static final float MAX_SCORE = 100f;

    private final String key;
    private final boolean visible;
    private final ToDoubleFunction<WingStats> getter;
    private final ModConfigSpec.DoubleValue atZero;
    private final ModConfigSpec.DoubleValue atMax;

    StatType(String key, boolean visible, ToDoubleFunction<WingStats> getter, ModConfigSpec.DoubleValue atZero,
             ModConfigSpec.DoubleValue atMax) {
        this.key = key;
        this.visible = visible;
        this.getter = getter;
        this.atZero = atZero;
        this.atMax = atMax;
    }

    public String key() {
        return key;
    }

    public boolean visible() {
        return visible;
    }

    public float score(WingStats stats) {
        return (float) getter.applyAsDouble(stats);
    }

    public double value(float score) {
        double t = Math.max(0, score) / MAX_SCORE;
        return atZero.get() + (atMax.get() - atZero.get()) * t;
    }
}
