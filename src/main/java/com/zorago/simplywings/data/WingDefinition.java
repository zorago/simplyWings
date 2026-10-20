package com.zorago.simplywings.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record WingDefinition(WingRarity rarity, WingStats equipped, WingStats global, float minSpeed, float flapRate,
                             float hoverRate, float hoverSpeed, float hoverSwing, float drag, ResourceLocation model,
                             int color) {
    public static final float CONTINUOUS_FLAP_RATE = 8f;
    private static final float REFERENCE_FLAP_RATE = 2f;

    public static final Codec<WingDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            WingRarity.CODEC.fieldOf("rarity").forGetter(WingDefinition::rarity),
            WingStats.CODEC.fieldOf("equipped").forGetter(WingDefinition::equipped),
            WingStats.CODEC.optionalFieldOf("global", WingStats.ZERO).forGetter(WingDefinition::global),
            Codec.FLOAT.fieldOf("min_speed").forGetter(WingDefinition::minSpeed),
            Codec.FLOAT.optionalFieldOf("flap_rate", 2f).forGetter(WingDefinition::flapRate),
            Codec.FLOAT.optionalFieldOf("hover_rate", 2.5f).forGetter(WingDefinition::hoverRate),
            Codec.FLOAT.optionalFieldOf("hover_speed", 8f).forGetter(WingDefinition::hoverSpeed),
            Codec.FLOAT.optionalFieldOf("hover_swing", 1f).forGetter(WingDefinition::hoverSwing),
            Codec.FLOAT.optionalFieldOf("drag", 1f).forGetter(WingDefinition::drag),
            ResourceLocation.CODEC.fieldOf("model").forGetter(WingDefinition::model),
            Codec.INT.optionalFieldOf("color", 0xFFA0A0A0).forGetter(WingDefinition::color)
    ).apply(i, WingDefinition::new));

    public boolean continuous() {
        return flapRate >= CONTINUOUS_FLAP_RATE;
    }

    public float beatCost(float referenceCost) {
        return referenceCost * REFERENCE_FLAP_RATE / flapRate;
    }

    public float continuousCostPerSecond(float referenceCost) {
        return referenceCost * REFERENCE_FLAP_RATE;
    }
}
