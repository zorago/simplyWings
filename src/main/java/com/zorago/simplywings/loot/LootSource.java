package com.zorago.simplywings.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zorago.simplywings.SimplyWings;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record LootSource(Kind kind, List<String> targets, Item item, int min, int max, float chance,
                         List<LootItemCondition> conditions, Optional<String> note) {
    public static final ResourceKey<Registry<LootSource>> KEY = ResourceKey.createRegistryKey(SimplyWings.id("loot_source"));

    public enum Kind implements StringRepresentable {
        MOB,
        BLOCK,
        FISH;

        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    public static final Codec<LootSource> CODEC = RecordCodecBuilder.create(i -> i.group(
            Kind.CODEC.fieldOf("kind").forGetter(LootSource::kind),
            Codec.STRING.listOf().optionalFieldOf("targets", List.of()).forGetter(LootSource::targets),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(LootSource::item),
            Codec.intRange(1, 64).optionalFieldOf("min", 1).forGetter(LootSource::min),
            Codec.intRange(1, 64).optionalFieldOf("max", 1).forGetter(LootSource::max),
            Codec.floatRange(0f, 1f).fieldOf("chance").forGetter(LootSource::chance),
            LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(LootSource::conditions),
            Codec.STRING.optionalFieldOf("note").forGetter(LootSource::note)
    ).apply(i, LootSource::new));

    public static final Codec<LootSource> NETWORK_CODEC = RecordCodecBuilder.create(i -> i.group(
            Kind.CODEC.fieldOf("kind").forGetter(LootSource::kind),
            Codec.STRING.listOf().optionalFieldOf("targets", List.of()).forGetter(LootSource::targets),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(LootSource::item),
            Codec.intRange(1, 64).optionalFieldOf("min", 1).forGetter(LootSource::min),
            Codec.intRange(1, 64).optionalFieldOf("max", 1).forGetter(LootSource::max),
            Codec.floatRange(0f, 1f).fieldOf("chance").forGetter(LootSource::chance),
            Codec.STRING.optionalFieldOf("note").forGetter(LootSource::note)
    ).apply(i, (kind, targets, item, min, max, chance, note) -> new LootSource(kind, targets, item, min, max, chance, List.of(), note)));

    public boolean matches(EntityType<?> type) {
        for (String target : targets) {
            if (target.startsWith("#")) {
                if (type.is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(target.substring(1))))) {
                    return true;
                }
            } else if (BuiltInRegistries.ENTITY_TYPE.getKey(type).equals(ResourceLocation.parse(target))) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(BlockState state) {
        for (String target : targets) {
            if (target.startsWith("#")) {
                if (state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse(target.substring(1))))) {
                    return true;
                }
            } else if (BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(ResourceLocation.parse(target))) {
                return true;
            }
        }
        return false;
    }

    public static Optional<Registry<LootSource>> registry(RegistryAccess access) {
        return access.registry(KEY);
    }
}
