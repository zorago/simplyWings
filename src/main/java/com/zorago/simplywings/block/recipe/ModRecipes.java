package com.zorago.simplywings.block.recipe;

import com.mojang.serialization.MapCodec;
import com.zorago.simplywings.SimplyWings;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, SimplyWings.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SimplyWings.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AssemblyRecipe>> ASSEMBLY =
            TYPES.register("assembly", () -> RecipeType.simple(SimplyWings.id("assembly")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<ComponentRecipe>> COMPONENT =
            TYPES.register("component", () -> RecipeType.simple(SimplyWings.id("component")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AssemblyRecipe>> ASSEMBLY_SERIALIZER =
            SERIALIZERS.register("assembly", () -> serializer(AssemblyRecipe.CODEC, AssemblyRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ComponentRecipe>> COMPONENT_SERIALIZER =
            SERIALIZERS.register("component", () -> serializer(ComponentRecipe.CODEC, ComponentRecipe.STREAM_CODEC));

    private static <T extends Recipe<?>> RecipeSerializer<T> serializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new RecipeSerializer<>() {
            @Override
            public MapCodec<T> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    }

    public static void register(IEventBus modBus) {
        TYPES.register(modBus);
        SERIALIZERS.register(modBus);
    }

    private ModRecipes() {
    }
}
