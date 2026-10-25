package com.zorago.simplywings.gacha;

public enum GachaTier {
    B("B", 0xFFDDE4EA),
    A("A", 0xFF5AA9FF),
    S("S", 0xFFB46CFF),
    SS("SS", 0xFFFFC23A),
    SSS("SSS", 0xFFFF4A5E),
    MYTHIC("✦", 0xFFF4F4F4);

    private final String label;
    private final int color;

    GachaTier(String label, int color) {
        this.label = label;
        this.color = color;
    }

    public String label() {
        return label;
    }

    public int color() {
        return color;
    }

    public boolean atLeast(GachaTier other) {
        return ordinal() >= other.ordinal();
    }

    public static GachaTier of(int ordinal) {
        GachaTier[] all = values();
        return all[Math.max(0, Math.min(all.length - 1, ordinal))];
    }
}
