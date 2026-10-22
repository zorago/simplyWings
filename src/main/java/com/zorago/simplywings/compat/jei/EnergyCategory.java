package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.item.WingedEnergy;
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

public class EnergyCategory implements IRecipeCategory<ItemStack> {
    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public EnergyCategory(IGuiHelper gui) {
        ItemStack sample = new ItemStack(net.minecraft.world.item.Items.IRON_SWORD);
        WingedEnergy.toggle(sample);
        this.icon = gui.createDrawableItemStack(sample);
        this.arrow = gui.getRecipeArrow();
    }

    @Override
    public RecipeType<ItemStack> getRecipeType() {
        return SimplyWingsJeiPlugin.ENERGY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.simplywings.winged_energy");
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
        return 56;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ItemStack stack, IFocusGroup focuses) {
        ItemStack imbued = stack.copy();
        if (!WingedEnergy.has(imbued)) {
            WingedEnergy.toggle(imbued);
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 1).setStandardSlotBackground().addItemStack(stack);
        builder.addSlot(RecipeIngredientRole.CATALYST, 64, 1).setStandardSlotBackground()
                .addItemStack(new ItemStack(ModBlocks.ASSEMBLY_TABLE_ITEM.get()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 1).setStandardSlotBackground().addItemStack(imbued);
    }

    @Override
    public void draw(ItemStack stack, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 86, 1);
        JeiText.wrapped(graphics, Minecraft.getInstance().font, Component.translatable("jei.simplywings.winged_energy.info"),
                0, 24, 150, 0x404040);
    }
}
