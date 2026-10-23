package com.zorago.simplywings.essence;

import com.zorago.simplywings.item.ModComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class EssenceContainerItem extends Item {
    private final int capacity;
    private final boolean stacksWhenEmpty;

    public EssenceContainerItem(Properties properties, int capacity, boolean stacksWhenEmpty) {
        super(properties);
        this.capacity = capacity;
        this.stacksWhenEmpty = stacksWhenEmpty;
    }

    public int capacity() {
        return capacity;
    }

    public static boolean isContainer(ItemStack stack) {
        return stack.getItem() instanceof EssenceContainerItem;
    }

    public static int capacity(ItemStack stack) {
        return stack.getItem() instanceof EssenceContainerItem item ? item.capacity : 0;
    }

    public static EssenceContent content(ItemStack stack) {
        return stack.get(ModComponents.ESSENCE.get());
    }

    public static int amount(ItemStack stack) {
        EssenceContent content = content(stack);
        return content == null ? 0 : content.amount();
    }

    public static EssenceType type(ItemStack stack) {
        EssenceContent content = content(stack);
        return content == null ? null : content.type();
    }

    public static boolean accepts(ItemStack stack, EssenceType type) {
        if (!isContainer(stack) || stack.getCount() != 1) {
            return false;
        }
        EssenceType current = type(stack);
        return (current == null || current == type) && amount(stack) < capacity(stack);
    }

    public static void set(ItemStack stack, EssenceType type, int amount) {
        int clamped = Mth.clamp(amount, 0, capacity(stack));
        if (clamped == 0 || type == null) {
            stack.remove(ModComponents.ESSENCE.get());
            if (stack.getItem() instanceof EssenceContainerItem item && item.stacksWhenEmpty) {
                stack.remove(DataComponents.MAX_STACK_SIZE);
            }
        } else {
            stack.set(ModComponents.ESSENCE.get(), new EssenceContent(type, clamped));
            stack.set(DataComponents.MAX_STACK_SIZE, 1);
        }
    }

    public static int fill(ItemStack stack, EssenceType type, int amount) {
        if (!accepts(stack, type)) {
            return 0;
        }
        int current = amount(stack);
        int added = Math.min(amount, capacity(stack) - current);
        set(stack, type, current + added);
        return added;
    }

    public static int drain(ItemStack stack, int amount) {
        EssenceType current = type(stack);
        if (current == null) {
            return 0;
        }
        int have = amount(stack);
        int taken = Math.min(have, amount);
        set(stack, current, have - taken);
        return taken;
    }

    public static ItemStack filled(Item item, EssenceType type, int amount) {
        ItemStack stack = new ItemStack(item);
        set(stack, type, amount);
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type(stack) == EssenceType.INTENSIFIED || super.isFoil(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return amount(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * amount(stack) / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        EssenceType type = type(stack);
        return type == null ? 0xBFE6F7 : type.barColor();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EssenceType type = type(stack);
        if (type == null) {
            tooltip.add(Component.translatable("tooltip.simplywings.essence_empty", capacity).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.simplywings.essence", type.displayName(), amount(stack), capacity)
                    .withStyle(style -> style.withColor(type.barColor())));
        }
    }
}
