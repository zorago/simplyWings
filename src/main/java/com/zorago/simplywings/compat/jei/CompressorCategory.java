package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.essence.EssenceContainerItem;
import com.zorago.simplywings.essence.EssenceType;
import com.zorago.simplywings.item.ModItems;
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

public class CompressorCategory implements IRecipeCategory<EssenceType> {
    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public CompressorCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.CLOUD_COMPRESSOR_ITEM.get()));
        this.arrow = gui.getRecipeArrow();
    }

    @Override
    public RecipeType<EssenceType> getRecipeType() {
        return SimplyWingsJeiPlugin.COMPRESSOR;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.simplywings.compressor");
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
        return 30 + 10 * JeiText.lines(Minecraft.getInstance().font, Component.translatable("jei.simplywings.compressor.info"), 150);
    }

    private static ItemStack flask(EssenceType type, int amount) {
        return EssenceContainerItem.filled(ModItems.CLOUD_FLASK.get(), type, amount);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EssenceType output, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).setStandardSlotBackground().addItemStack(flask(EssenceType.CLOUD, 1));
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 1).setStandardSlotBackground().addItemStack(flask(EssenceType.STEAM, 1));
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 1).setStandardSlotBackground().addItemStack(flask(EssenceType.ETHEREAL, 1));
        builder.addSlot(RecipeIngredientRole.CATALYST, 66, 1).setStandardSlotBackground()
                .addItemStack(new ItemStack(ModBlocks.CLOUD_COMPRESSOR_ITEM.get()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 1).setStandardSlotBackground().addItemStack(flask(output, 3));
    }

    @Override
    public void draw(EssenceType output, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 98, 1);
        JeiText.wrapped(graphics, Minecraft.getInstance().font, Component.translatable("jei.simplywings.compressor.info"),
                0, 24, 150, 0x404040);
    }
}
