package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.block.recipe.AssemblyRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;

public class AssemblyCategory implements IRecipeCategory<RecipeHolder<AssemblyRecipe>> {
    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public AssemblyCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.ASSEMBLY_TABLE_ITEM.get()));
        this.arrow = gui.getRecipeArrow();
    }

    @Override
    public RecipeType<RecipeHolder<AssemblyRecipe>> getRecipeType() {
        return SimplyWingsJeiPlugin.ASSEMBLY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.simplywings.assembly");
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
        return 64;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AssemblyRecipe> holder, IFocusGroup focuses) {
        AssemblyRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 1).setStandardSlotBackground().addIngredients(recipe.harness());
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 25).setStandardSlotBackground().addIngredients(recipe.leftWing());
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 25).setStandardSlotBackground().addIngredients(recipe.core());
        builder.addSlot(RecipeIngredientRole.INPUT, 52, 25).setStandardSlotBackground().addIngredients(recipe.rightWing());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 25).setStandardSlotBackground().addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<AssemblyRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 80, 25);
        JeiText.essence(graphics, Minecraft.getInstance().font, holder.value().essence(), 4, 52, false);
    }
}
