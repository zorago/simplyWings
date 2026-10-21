package com.zorago.simplywings.anim;

public final class Cycle {
    private final float[] times;
    private final float[] values;

    private Cycle(float[] times, float[] values) {
        this.times = times;
        this.values = values;
    }

    public static Cycle of(float[] times, float... values) {
        if (times.length != values.length + 1) {
            throw new IllegalArgumentException("A cycle needs one more time than values, the last time closes the loop");
        }
        return new Cycle(times, values);
    }

    public static float[] beat(float up, float down) {
        return new float[]{0f, 0.5f * up, up, up + 0.15f * down, up + 0.6f * down, up + down, 1f};
    }

    public float at(float t) {
        t = wrap(t);
        int n = values.length;
        int i = 0;
        while (i < n - 1 && t >= times[i + 1]) {
            i++;
        }
        float span = Math.max(1e-4f, times[i + 1] - times[i]);
        float u = Math.min(1f, Math.max(0f, (t - times[i]) / span));
        float p0 = values[Math.floorMod(i - 1, n)];
        float p1 = values[i];
        float p2 = values[(i + 1) % n];
        float p3 = values[(i + 2) % n];
        float u2 = u * u;
        float u3 = u2 * u;
        return 0.5f * (2f * p1 + (p2 - p0) * u + (2f * p0 - 5f * p1 + 4f * p2 - p3) * u2 + (3f * p1 - p0 - 3f * p2 + p3) * u3);
    }

    public float lagged(float t, float lag) {
        return at(t - lag) - at(t);
    }

    public static float wrap(float t) {
        return t - (float) Math.floor(t);
    }
}
