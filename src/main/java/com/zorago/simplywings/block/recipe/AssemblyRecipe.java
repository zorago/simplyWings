package com.zorago.simplywings.block.recipe;

import com.zorago.simplywings.essence.EssenceCost;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record AssemblyRecipe(Ingredient harness, Ingredient core, Ingredient leftWing, Ingredient rightWing,
                             ItemStack result, EssenceCost essence) implements Recipe<AssemblyInput> {
    public static final MapCodec<AssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("harness").forGetter(AssemblyRecipe::harness),
            Ingredient.CODEC_NONEMPTY.fieldOf("core").forGetter(AssemblyRecipe::core),
            Ingredient.CODEC_NONEMPTY.fieldOf("left_wing").forGetter(AssemblyRecipe::leftWing),
            Ingredient.CODEC_NONEMPTY.fieldOf("right_wing").forGetter(AssemblyRecipe::rightWing),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(AssemblyRecipe::result),
            EssenceCost.CODEC.optionalFieldOf("essence", EssenceCost.NONE).forGetter(AssemblyRecipe::essence)
    ).apply(instance, AssemblyRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AssemblyRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, AssemblyRecipe::harness,
            Ingredient.CONTENTS_STREAM_CODEC, AssemblyRecipe::core,
            Ingredient.CONTENTS_STREAM_CODEC, AssemblyRecipe::leftWing,
            Ingredient.CONTENTS_STREAM_CODEC, AssemblyRecipe::rightWing,
            ItemStack.STREAM_CODEC, AssemblyRecipe::result,
            EssenceCost.STREAM_CODEC.cast(), AssemblyRecipe::essence,
            AssemblyRecipe::new);

    @Override
    public boolean matches(AssemblyInput input, Level level) {
        return harness.test(input.harness()) && core.test(input.core())
                && leftWing.test(input.leftWing()) && rightWing.test(input.rightWing());
    }

    @Override
    public ItemStack assemble(AssemblyInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, harness, core, leftWing, rightWing);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ASSEMBLY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ASSEMBLY.get();
    }
}
