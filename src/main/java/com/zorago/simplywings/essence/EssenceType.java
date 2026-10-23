package com.zorago.simplywings.essence;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum EssenceType implements StringRepresentable {
    CLOUD("cloud", 0xDDF2FF, 0xBFE6F7),
    STEAM("steam", 0xFF9C5A, 0xF07A3A),
    ETHEREAL("ethereal", 0xC9A8F0, 0xA77FE0),
    INTENSIFIED("intensified", 0xFFE68A, 0xF4C542);

    public static final Codec<EssenceType> CODEC = StringRepresentable.fromEnum(EssenceType::values);
    private static final IntFunction<EssenceType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, EssenceType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
    public static final int COUNT = values().length;

    private final String id;
    private final int liquid;
    private final int bar;

    EssenceType(String id, int liquid, int bar) {
        this.id = id;
        this.liquid = liquid;
        this.bar = bar;
    }

    public int liquidColor() {
        return liquid;
    }

    public int barColor() {
        return bar;
    }

    public Component displayName() {
        return Component.translatable("essence.simplywings." + id);
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
