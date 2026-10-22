package com.zorago.simplywings.compat.jei;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

final class JeiText {
    private static final int COLOR = 0x404040;

    static void essence(GuiGraphics graphics, Font font, int essence, int x, int y) {
        if (essence > 0) {
            graphics.drawString(font, Component.translatable("jei.simplywings.essence", essence), x, y, COLOR, false);
        }
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
