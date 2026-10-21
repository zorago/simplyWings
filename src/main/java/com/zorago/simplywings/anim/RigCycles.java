package com.zorago.simplywings.anim;

public final class RigCycles {
    private static final int[] CHANNELS = {RigPose.STROKE, RigPose.SWEEP, RigPose.ELBOW, RigPose.WRIST, RigPose.TWIST,
            RigPose.FAN, RigPose.HAND_TWIST};
    private static final float[][] FLAP = {
            {-0.75f, 0.0f, 1.0f, 1.05f, -0.3f, -0.9f, -0.75f},
            {0.25f, -0.15f, -0.25f, -0.1f, 0.25f, 0.35f, 0.25f},
            {0.1f, 0.55f, 0.25f, 0.0f, -0.05f, 0.0f, 0.1f},
            {0.1f, 0.7f, 0.35f, -0.1f, -0.1f, 0.0f, 0.1f},
            {0.0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f, 0.0f},
            {0.1f, -0.2f, 0.1f, 0.25f, 0.2f, 0.05f, 0.1f},
            {0.0f, -0.5f, -0.2f, 0.0f, 0.15f, 0.05f, 0.0f}};
    private static final float LAG_ELBOW = 0.04f;
    private static final float LAG_WRIST = 0.07f;
    private static final float LAG_TIPS = 0.1f;

    public static void flap(float t, float up, float down, float amplitude, RigPose out) {
        float[] times = times(up, down);
        for (int i = 0; i < CHANNELS.length; i++) {
            out.add(CHANNELS[i], amplitude * sample(times, FLAP[i], t));
        }
        float stroke = sample(times, FLAP[0], t);
        out.add(RigPose.BEND_ELBOW, amplitude * 0.2f * (sample(times, FLAP[0], wrap(t - LAG_ELBOW)) - stroke));
        out.add(RigPose.BEND_WRIST, amplitude * 0.3f * (sample(times, FLAP[0], wrap(t - LAG_WRIST)) - stroke));
        out.add(RigPose.BEND_TIPS, amplitude * 0.35f * (sample(times, FLAP[0], wrap(t - LAG_TIPS)) - stroke));
    }

    private static float[] times(float up, float down) {
        return new float[]{0f, 0.5f * up, up, up + 0.15f * down, up + 0.6f * down, up + down, 1f};
    }

    private static float sample(float[] times, float[] values, float t) {
        int unique = values.length - 1;
        int i = 0;
        while (i < unique - 1 && t >= times[i + 1]) {
            i++;
        }
        float span = Math.max(1e-4f, times[i + 1] - times[i]);
        float u = Math.min(1f, Math.max(0f, (t - times[i]) / span));
        float p0 = values[Math.floorMod(i - 1, unique)];
        float p1 = values[i];
        float p2 = values[(i + 1) % unique];
        float p3 = values[(i + 2) % unique];
        float u2 = u * u;
        float u3 = u2 * u;
        return 0.5f * (2f * p1 + (p2 - p0) * u + (2f * p0 - 5f * p1 + 4f * p2 - p3) * u2 + (3f * p1 - p0 - 3f * p2 + p3) * u3);
    }

    private static float wrap(float t) {
        return t - (float) Math.floor(t);
    }

    private RigCycles() {
    }
}
