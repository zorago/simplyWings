package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.client.ClientWings;
import com.zorago.simplywings.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class InventoryButton extends Button {
    private static final int SIZE = 20;
    private static final int GAP = 2;
    private static final int CREATIVE_TABS = 30;

    private final AbstractContainerScreen<?> screen;

    private InventoryButton(AbstractContainerScreen<?> screen) {
        super(0, 0, SIZE, SIZE, Component.empty(), b -> Minecraft.getInstance().setScreen(new WingsScreen()),
                DEFAULT_NARRATION);
        this.screen = screen;
        setTooltip(Tooltip.create(Component.translatable("gui.simplywings.title")));
        reposition();
    }

    private void reposition() {
        setX(screen.getGuiLeft());
        setY(screen.getGuiTop() + screen.getYSize() + GAP + (screen instanceof CreativeModeInventoryScreen ? CREATIVE_TABS : 0));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.renderItem(icon(), getX() + 2, getY() + 2);
    }

    private static ItemStack icon() {
        ResourceLocation wing = ClientWings.equipped().orElse(null);
        if (wing != null) {
            Item item = BuiltInRegistries.ITEM.get(wing.withSuffix("_wings"));
            if (item != Items.AIR) {
                return new ItemStack(item);
            }
        }
        return new ItemStack(ModItems.COMMON_WINGS.get());
    }

    private static boolean supported(Screen screen) {
        return screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen;
    }

    public static void onInit(ScreenEvent.Init.Post event) {
        if (supported(event.getScreen()) && !ClientWings.collection().isEmpty()) {
            event.addListener(new InventoryButton((AbstractContainerScreen<?>) event.getScreen()));
        }
    }

    public static void onRender(ScreenEvent.Render.Pre event) {
        if (supported(event.getScreen())) {
            for (var child : event.getScreen().children()) {
                if (child instanceof InventoryButton button) {
                    button.reposition();
                }
            }
        }
    }
}
