package com.zorago.simplywings.anim;

public final class WingMotion {
    public static final int GROUND = 0;
    public static final int CHARGE = 1;
    public static final int AIR = 2;
    public static final int LANDING = 3;
    public static final int HOVER = 4;
    public static final int CRUISE = 5;
    public static final int DIVE = 6;
    public static final int BRAKE = 7;
    public static final int TUCK = 8;
    public static final int COUNT = 9;

    public final float[] w = new float[COUNT];
    public float time;
    public float partialTick;
    public float stall;
    public float beat = -1f;
    public float beatWeight;
    public float hoverPhase;
    public float hoverBeat;
    public float buzz;
    public float bank;
    public float diveDepth;
    public float rouse;
    public float rouseProgress;
    public float speed;
    public float climb;
    public float flapRate;
    public float hoverRate;
    public float upFraction;
    public float downFraction;
    public boolean idle;

    public float w(int state) {
        return w[state];
    }

    public boolean beating() {
        return beat >= 0f;
    }
}
