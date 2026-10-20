package com.zorago.simplywings.client;

import net.minecraft.world.phys.Vec3;

public final class FlightMath {
    public static Vec3 look(double yawDeg, double pitchDeg) {
        double yaw = Math.toRadians(yawDeg);
        double pitch = Math.toRadians(pitchDeg);
        double cos = Math.cos(pitch);
        return new Vec3(-Math.sin(yaw) * cos, -Math.sin(pitch), Math.cos(yaw) * cos);
    }

    public static float yawOf(Vec3 dir) {
        return (float) Math.toDegrees(Math.atan2(-dir.x, dir.z));
    }

    public static float pitchOf(Vec3 dir) {
        return (float) Math.toDegrees(Math.asin(Math.max(-1, Math.min(1, -dir.y))));
    }

    public static double clamp(double v, double min, double max) {
        return v < min ? min : Math.min(v, max);
    }

    private FlightMath() {
    }
}
