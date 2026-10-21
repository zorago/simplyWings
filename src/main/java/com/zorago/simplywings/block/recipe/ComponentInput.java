package com.zorago.simplywings.block.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ComponentInput(List<ItemStack> cells) implements RecipeInput {
    public static final int SIDE = 5;

    public static boolean isCorner(int x, int y) {
        return (x == 0 || x == SIDE - 1) && (y == 0 || y == SIDE - 1);
    }

    public ItemStack at(int x, int y) {
        return cells.get(y * SIDE + x);
    }

    @Override
    public ItemStack getItem(int index) {
        return cells.get(index);
    }

    @Override
    public int size() {
        return SIDE * SIDE;
    }
}
