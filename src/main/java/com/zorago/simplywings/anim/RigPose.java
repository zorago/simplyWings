package com.zorago.simplywings.anim;

import net.minecraft.util.Mth;

public final class RigPose {
    public static final int FOLD = 0;
    public static final int STROKE = 1;
    public static final int SWEEP = 2;
    public static final int TWIST = 3;
    public static final int ELBOW = 4;
    public static final int WRIST = 5;
    public static final int BEND_ELBOW = 6;
    public static final int BEND_WRIST = 7;
    public static final int BEND_TIPS = 8;
    public static final int FAN = 9;
    public static final int HAND_TWIST = 10;
    public static final int ASYM = 11;
    public static final int SIZE = 12;

    public final float[] v = new float[SIZE];

    public float get(int channel) {
        return v[channel];
    }

    public RigPose set(int channel, float value) {
        v[channel] = value;
        return this;
    }

    public RigPose add(int channel, float value) {
        v[channel] += value;
        return this;
    }

    public RigPose blendIn(float weight, RigPose pose) {
        if (weight != 0f) {
            for (int i = 0; i < SIZE; i++) {
                v[i] += weight * pose.v[i];
            }
        }
        return this;
    }

    public RigPose zero() {
        java.util.Arrays.fill(v, 0f);
        return this;
    }

    public RigPose clear() {
        java.util.Arrays.fill(v, 0f);
        v[FAN] = 1f;
        return this;
    }

    public RigPose copy(RigPose other) {
        System.arraycopy(other.v, 0, v, 0, SIZE);
        return this;
    }

    public RigPose lerp(RigPose a, RigPose b, float t) {
        for (int i = 0; i < SIZE; i++) {
            v[i] = Mth.lerp(t, a.v[i], b.v[i]);
        }
        return this;
    }

    public RigPose blend(RigPose target, float t) {
        for (int i = 0; i < SIZE; i++) {
            v[i] += (target.v[i] - v[i]) * t;
        }
        return this;
    }
}
