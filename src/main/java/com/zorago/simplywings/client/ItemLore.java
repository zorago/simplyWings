package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class ItemLore {
    private static final int WIDTH = 200;
    private static final Style LORE = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
    private static final Style RIDDLE = Style.EMPTY.withColor(TextColor.fromRgb(0xC9A8F0));

    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!SimplyWings.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace())) {
            return;
        }
        String key = stack.getDescriptionId();
        if (!I18n.exists(key + ".lore")) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        List<Component> lines = new ArrayList<>();
        for (String line : wrap(font, I18n.get(key + ".lore"))) {
            lines.add(Component.literal(line).withStyle(LORE));
        }
        if (I18n.exists(key + ".riddle")) {
            lines.add(Component.empty());
            for (String line : wrap(font, "✦ " + I18n.get(key + ".riddle"))) {
                lines.add(Component.literal(line).withStyle(RIDDLE));
            }
        }
        List<Component> tooltip = event.getToolTip();
        tooltip.addAll(Math.min(1, tooltip.size()), lines);
    }

    private static List<String> wrap(Font font, String text) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split(" ")) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (!current.isEmpty() && font.width(candidate) > WIDTH) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private ItemLore() {
    }
}
