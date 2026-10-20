package com.zorago.simplywings.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record WingDefinition(WingRarity rarity, WingStats equipped, WingStats global, float minSpeed, float flapRate,
                             ResourceLocation model, int color) {
    public static final Codec<WingDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            WingRarity.CODEC.fieldOf("rarity").forGetter(WingDefinition::rarity),
            WingStats.CODEC.fieldOf("equipped").forGetter(WingDefinition::equipped),
            WingStats.CODEC.optionalFieldOf("global", WingStats.ZERO).forGetter(WingDefinition::global),
            Codec.FLOAT.fieldOf("min_speed").forGetter(WingDefinition::minSpeed),
            Codec.FLOAT.optionalFieldOf("flap_rate", 2f).forGetter(WingDefinition::flapRate),
            ResourceLocation.CODEC.fieldOf("model").forGetter(WingDefinition::model),
            Codec.INT.optionalFieldOf("color", 0xFFA0A0A0).forGetter(WingDefinition::color)
    ).apply(i, WingDefinition::new));
}
