package com.zorago.simplywings.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue GRAVITY;
    public static final ModConfigSpec.DoubleValue TERMINAL_VELOCITY;
    public static final ModConfigSpec.DoubleValue OVERSPEED_FRICTION;
    public static final ModConfigSpec.DoubleValue AIR_DRAG;
    public static final ModConfigSpec.DoubleValue GLIDE_DRAG_SPEED;
    public static final ModConfigSpec.DoubleValue DIVE_DRAG_SPEED;
    public static final ModConfigSpec.DoubleValue BRAKE_FACTOR;
    public static final ModConfigSpec.DoubleValue TUCK_DRAG_FACTOR;
    public static final ModConfigSpec.DoubleValue TUCK_MIN_PITCH;
    public static final ModConfigSpec.DoubleValue STOP_SPEED_FACTOR;
    public static final ModConfigSpec.DoubleValue ZERO_STAMINA_SINK_SPEED;
    public static final ModConfigSpec.IntValue TAKEOFF_CHARGE_TICKS;
    public static final ModConfigSpec.DoubleValue TAKEOFF_PITCH;
    public static final ModConfigSpec.DoubleValue LAUNCH_SPEED_FRACTION;
    public static final ModConfigSpec.DoubleValue AIR_JUMP_BASE;
    public static final ModConfigSpec.DoubleValue AIR_JUMP_PER_BEAT;

    public static final ModConfigSpec.DoubleValue STABILIZED_RAMP_TICKS;
    public static final ModConfigSpec.DoubleValue STABILIZED_RAMP_TICKS_PER_POINT;
    public static final ModConfigSpec.DoubleValue PERTURBATION_SWING;
    public static final ModConfigSpec.DoubleValue DRIFT_AMPLITUDE;

    public static final ModConfigSpec.DoubleValue FLAP_COST;
    public static final ModConfigSpec.DoubleValue STABILIZED_DRAIN;
    public static final ModConfigSpec.DoubleValue GROUND_REGEN;
    public static final ModConfigSpec.DoubleValue GLIDE_REGEN;
    public static final ModConfigSpec.DoubleValue STAMINA_SYNC_FRACTION;
    public static final ModConfigSpec.IntValue FURY_INVISIBILITY_TICKS;
    public static final ModConfigSpec.DoubleValue CLOUD_MIN_Y;
    public static final ModConfigSpec.DoubleValue CLOUD_MAX_Y;
    public static final ModConfigSpec.IntValue CLOUD_ESSENCE_PER_SECOND;
    public static final ModConfigSpec.BooleanValue CLOUD_HOVER_HARVEST;
    public static final ModConfigSpec.IntValue RESCUE_JUMPS;
    public static final ModConfigSpec.DoubleValue AIR_JUMP_COST;

    public static final ModConfigSpec.DoubleValue CRASH_THRESHOLD;
    public static final ModConfigSpec.DoubleValue CRASH_DAMAGE_PER_SPEED;

    public static final ModConfigSpec.DoubleValue ENDURANCE_MIN;
    public static final ModConfigSpec.DoubleValue ENDURANCE_MAX;
    public static final ModConfigSpec.DoubleValue ACCELERATION_MIN;
    public static final ModConfigSpec.DoubleValue ACCELERATION_MAX;
    public static final ModConfigSpec.DoubleValue SPEED_MIN;
    public static final ModConfigSpec.DoubleValue SPEED_MAX;
    public static final ModConfigSpec.DoubleValue HANDLING_MIN;
    public static final ModConfigSpec.DoubleValue HANDLING_MAX;
    public static final ModConfigSpec.DoubleValue STABILITY_MIN;
    public static final ModConfigSpec.DoubleValue STABILITY_MAX;
    public static final ModConfigSpec.DoubleValue GLIDE_MIN;
    public static final ModConfigSpec.DoubleValue GLIDE_MAX;
    public static final ModConfigSpec.DoubleValue LIFT_MIN;
    public static final ModConfigSpec.DoubleValue LIFT_MAX;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.push("physics");
        GRAVITY = b.comment("Gravity used by wing flight, in m/s².")
                .defineInRange("gravity", 9.81, 1.0, 40.0);
        TERMINAL_VELOCITY = b.comment("Terminal velocity of a vertical dive, in m/s (1 block = 1 m).",
                        "WARNING: above 55 m/s the server has to load and generate terrain faster than most servers can handle.")
                .defineInRange("terminalVelocity", 110.0, 10.0, 300.0);
        GLIDE_DRAG_SPEED = b.comment("Air drag in level flight, expressed as the speed where it would equal gravity, in m/s.",
                        "Higher = less speed lost between wing beats.")
                .defineInRange("glideDragSpeed", 150.0, 10.0, 500.0);
        DIVE_DRAG_SPEED = b.comment("Terminal speed of an open-wing vertical dive, in m/s.",
                        "Only a tucked dive (falcon stoop) goes beyond it, up to terminalVelocity.")
                .defineInRange("diveDragSpeed", 60.0, 10.0, 300.0);
        AIR_DRAG = b.comment("Air drag multiplier. Below 1, speed bleeds off more slowly.")
                .defineInRange("airDrag", 1.0, 0.1, 2.0);
        OVERSPEED_FRICTION = b.comment("Extra friction above cruise speed, in 1/s.")
                .defineInRange("overspeedFriction", 0.6, 0.0, 10.0);
        TUCK_DRAG_FACTOR = b.comment("Air drag multiplier with wings tucked in a dive (falcon stoop). Lower = faster acceleration toward terminal velocity.")
                .defineInRange("tuckDragFactor", 0.35, 0.05, 1.0);
        TUCK_MIN_PITCH = b.comment("Minimum dive angle (degrees, nose down) to tuck the wings by holding sprint above cruise speed.")
                .defineInRange("tuckMinPitch", 15.0, 0.0, 85.0);
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
        LAUNCH_SPEED_FRACTION = b.comment("Entering travel mode (ground takeoff or from hovering) launches at this fraction of max speed.")
                .defineInRange("launchSpeedFraction", 0.5, 0.0, 1.0);
        b.pop();

        b.push("stabilized");
        STABILIZED_RAMP_TICKS = b.comment("Ticks to reach full speed at stabilization 10.")
                .defineInRange("rampTicks", 8.0, 1.0, 100.0);
        STABILIZED_RAMP_TICKS_PER_POINT = b.comment("Extra ramp ticks per missing stabilization point.")
                .defineInRange("rampTicksPerMissingPoint", 1.5, 0.0, 20.0);
        PERTURBATION_SWING = b.comment("Swing of the hover oscillation at stabilization 0, in blocks (up/down and back/forth).",
                        "Scaled by (10 - stabilization) / 10. Continuous wings get random buffeting of the same size instead.")
                .defineInRange("perturbationSwing", 0.6, 0.0, 5.0);
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
        AIR_JUMP_BASE = b.comment("Upward speed of an air jump before the wing bonus, in m/s (a vanilla jump is about 8.4).")
                .defineInRange("airJumpBase", 6.0, 0.0, 50.0);
        AIR_JUMP_PER_BEAT = b.comment("Extra upward speed per m/s gained by one wing beat (acceleration / flap rate).")
                .defineInRange("airJumpPerBeatGain", 0.6, 0.0, 5.0);
        b.pop();

        b.push("fury");
        FURY_INVISIBILITY_TICKS = b.comment("Invisibility granted by the Night / Light charged dash, in ticks.")
                .defineInRange("invisibilityTicks", 300, 0, 72000);
        b.pop();

        b.push("cloud");
        CLOUD_MIN_Y = b.comment("Bottom of the Overworld cloud layer where a Cloud Absorber fills Cloud Flasks.")
                .defineInRange("minY", 186.0, -64.0, 512.0);
        CLOUD_MAX_Y = b.comment("Top of the Overworld cloud layer.")
                .defineInRange("maxY", 204.0, -64.0, 512.0);
        CLOUD_ESSENCE_PER_SECOND = b.comment("Cloud Essence added to the flasks each second of flight inside the cloud layer.")
                .defineInRange("essencePerSecond", 2, 0, 100);
        CLOUD_HOVER_HARVEST = b.comment("Whether hovering (stabilized mode) also harvests, not only travel flight.")
                .define("hoverHarvest", true);
        b.pop();

        b.push("damage");
        CRASH_THRESHOLD = b.comment("Impact speed (perpendicular to the surface) below which a crash does no damage, in m/s.")
                .defineInRange("crashThreshold", 10.0, 0.0, 200.0);
        CRASH_DAMAGE_PER_SPEED = b.comment("Damage per m/s above the threshold.")
                .defineInRange("crashDamagePerSpeed", 0.5, 0.0, 100.0);
        b.pop();

        b.comment("Physical value of each wing stat at score 0 and at score 100 (wing JSON stats use a 0-100 scale).").push("stats");
        ENDURANCE_MIN = b.comment("Stamina points.").defineInRange("enduranceAt0", 50.0, 1.0, 10000.0);
        ENDURANCE_MAX = b.defineInRange("enduranceAt100", 300.0, 1.0, 10000.0);
        ACCELERATION_MIN = b.comment("Speed gained per second of beating (m/s per s), also brake power and air jump.")
                .defineInRange("accelerationAt0", 2.0, 0.0, 100.0);
        ACCELERATION_MAX = b.defineInRange("accelerationAt100", 10.0, 0.0, 100.0);
        SPEED_MIN = b.comment("Max cruise speed while beating, m/s.").defineInRange("speedAt0", 12.0, 1.0, 300.0);
        SPEED_MAX = b.defineInRange("speedAt100", 64.0, 1.0, 300.0);
        HANDLING_MIN = b.comment("Turn rate, degrees per second.").defineInRange("handlingAt0", 40.0, 1.0, 720.0);
        HANDLING_MAX = b.defineInRange("handlingAt100", 150.0, 1.0, 720.0);
        STABILITY_MIN = b.comment("Hover stabilization, 0-10 (10 = creative-like).").defineInRange("stabilityAt0", 0.0, 0.0, 10.0);
        STABILITY_MAX = b.defineInRange("stabilityAt100", 10.0, 0.0, 10.0);
        GLIDE_MIN = b.comment("Air drag multiplier (lower = longer glide).").defineInRange("glideAt0", 1.4, 0.05, 5.0);
        GLIDE_MAX = b.defineInRange("glideAt100", 0.4, 0.05, 5.0);
        LIFT_MIN = b.comment("Stall speed, m/s (lower = harder to stall).").defineInRange("liftAt0", 14.0, 0.5, 100.0);
        LIFT_MAX = b.defineInRange("liftAt100", 3.0, 0.5, 100.0);
        b.pop();

        SPEC = b.build();
    }

    private ServerConfig() {
    }
}
