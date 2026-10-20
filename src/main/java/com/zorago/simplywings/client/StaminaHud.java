package com.zorago.simplywings.client;

import com.zorago.simplywings.data.EffectiveStats;
import java.util.Optional;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class StaminaHud {
    private static final int HEIGHT = 20;
    private static final int WIDTH = 4;

    static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.player.isSpectator()) {
            return;
        }
        Optional<EffectiveStats> stats = ClientWings.stats();
        if (stats.isEmpty()) {
            return;
        }
        float max = stats.get().stamina();
        float stamina = ClientWings.stamina();
        if (stamina >= max && !ClientFlight.mode().flying()) {
            return;
        }
        int x = graphics.guiWidth() / 2 + 96;
        int bottom = graphics.guiHeight() - 2;
        int top = bottom - HEIGHT;
        float ratio = Math.max(0, Math.min(1, stamina / max));
        int filled = Math.round(HEIGHT * ratio);
        int color = ratio < 0.25f ? 0xFFE05050 : 0xFFE8ECFF;
        graphics.fill(x - 1, top - 1, x + WIDTH + 1, bottom + 1, 0x90000000);
        graphics.fill(x, bottom - filled, x + WIDTH, bottom, color);
        int rescue = ClientWings.rescueJumps();
        for (int i = 0; i < rescue; i++) {
            int y = top - 5 - i * 4;
            graphics.fill(x, y, x + WIDTH, y + 2, 0xFFFFD27A);
        }
    }

    private StaminaHud() {
    }
}
