package com.zorago.simplywings.essence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record EssenceContent(EssenceType type, int amount) {
    public static final Codec<EssenceContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EssenceType.CODEC.fieldOf("type").forGetter(EssenceContent::type),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(EssenceContent::amount)
    ).apply(instance, EssenceContent::new));
    public static final StreamCodec<ByteBuf, EssenceContent> STREAM_CODEC = StreamCodec.composite(
            EssenceType.STREAM_CODEC, EssenceContent::type,
            ByteBufCodecs.VAR_INT, EssenceContent::amount,
            EssenceContent::new);
}
