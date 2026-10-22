package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.block.recipe.ComponentInput;
import com.zorago.simplywings.block.recipe.ComponentRecipe;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ComponentCategory implements IRecipeCategory<RecipeHolder<ComponentRecipe>> {
    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public ComponentCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.ASSEMBLY_TABLE_ITEM.get()));
        this.arrow = gui.getRecipeArrow();
    }

    @Override
    public RecipeType<RecipeHolder<ComponentRecipe>> getRecipeType() {
        return SimplyWingsJeiPlugin.COMPONENTS;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.simplywings.components");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 92;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ComponentRecipe> holder, IFocusGroup focuses) {
        ComponentRecipe recipe = holder.value();
        List<String> pattern = recipe.pattern();
        int height = pattern.size();
        int width = pattern.getFirst().length();
        int offsetX = height == ComponentInput.SIDE && width == ComponentInput.SIDE ? 0 : (ComponentInput.SIDE - width) / 2;
        int offsetY = height == ComponentInput.SIDE && width == ComponentInput.SIDE ? 0 : (ComponentInput.SIDE - height) / 2;
        for (int y = 0; y < ComponentInput.SIDE; y++) {
            for (int x = 0; x < ComponentInput.SIDE; x++) {
                if (ComponentInput.isCorner(x, y)) {
                    continue;
                }
                var slot = builder.addSlot(RecipeIngredientRole.INPUT, 1 + x * 18, 1 + y * 18).setStandardSlotBackground();
                int px = x - offsetX;
                int py = y - offsetY;
                if (px >= 0 && py >= 0 && px < width && py < height) {
                    char symbol = pattern.get(py).charAt(px);
                    Ingredient ingredient = recipe.key().get(String.valueOf(symbol));
                    if (symbol != ' ' && ingredient != null) {
                        slot.addIngredients(ingredient);
                    }
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 37).setStandardSlotBackground().addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<ComponentRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 96, 37);
        JeiText.essence(graphics, Minecraft.getInstance().font, holder.value().essence(), 100, 62);
    }
}
