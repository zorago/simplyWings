package com.zorago.simplywings.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum WingRarity implements StringRepresentable {
    COMMON("common"),
    RARE("rare"),
    EPIC("epic"),
    LEGENDARY("legendary"),
    MYTHIC("mythic");

    public static final Codec<WingRarity> CODEC = StringRepresentable.fromEnum(WingRarity::values);

    private final String name;

    WingRarity(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
