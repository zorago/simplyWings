package com.zorago.simplywings.block.recipe;

import com.zorago.simplywings.essence.EssenceCost;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ComponentRecipe(List<String> pattern, Map<String, Ingredient> key, ItemStack result, EssenceCost essence)
        implements Recipe<ComponentInput> {
    private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().validate(rows -> {
        if (rows.isEmpty() || rows.size() > ComponentInput.SIDE) {
            return DataResult.error(() -> "Pattern must have 1 to 5 rows");
        }
        int width = rows.getFirst().length();
        for (String row : rows) {
            if (row.length() != width || width == 0 || width > ComponentInput.SIDE) {
                return DataResult.error(() -> "Pattern rows must share a width of 1 to 5");
            }
        }
        return DataResult.success(rows);
    });
    private static final Codec<String> KEY_CODEC = Codec.STRING.validate(s -> s.length() == 1 && !s.equals(" ")
            ? DataResult.success(s) : DataResult.error(() -> "Key must be a single non-space character"));
    public static final MapCodec<ComponentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PATTERN_CODEC.fieldOf("pattern").forGetter(ComponentRecipe::pattern),
            Codec.unboundedMap(KEY_CODEC, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(ComponentRecipe::key),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ComponentRecipe::result),
            EssenceCost.CODEC.optionalFieldOf("essence", EssenceCost.NONE).forGetter(ComponentRecipe::essence)
    ).apply(instance, ComponentRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ComponentRecipe::pattern,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, Ingredient.CONTENTS_STREAM_CODEC), ComponentRecipe::key,
            ItemStack.STREAM_CODEC, ComponentRecipe::result,
            EssenceCost.STREAM_CODEC.cast(), ComponentRecipe::essence,
            ComponentRecipe::new);

    @Override
    public boolean matches(ComponentInput input, Level level) {
        int width = pattern.getFirst().length();
        int height = pattern.size();
        for (int oy = 0; oy <= ComponentInput.SIDE - height; oy++) {
            for (int ox = 0; ox <= ComponentInput.SIDE - width; ox++) {
                if (matchesAt(input, ox, oy, false) || matchesAt(input, ox, oy, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesAt(ComponentInput input, int ox, int oy, boolean mirrored) {
        int width = pattern.getFirst().length();
        int height = pattern.size();
        for (int y = 0; y < ComponentInput.SIDE; y++) {
            for (int x = 0; x < ComponentInput.SIDE; x++) {
                int px = x - ox;
                int py = y - oy;
                char symbol = ' ';
                if (px >= 0 && py >= 0 && px < width && py < height) {
                    symbol = pattern.get(py).charAt(mirrored ? width - 1 - px : px);
                }
                ItemStack stack = input.at(x, y);
                if (symbol == ' ') {
                    if (!stack.isEmpty()) {
                        return false;
                    }
                } else {
                    Ingredient ingredient = key.get(String.valueOf(symbol));
                    if (ingredient == null || ComponentInput.isCorner(x, y) || !ingredient.test(stack)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(ComponentInput input, HolderLookup.Provider registries) {
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
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (String row : pattern) {
            for (char symbol : row.toCharArray()) {
                ingredients.add(symbol == ' ' ? Ingredient.EMPTY : key.getOrDefault(String.valueOf(symbol), Ingredient.EMPTY));
            }
        }
        return ingredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COMPONENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.COMPONENT.get();
    }
}
