package com.zorago.simplywings.essence;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EssenceCost(int cloud, int steam, int ethereal, int intensified) {
    public static final EssenceCost NONE = new EssenceCost(0, 0, 0, 0);
    private static final Codec<Integer> AMOUNT = Codec.intRange(0, EssenceTanks.CAPACITY);
    private static final Codec<EssenceCost> OBJECT = RecordCodecBuilder.create(instance -> instance.group(
            AMOUNT.optionalFieldOf("cloud", 0).forGetter(EssenceCost::cloud),
            AMOUNT.optionalFieldOf("steam", 0).forGetter(EssenceCost::steam),
            AMOUNT.optionalFieldOf("ethereal", 0).forGetter(EssenceCost::ethereal),
            AMOUNT.optionalFieldOf("intensified", 0).forGetter(EssenceCost::intensified)
    ).apply(instance, EssenceCost::new));
    public static final Codec<EssenceCost> CODEC = Codec.either(AMOUNT, OBJECT).xmap(
            either -> either.map(cloud -> new EssenceCost(cloud, 0, 0, 0), cost -> cost),
            Either::right);
    public static final StreamCodec<ByteBuf, EssenceCost> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EssenceCost::cloud,
            ByteBufCodecs.VAR_INT, EssenceCost::steam,
            ByteBufCodecs.VAR_INT, EssenceCost::ethereal,
            ByteBufCodecs.VAR_INT, EssenceCost::intensified,
            EssenceCost::new);

    public int get(EssenceType type) {
        return switch (type) {
            case CLOUD -> cloud;
            case STEAM -> steam;
            case ETHEREAL -> ethereal;
            case INTENSIFIED -> intensified;
        };
    }

    public int total() {
        return cloud + steam + ethereal + intensified;
    }

    public boolean isFree() {
        return total() == 0;
    }
}
