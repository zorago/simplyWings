package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.item.WingedEnergy;
import com.zorago.simplywings.loot.LootSource;
import java.util.ArrayList;
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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

public class DropCategory implements IRecipeCategory<LootSource> {
    private static final int TEXT = 0x404040;
    private final LootSource.Kind kind;
    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public DropCategory(IGuiHelper gui, LootSource.Kind kind) {
        this.kind = kind;
        this.icon = gui.createDrawableItemStack(tool(kind));
        this.arrow = gui.getRecipeArrow();
    }

    static ItemStack tool(LootSource.Kind kind) {
        ItemStack stack = new ItemStack(switch (kind) {
            case MOB -> Items.IRON_SWORD;
            case BLOCK -> Items.IRON_PICKAXE;
            case FISH -> Items.FISHING_ROD;
        });
        WingedEnergy.toggle(stack);
        return stack;
    }

    @Override
    public RecipeType<LootSource> getRecipeType() {
        return switch (kind) {
            case MOB -> SimplyWingsJeiPlugin.MOB_DROPS;
            case BLOCK -> SimplyWingsJeiPlugin.BLOCK_DROPS;
            case FISH -> SimplyWingsJeiPlugin.FISHING;
        };
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.simplywings.drops." + kind.getSerializedName());
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 74;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LootSource source, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).setStandardSlotBackground().addItemStacks(sources(source));
        builder.addSlot(RecipeIngredientRole.CATALYST, 23, 1).setStandardSlotBackground().addItemStack(tool(source.kind()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).setStandardSlotBackground().addItemStack(new ItemStack(source.item(), source.max()));
    }

    @Override
    public void draw(LootSource source, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        arrow.draw(graphics, 46, 1);
        graphics.drawString(font, Component.translatable("jei.simplywings.chance", Math.round(source.chance() * 100)), 96, 1, TEXT, false);
        Component quantity = source.min() == source.max()
                ? Component.translatable("jei.simplywings.quantity", source.min())
                : Component.translatable("jei.simplywings.quantity_range", source.min(), source.max());
        graphics.drawString(font, quantity, 96, 11, TEXT, false);
        int y = 24;
        y = JeiText.wrapped(graphics, font, Component.translatable("jei.simplywings.drop." + source.kind().getSerializedName(),
                targets(source)), 0, y, 160, TEXT);
        if (source.note().isPresent()) {
            JeiText.wrapped(graphics, font, Component.translatable(source.note().get()), 0, y, 160, 0x7A4FB0);
        }
    }

    private static List<ItemStack> sources(LootSource source) {
        List<ItemStack> stacks = new ArrayList<>();
        switch (source.kind()) {
            case FISH -> stacks.add(new ItemStack(Items.FISHING_ROD));
            case MOB -> {
                for (String target : source.targets()) {
                    if (!target.startsWith("#")) {
                        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(target));
                        SpawnEggItem egg = SpawnEggItem.byId(type);
                        stacks.add(egg != null ? new ItemStack(egg) : new ItemStack(Items.NAME_TAG));
                    }
                }
            }
            case BLOCK -> {
                for (String target : source.targets()) {
                    if (target.startsWith("#")) {
                        BuiltInRegistries.BLOCK.getTag(TagKey.create(Registries.BLOCK, ResourceLocation.parse(target.substring(1))))
                                .ifPresent(set -> {
                                    for (Holder<Block> block : set) {
                                        addBlock(stacks, block.value());
                                    }
                                });
                    } else {
                        addBlock(stacks, BuiltInRegistries.BLOCK.get(ResourceLocation.parse(target)));
                    }
                }
            }
        }
        if (stacks.isEmpty()) {
            stacks.add(new ItemStack(Items.BARRIER));
        }
        return stacks;
    }

    private static void addBlock(List<ItemStack> stacks, Block block) {
        ItemStack stack = new ItemStack(block.asItem());
        if (!stack.isEmpty()) {
            stacks.add(stack);
        }
    }

    private static Component targets(LootSource source) {
        if (source.kind() == LootSource.Kind.FISH) {
            return Component.empty();
        }
        MutableComponent out = Component.empty();
        boolean first = true;
        for (String target : source.targets()) {
            if (!first) {
                out.append(", ");
            }
            first = false;
            if (target.startsWith("#")) {
                out.append(Component.literal(target));
            } else if (source.kind() == LootSource.Kind.MOB) {
                out.append(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(target)).getDescription());
            } else {
                out.append(BuiltInRegistries.BLOCK.get(ResourceLocation.parse(target)).getName());
            }
        }
        return out;
    }
}
