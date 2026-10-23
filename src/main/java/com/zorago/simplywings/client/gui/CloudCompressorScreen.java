package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.CloudCompressorBlockEntity;
import com.zorago.simplywings.block.CloudCompressorMenu;
import com.zorago.simplywings.essence.EssenceContainerItem;
import com.zorago.simplywings.essence.EssenceType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CloudCompressorScreen extends AbstractContainerScreen<CloudCompressorMenu> {
    private static final ResourceLocation BACKGROUND = texture("cloud_compressor");
    private static final ResourceLocation GAUGE = texture("compressor_gauge");
    private static final ResourceLocation VAT = texture("compressor_vat");
    private static final ResourceLocation BUBBLES = texture("compressor_bubbles");
    private static final int GAUGE_W = 4;
    private static final int GAUGE_H = 16;
    private static final int VAT_X = 66;
    private static final int VAT_Y = 18;
    private static final int VAT_W = 44;
    private static final int VAT_H = 56;

    public CloudCompressorScreen(CloudCompressorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 180;
        inventoryLabelY = 87;
    }

    private static ResourceLocation texture(String name) {
        return SimplyWings.id("textures/gui/cloud_compressor/" + name + ".png");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (hoveredSlot == null && menu.getCarried().isEmpty()) {
            for (int slot = 0; slot < CloudCompressorBlockEntity.SIZE; slot++) {
                int[] at = CloudCompressorMenu.SLOT_POS[slot];
                if (isIn(mouseX, mouseY, at[0] + 19, at[1], GAUGE_W + 1, GAUGE_H) && menu.slotItem(slot).isEmpty()) {
                    EssenceType type = slot < CloudCompressorBlockEntity.OUTPUT ? CloudCompressorBlockEntity.INPUTS[slot] : EssenceType.INTENSIFIED;
                    graphics.renderTooltip(font, Component.translatable("tooltip.simplywings.compressor_slot", type.displayName()), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        for (int slot = 0; slot < CloudCompressorBlockEntity.SIZE; slot++) {
            int[] at = CloudCompressorMenu.SLOT_POS[slot];
            ItemStack stack = menu.slotItem(slot);
            int capacity = EssenceContainerItem.capacity(stack);
            EssenceType type = EssenceContainerItem.type(stack);
            if (capacity <= 0 || type == null) {
                continue;
            }
            int fill = Math.max(1, Math.round(GAUGE_H * EssenceContainerItem.amount(stack) / (float) capacity));
            tint(graphics, type.liquidColor());
            graphics.blit(GAUGE, leftPos + at[0] + 19, topPos + at[1] + GAUGE_H - fill, 0, GAUGE_H - fill, GAUGE_W, fill, GAUGE_W, GAUGE_H);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (menu.running()) {
            long time = minecraft != null && minecraft.level != null ? minecraft.level.getGameTime() : 0;
            float t = (time % 40 + partialTick) / 40.0F;
            int level = VAT_H - 8;
            tint(graphics, EssenceType.INTENSIFIED.liquidColor());
            graphics.blit(VAT, leftPos + VAT_X, topPos + VAT_Y + VAT_H - level, 0, VAT_H - level, VAT_W, level, VAT_W, VAT_H);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int offset = Math.round(t * 32);
            graphics.blit(BUBBLES, leftPos + VAT_X, topPos + VAT_Y + 8, 0, 32 - offset, VAT_W, level, VAT_W, 96);
        }
    }

    private static void tint(GuiGraphics graphics, int rgb) {
        graphics.setColor(((rgb >> 16) & 0xFF) / 255.0F, ((rgb >> 8) & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, 1.0F);
    }

    private boolean isIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        double rx = mouseX - leftPos;
        double ry = mouseY - topPos;
        return rx >= x && rx < x + width && ry >= y && ry < y + height;
    }
}
