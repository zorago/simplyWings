package com.zorago.simplywings.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue CAMERA_DISTANCE;
    public static final ModConfigSpec.IntValue FREEZE_TICKS;
    public static final ModConfigSpec.IntValue CATCH_UP_TICKS;
    public static final ModConfigSpec.IntValue INTO_HEAD_TICKS;
    public static final ModConfigSpec.IntValue DOUBLE_TAP_TICKS;
    public static final ModConfigSpec.EnumValue<RampCurve> STABILIZED_CURVE;
    public static final ModConfigSpec.DoubleValue CAMERA_ROLL;
    public static final ModConfigSpec.BooleanValue PARTICLES;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("camera");
        CAMERA_DISTANCE = b.defineInRange("distance", 5.0, 1.0, 20.0);
        FREEZE_TICKS = b.comment("Ticks the camera stays frozen in the world when travel mode starts.")
                .defineInRange("freezeTicks", 4, 0, 40);
        CATCH_UP_TICKS = b.comment("Ticks the camera takes to catch up behind the player.")
                .defineInRange("catchUpTicks", 10, 1, 60);
        INTO_HEAD_TICKS = b.comment("Ticks the camera takes to fly back into the head when stopping.")
                .defineInRange("intoHeadTicks", 5, 1, 40);
        CAMERA_ROLL = b.comment("Share of the body banking applied to the camera roll.")
                .defineInRange("roll", 0.25, 0.0, 1.0);
        b.pop();
        b.push("controls");
        DOUBLE_TAP_TICKS = b.comment("Max ticks between two jump presses to toggle hovering. A slower second press is an air jump.")
                .defineInRange("doubleTapTicks", 4, 2, 10);
        STABILIZED_CURVE = b.comment("Speed ramp of the stabilized mode.")
                .defineEnum("stabilizedCurve", RampCurve.EASE_IN);
        b.pop();
        b.push("visuals");
        PARTICLES = b.define("particles", true);
        b.pop();
        SPEC = b.build();
    }

    public enum RampCurve {
        EASE_IN,
        SMOOTHSTEP;

        public double apply(double x) {
            double t = Math.max(0, Math.min(1, x));
            return this == EASE_IN ? t * t : t * t * (3 - 2 * t);
        }
    }

    private ClientConfig() {
    }
}
