package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.EssenceBenchMenu;
import com.zorago.simplywings.essence.EssenceCost;
import com.zorago.simplywings.essence.EssenceTanks;
import com.zorago.simplywings.essence.EssenceType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class EssenceBenchScreen<M extends EssenceBenchMenu> extends AbstractContainerScreen<M> {
    protected static final ResourceLocation ARROW = texture("arrow_progress");
    private static final ResourceLocation GAUGE_FILL = texture("gauge_fill");
    private static final ResourceLocation GAUGE_TICKS = texture("gauge_ticks");
    private static final ResourceLocation COST_ICON = texture("cost_icon");
    private static final int GAUGE_X = 9;
    private static final int GAUGE_Y = 18;
    private static final int GAUGE_W = 5;
    private static final int GAUGE_H = 70;
    private static final int GAUGE_STEP = 6;
    private static final int COST_LINE = 9;

    protected EssenceBenchScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 206;
        inventoryLabelY = 113;
    }

    protected static ResourceLocation texture(String name) {
        return SimplyWings.id("textures/gui/assembly_table/" + name + ".png");
    }

    protected abstract int costX();

    protected abstract int costY();

    protected boolean showCost() {
        return true;
    }

    protected void press(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    protected void renderGauges(GuiGraphics graphics) {
        for (EssenceType type : EssenceType.values()) {
            int x = leftPos + GAUGE_X + type.ordinal() * GAUGE_STEP;
            int fill = Math.round(GAUGE_H * Math.min(1.0F, menu.tank(type) / (float) EssenceTanks.CAPACITY));
            if (fill > 0) {
                tint(graphics, type.liquidColor());
                graphics.blit(GAUGE_FILL, x, topPos + GAUGE_Y + GAUGE_H - fill, 0, GAUGE_H - fill, GAUGE_W, fill, GAUGE_W, GAUGE_H);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            graphics.blit(GAUGE_TICKS, x, topPos + GAUGE_Y, 0, 0, GAUGE_W, GAUGE_H, GAUGE_W, GAUGE_H);
        }
    }

    private static void tint(GuiGraphics graphics, int rgb) {
        graphics.setColor(((rgb >> 16) & 0xFF) / 255.0F, ((rgb >> 8) & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, 1.0F);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        for (EssenceType type : EssenceType.values()) {
            if (isIn(mouseX, mouseY, GAUGE_X + type.ordinal() * GAUGE_STEP, GAUGE_Y, GAUGE_W, GAUGE_H)) {
                graphics.renderTooltip(font, Component.translatable("tooltip.simplywings.gauge",
                        type.displayName(), menu.tank(type), EssenceTanks.CAPACITY).withStyle(style -> style.withColor(type.barColor())), mouseX, mouseY);
            }
        }
        EssenceCost cost = menu.cost();
        if (cost != null && showCost() && hoveredSlot == null) {
            int line = 0;
            for (EssenceType type : EssenceType.values()) {
                if (cost.get(type) <= 0) {
                    continue;
                }
                if (isIn(mouseX, mouseY, costX(), costY() + line * COST_LINE, 36, COST_LINE)) {
                    boolean enough = menu.tank(type) >= cost.get(type);
                    graphics.renderTooltip(font, Component.translatable("tooltip.simplywings.cost", cost.get(type), type.displayName())
                            .withStyle(enough ? ChatFormatting.GRAY : ChatFormatting.RED), mouseX, mouseY);
                }
                line++;
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        EssenceCost cost = menu.cost();
        if (cost == null || !showCost()) {
            return;
        }
        int line = 0;
        for (EssenceType type : EssenceType.values()) {
            int amount = cost.get(type);
            if (amount <= 0) {
                continue;
            }
            int y = costY() + line * COST_LINE + 1;
            tint(graphics, type.liquidColor());
            graphics.blit(COST_ICON, costX(), y, 0, 0, 9, 7, 9, 7);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.drawString(font, String.valueOf(amount), costX() + 11, y, menu.tank(type) >= amount ? 0x404040 : 0xC02020, false);
            line++;
        }
    }

    protected boolean isIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        double rx = mouseX - leftPos;
        double ry = mouseY - topPos;
        return rx >= x && rx < x + width && ry >= y && ry < y + height;
    }
}
