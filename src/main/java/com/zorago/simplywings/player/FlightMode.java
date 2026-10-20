package com.zorago.simplywings.player;

public enum FlightMode {
    NONE,
    STABILIZED,
    TRAVEL;

    public static final int FLAG_BRAKE = 1;
    public static final int FLAG_STALL = 2;

    private static final FlightMode[] VALUES = values();

    public static FlightMode byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : NONE;
    }

    public boolean flying() {
        return this != NONE;
    }
}
