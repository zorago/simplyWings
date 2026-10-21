package com.zorago.simplywings.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CloudFlaskItem extends Item {
    public static final int CAPACITY = 100;

    public CloudFlaskItem(Properties properties) {
        super(properties);
    }

    public static int essence(ItemStack stack) {
        return stack.getOrDefault(ModComponents.CLOUD_ESSENCE.get(), 0);
    }

    public static void setEssence(ItemStack stack, int amount) {
        int clamped = Mth.clamp(amount, 0, CAPACITY);
        if (clamped == 0) {
            stack.remove(ModComponents.CLOUD_ESSENCE.get());
            stack.remove(DataComponents.MAX_STACK_SIZE);
        } else {
            stack.set(ModComponents.CLOUD_ESSENCE.get(), clamped);
            stack.set(DataComponents.MAX_STACK_SIZE, 1);
        }
    }

    public static ItemStack filled(Item item, int amount) {
        ItemStack stack = new ItemStack(item);
        setEssence(stack, amount);
        return stack;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return essence(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * essence(stack) / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xBFE6F7;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.simplywings.cloud_essence", essence(stack), CAPACITY)
                .withStyle(ChatFormatting.AQUA));
    }
}
