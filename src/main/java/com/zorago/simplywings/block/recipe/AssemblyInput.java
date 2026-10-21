package com.zorago.simplywings.block.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AssemblyInput(ItemStack harness, ItemStack core, ItemStack leftWing, ItemStack rightWing) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> harness;
            case 1 -> core;
            case 2 -> leftWing;
            case 3 -> rightWing;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 4;
    }
}
