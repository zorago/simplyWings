package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.essence.EssenceCost;
import com.zorago.simplywings.essence.EssenceType;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

final class JeiText {
    private static final int COLOR = 0x404040;
    private static final ResourceLocation COST_ICON = SimplyWings.id("textures/gui/assembly_table/cost_icon.png");

    static void essence(GuiGraphics graphics, Font font, EssenceCost cost, int x, int y, boolean vertical) {
        for (EssenceType type : EssenceType.values()) {
            int amount = cost.get(type);
            if (amount <= 0) {
                continue;
            }
            int rgb = type.liquidColor();
            graphics.setColor(((rgb >> 16) & 0xFF) / 255.0F, ((rgb >> 8) & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, 1.0F);
            graphics.blit(COST_ICON, x, y, 0, 0, 9, 7, 9, 7);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = String.valueOf(amount);
            graphics.drawString(font, text, x + 11, y, COLOR, false);
            if (vertical) {
                y += font.lineHeight + 1;
            } else {
                x += 11 + font.width(text) + 6;
            }
        }
    }

    static List<Component> essenceTooltip(EssenceCost cost) {
        List<Component> lines = new java.util.ArrayList<>();
        for (EssenceType type : EssenceType.values()) {
            if (cost.get(type) > 0) {
                lines.add(Component.translatable("tooltip.simplywings.cost", cost.get(type), type.displayName())
                        .withStyle(style -> style.withColor(type.barColor())));
            }
        }
        return lines;
    }

    static int lines(Font font, Component text, int width) {
        return font.split(text, width).size();
    }

    static int wrapped(GuiGraphics graphics, Font font, Component text, int x, int y, int width, int color) {
        List<FormattedCharSequence> lines = font.split(text, width);
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x, y, color, false);
            y += font.lineHeight + 1;
        }
        return y;
    }

    private JeiText() {
    }
}
