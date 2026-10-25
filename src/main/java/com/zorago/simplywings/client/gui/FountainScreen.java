package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.gacha.FountainMenu;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class FountainScreen extends AbstractContainerScreen<FountainMenu> {
    private Button offer;

    public FountainScreen(FountainMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 180;
        inventoryLabelY = 87;
    }

    @Override
    protected void init() {
        super.init();
        offer = addRenderableWidget(Button.builder(Component.translatable("screen.simplywings.fountain.offer"),
                b -> {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, FountainMenu.BUTTON_OFFER);
                    }
                }).bounds(leftPos + 140, topPos + 56, 30, 20).build());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        offer.active = menu.offered() > 0;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        g.fillGradient(x, y, x + imageWidth, y + imageHeight, 0xF01A2A3A, 0xF0101820);
        g.renderOutline(x, y, imageWidth, imageHeight, 0xFF7FD8F0);
        g.renderOutline(x + 2, y + 2, imageWidth - 4, imageHeight - 4, 0x557FD8F0);
        long t = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            int wy = y + 80 - (int) ((t / 40 + i * 9) % 70);
            g.fill(x + 20 + i * 3, wy, x + 21 + i * 3, wy + 3, 0x667FD8F0);
            g.fill(x + 134 + i * 3, wy, x + 135 + i * 3, wy + 3, 0x667FD8F0);
        }
        for (Slot slot : menu.slots) {
            g.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF0C141C);
            g.renderOutline(x + slot.x - 1, y + slot.y - 1, 18, 18, 0xFF3A5A6A);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, titleLabelX, titleLabelY, 0xFFDFF6FF, false);
        g.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFB8D8E8, false);
        String value = String.format(Locale.ROOT, "%.2f", menu.offered());
        String reserve = String.format(Locale.ROOT, "%.2f", menu.reserve());
        g.drawString(font, Component.translatable("screen.simplywings.fountain.value", value), 8, 22, 0xFFFFE9B0, false);
        g.drawString(font, Component.translatable("screen.simplywings.fountain.reserve", reserve), 8, 34, 0xFF9FC8D8, false);
    }
}
