package com.zorago.simplywings.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue GRAVITY;
    public static final ModConfigSpec.DoubleValue TERMINAL_VELOCITY;
    public static final ModConfigSpec.DoubleValue OVERSPEED_FRICTION;
    public static final ModConfigSpec.DoubleValue BRAKE_FACTOR;
    public static final ModConfigSpec.DoubleValue STOP_SPEED_FACTOR;
    public static final ModConfigSpec.DoubleValue ZERO_STAMINA_SINK_SPEED;
    public static final ModConfigSpec.IntValue TAKEOFF_CHARGE_TICKS;
    public static final ModConfigSpec.DoubleValue TAKEOFF_PITCH;
    public static final ModConfigSpec.DoubleValue TAKEOFF_SPEED_FACTOR;

    public static final ModConfigSpec.DoubleValue STABILIZED_RAMP_TICKS;
    public static final ModConfigSpec.DoubleValue STABILIZED_RAMP_TICKS_PER_POINT;
    public static final ModConfigSpec.DoubleValue PERTURBATION_AMPLITUDE;
    public static final ModConfigSpec.DoubleValue DRIFT_AMPLITUDE;

    public static final ModConfigSpec.DoubleValue FLAP_COST;
    public static final ModConfigSpec.DoubleValue STABILIZED_DRAIN;
    public static final ModConfigSpec.DoubleValue GROUND_REGEN;
    public static final ModConfigSpec.DoubleValue GLIDE_REGEN;
    public static final ModConfigSpec.DoubleValue STAMINA_SYNC_FRACTION;
    public static final ModConfigSpec.IntValue RESCUE_JUMPS;
    public static final ModConfigSpec.DoubleValue AIR_JUMP_COST;

    public static final ModConfigSpec.DoubleValue CRASH_THRESHOLD;
    public static final ModConfigSpec.DoubleValue CRASH_DAMAGE_PER_SPEED;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.push("physics");
        GRAVITY = b.comment("Gravity used by wing flight, in m/s².")
                .defineInRange("gravity", 9.81, 1.0, 40.0);
        TERMINAL_VELOCITY = b.comment("Terminal velocity of a vertical dive, in m/s (1 block = 1 m).",
                        "WARNING: above 55 m/s the server has to load and generate terrain faster than most servers can handle.")
                .defineInRange("terminalVelocity", 55.0, 10.0, 150.0);
        OVERSPEED_FRICTION = b.comment("Extra friction above cruise speed, in 1/s.")
                .defineInRange("overspeedFriction", 0.6, 0.0, 10.0);
        BRAKE_FACTOR = b.comment("Brake deceleration = acceleration stat × this factor, in m/s².")
                .defineInRange("brakeFactor", 2.0, 0.1, 20.0);
        STOP_SPEED_FACTOR = b.comment("Braking below (min speed × this factor) stops the player and switches to stabilized mode.")
                .defineInRange("stopSpeedFactor", 0.6, 0.05, 1.0);
        ZERO_STAMINA_SINK_SPEED = b.comment("Sink speed while braking with no stamina left, in m/s.")
                .defineInRange("zeroStaminaSinkSpeed", 3.0, 0.5, 20.0);
        TAKEOFF_CHARGE_TICKS = b.comment("Ticks the travel key must be held on the ground to take off.")
                .defineInRange("takeoffChargeTicks", 20, 1, 100);
        TAKEOFF_PITCH = b.comment("Climb angle of the ground takeoff, in degrees.")
                .defineInRange("takeoffPitch", 40.0, 5.0, 85.0);
        TAKEOFF_SPEED_FACTOR = b.comment("Ground takeoff speed = min speed × this factor.")
                .defineInRange("takeoffSpeedFactor", 1.5, 1.0, 5.0);
        b.pop();

        b.push("stabilized");
        STABILIZED_RAMP_TICKS = b.comment("Ticks to reach full speed at stabilization 10.")
                .defineInRange("rampTicks", 8.0, 1.0, 100.0);
        STABILIZED_RAMP_TICKS_PER_POINT = b.comment("Extra ramp ticks per missing stabilization point.")
                .defineInRange("rampTicksPerMissingPoint", 1.5, 0.0, 20.0);
        PERTURBATION_AMPLITUDE = b.comment("Vertical oscillation per beat at stabilization 0, in m/s.")
                .defineInRange("perturbationAmplitude", 1.2, 0.0, 10.0);
        DRIFT_AMPLITUDE = b.comment("Random drift per beat at stabilization 0, in m/s.")
                .defineInRange("driftAmplitude", 0.6, 0.0, 10.0);
        b.pop();

        b.push("stamina");
        FLAP_COST = b.defineInRange("flapCost", 5.0, 0.0, 1000.0);
        STABILIZED_DRAIN = b.comment("Stamina per second while hovering.")
                .defineInRange("stabilizedDrain", 2.5, 0.0, 1000.0);
        GROUND_REGEN = b.comment("Stamina per second on the ground.")
                .defineInRange("groundRegen", 20.0, 0.0, 1000.0);
        GLIDE_REGEN = b.comment("Stamina per second while gliding in travel mode.")
                .defineInRange("glideRegen", 3.0, 0.0, 1000.0);
        STAMINA_SYNC_FRACTION = b.comment("The server corrects the client when its stamina drifts by this fraction of the max.")
                .defineInRange("syncFraction", 0.2, 0.01, 1.0);
        RESCUE_JUMPS = b.comment("Free jumps available when stamina is below the air jump cost. Reset on the ground.")
                .defineInRange("rescueJumps", 2, 0, 10);
        AIR_JUMP_COST = b.comment("Stamina spent per wing-assisted air jump.")
                .defineInRange("airJumpCost", 20.0, 0.0, 1000.0);
        b.pop();

        b.push("damage");
        CRASH_THRESHOLD = b.comment("Impact speed (perpendicular to the surface) below which a crash does no damage, in m/s.")
                .defineInRange("crashThreshold", 10.0, 0.0, 200.0);
        CRASH_DAMAGE_PER_SPEED = b.comment("Damage per m/s above the threshold.")
                .defineInRange("crashDamagePerSpeed", 0.5, 0.0, 100.0);
        b.pop();

        SPEC = b.build();
    }

    private ServerConfig() {
    }
}
