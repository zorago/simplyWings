package com.zorago.simplywings.anim;

import net.minecraft.util.Mth;

public final class RigMemory {
    public static final int SIZE = 48;

    private final float[] value = new float[SIZE];
    private final float[] prev = new float[SIZE];
    private final float[] velocity = new float[SIZE];
    private String owner;

    public boolean claim(String rig) {
        if (rig.equals(owner)) {
            return false;
        }
        owner = rig;
        java.util.Arrays.fill(value, 0f);
        java.util.Arrays.fill(prev, 0f);
        java.util.Arrays.fill(velocity, 0f);
        return true;
    }

    public void begin() {
        System.arraycopy(value, 0, prev, 0, SIZE);
    }

    public float get(int slot, float partialTick) {
        return Mth.lerp(partialTick, prev[slot], value[slot]);
    }

    public float now(int slot) {
        return value[slot];
    }

    public void set(int slot, float v) {
        value[slot] = v;
    }

    public void snap(int slot, float v) {
        value[slot] = v;
        prev[slot] = v;
        velocity[slot] = 0f;
    }

    public void spring(int slot, float target, float stiffness, float damping) {
        velocity[slot] = (velocity[slot] + (target - value[slot]) * stiffness) * damping;
        value[slot] += velocity[slot];
    }

    public void ease(int slot, float target, float rate) {
        value[slot] += (target - value[slot]) * rate;
    }

    public float velocity(int slot) {
        return velocity[slot];
    }
}
