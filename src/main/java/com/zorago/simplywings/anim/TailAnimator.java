package com.zorago.simplywings.anim;

import com.zorago.simplywings.player.FlightMode;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class TailAnimator {
    public static final int SEGMENTS = 6;
    private static final float[] GROUND = {0.55f, 0.05f, 0.05f, 0.85f, 0.04f, 0.02f};
    private static final float[] HANG = {0.3f, 0.12f, 0.12f, 0.12f, 0.1f, 0.1f};

    private final float[] pitch = new float[SEGMENTS];
    private final float[] yaw = new float[SEGMENTS];
    private final float[] prevPitch = new float[SEGMENTS];
    private final float[] prevYaw = new float[SEGMENTS];
    private boolean initialized;

    public void tick(Player player, FlightMode mode) {
        float t = player.tickCount;
        float turn = Mth.wrapDegrees(player.yBodyRot - player.yBodyRotO);
        float walkSpeed = Math.min(1f, player.walkAnimation.speed());
        float walkPos = player.walkAnimation.position();
        for (int i = 0; i < SEGMENTS; i++) {
            float pitchTarget;
            float yawTarget;
            switch (mode) {
                case TRAVEL -> {
                    pitchTarget = (i == 0 ? 0.12f : 0.03f) + 0.05f * Mth.sin(t * 0.25f - i * 0.8f);
                    yawTarget = -turn * 0.02f * (1 + i * 0.3f) + 0.04f * Mth.sin(t * 0.15f - i * 0.6f);
                }
                case STABILIZED -> {
                    pitchTarget = HANG[i] + 0.06f * Mth.sin(t * 0.12f - i * 0.5f);
                    yawTarget = 0.1f * Mth.sin(t * 0.1f - i * 0.5f) - turn * 0.03f;
                }
                default -> {
                    pitchTarget = player.onGround() ? GROUND[i] : HANG[i];
                    yawTarget = 0.15f * walkSpeed * Mth.sin(walkPos * 0.66f - i * 0.5f)
                            + 0.05f * Mth.sin(t * 0.06f - i * 0.7f) - turn * 0.03f;
                }
            }
            prevPitch[i] = pitch[i];
            prevYaw[i] = yaw[i];
            if (!initialized) {
                pitch[i] = pitchTarget;
                yaw[i] = yawTarget;
                continue;
            }
            float follow = 0.35f - 0.035f * i;
            pitch[i] += (pitchTarget - pitch[i]) * follow;
            yaw[i] += (Mth.clamp(yawTarget, -0.6f, 0.6f) - yaw[i]) * follow;
        }
        initialized = true;
    }

    public float pitch(int segment, float partialTick) {
        return Mth.lerp(partialTick, prevPitch[segment], pitch[segment]);
    }

    public float yaw(int segment, float partialTick) {
        return Mth.lerp(partialTick, prevYaw[segment], yaw[segment]);
    }
}
