package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.AssemblyTableBlockEntity;
import com.zorago.simplywings.block.AssemblyTableMenu;
import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.item.WingedEnergy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AssemblyTableScreen extends AbstractContainerScreen<AssemblyTableMenu> {
    private static final ResourceLocation ASSEMBLY = texture("assembly_table_assembly");
    private static final ResourceLocation COMPONENTS = texture("assembly_table_components");
    private static final ResourceLocation ENERGY = texture("assembly_table_energy");
    private static final ResourceLocation TAB_ON = texture("tab_on");
    private static final ResourceLocation TAB_OFF = texture("tab_off");
    private static final ResourceLocation ARROW = texture("arrow_progress");
    private static final ResourceLocation TANK_FILL = texture("tank_fill");
    private static final ResourceLocation TANK_TICKS = texture("tank_ticks");
    private static final ResourceLocation COST_ICON = texture("cost_icon");
    private static final int TANK_X = 10;
    private static final int TANK_Y = 18;
    private static final int TANK_W = 12;
    private static final int TANK_H = 70;
    private static final int TAB_W = 28;
    private static final int TAB_SPACING = 30;

    private Button assemble;
    private Button energy;

    public AssemblyTableScreen(AssemblyTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 206;
        inventoryLabelY = 113;
    }

    private static ResourceLocation texture(String name) {
        return SimplyWings.id("textures/gui/assembly_table/" + name + ".png");
    }

    @Override
    protected void init() {
        super.init();
        assemble = addRenderableWidget(Button.builder(Component.translatable("button.simplywings.assemble"),
                b -> press(AssemblyTableMenu.BUTTON_ASSEMBLE)).bounds(leftPos + 52, topPos + 66, 72, 16).build());
        energy = addRenderableWidget(Button.builder(Component.translatable("button.simplywings.imbue"),
                b -> press(AssemblyTableMenu.BUTTON_ENERGY)).bounds(leftPos + 58, topPos + 80, 60, 16).build());
        updateButtons();
    }

    private void press(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    private void selectTab(int tab) {
        if (menu.tab() != tab) {
            menu.setTab(tab);
            press(switch (tab) {
                case AssemblyTableMenu.TAB_ASSEMBLY -> AssemblyTableMenu.BUTTON_TAB_ASSEMBLY;
                case AssemblyTableMenu.TAB_COMPONENTS -> AssemblyTableMenu.BUTTON_TAB_COMPONENTS;
                default -> AssemblyTableMenu.BUTTON_TAB_ENERGY;
            });
            updateButtons();
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    private void updateButtons() {
        boolean assembly = menu.tab() == AssemblyTableMenu.TAB_ASSEMBLY;
        int cost = menu.assemblyCost();
        assemble.visible = assembly;
        assemble.active = cost >= 0 && menu.tank() >= cost && menu.progress() < 0 && menu.outputEmpty();
        ItemStack stack = menu.energyItem();
        energy.visible = menu.tab() == AssemblyTableMenu.TAB_ENERGY;
        energy.active = !stack.isEmpty();
        energy.setMessage(Component.translatable(WingedEnergy.has(stack) ? "button.simplywings.cleanse" : "button.simplywings.imbue"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (isIn(mouseX, mouseY, TANK_X, TANK_Y, TANK_W, TANK_H)) {
            graphics.renderTooltip(font, Component.translatable("tooltip.simplywings.tank",
                    menu.tank(), AssemblyTableBlockEntity.TANK_CAPACITY), mouseX, mouseY);
        }
        for (int tab = 0; tab < AssemblyTableMenu.TABS; tab++) {
            if (isIn(mouseX, mouseY, tabX(tab) - leftPos, -28, TAB_W, 28)) {
                graphics.renderTooltip(font, tabTitle(tab), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int active = menu.tab();
        for (int tab = 0; tab < AssemblyTableMenu.TABS; tab++) {
            if (tab != active) {
                graphics.blit(TAB_OFF, tabX(tab), topPos - 26, 0, 0, TAB_W, 28, TAB_W, 28);
                graphics.renderItem(tabIcon(tab), tabX(tab) + 6, topPos - 20);
            }
        }
        graphics.blit(switch (active) {
            case AssemblyTableMenu.TAB_ASSEMBLY -> ASSEMBLY;
            case AssemblyTableMenu.TAB_COMPONENTS -> COMPONENTS;
            default -> ENERGY;
        }, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        graphics.blit(TAB_ON, tabX(active), topPos - 28, 0, 0, TAB_W, 32, TAB_W, 32);
        graphics.renderItem(tabIcon(active), tabX(active) + 6, topPos - 21);

        int fill = Math.round(TANK_H * Math.min(1.0F, menu.tank() / (float) AssemblyTableBlockEntity.TANK_CAPACITY));
        if (fill > 0) {
            graphics.blit(TANK_FILL, leftPos + TANK_X, topPos + TANK_Y + TANK_H - fill, 0, TANK_H - fill, TANK_W, fill, TANK_W, TANK_H);
        }
        graphics.blit(TANK_TICKS, leftPos + TANK_X, topPos + TANK_Y, 0, 0, TANK_W, TANK_H, TANK_W, TANK_H);

        if (active == AssemblyTableMenu.TAB_ASSEMBLY && menu.progress() >= 0) {
            int width = Math.round(20.0F * menu.progress() / AssemblyTableBlockEntity.ASSEMBLY_TICKS);
            graphics.blit(ARROW, leftPos + 125, topPos + 45, 0, 0, width, 15, 20, 15);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        boolean assembly = menu.tab() == AssemblyTableMenu.TAB_ASSEMBLY;
        int cost = assembly ? menu.assemblyCost() : menu.componentCost();
        if (cost < 0 || menu.tab() == AssemblyTableMenu.TAB_ENERGY) {
            return;
        }
        String text = String.valueOf(cost);
        int width = font.width(text) + 11;
        int x = 158 - width / 2;
        int y = assembly ? 70 : 80;
        graphics.blit(COST_ICON, x, y, 0, 0, 9, 7, 9, 7);
        int color = menu.tank() >= cost ? 0x404040 : 0xC02020;
        graphics.drawString(font, text, x + 11, y, color, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        int cost = menu.tab() == AssemblyTableMenu.TAB_ASSEMBLY ? menu.assemblyCost() : menu.componentCost();
        int y = menu.tab() == AssemblyTableMenu.TAB_ASSEMBLY ? 68 : 78;
        if (cost >= 0 && menu.tab() != AssemblyTableMenu.TAB_ENERGY && hoveredSlot == null && isIn(mouseX, mouseY, 140, y, 36, 11)) {
            graphics.renderTooltip(font, Component.translatable("tooltip.simplywings.cost", cost)
                    .withStyle(menu.tank() >= cost ? ChatFormatting.GRAY : ChatFormatting.RED), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int tab = 0; tab < AssemblyTableMenu.TABS; tab++) {
            if (button == 0 && isIn(mouseX, mouseY, tabX(tab) - leftPos, -28, TAB_W, 28)) {
                selectTab(tab);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        double rx = mouseX - leftPos;
        double ry = mouseY - topPos;
        return rx >= x && rx < x + width && ry >= y && ry < y + height;
    }

    private int tabX(int tab) {
        return leftPos + 2 + tab * TAB_SPACING;
    }

    private static Component tabTitle(int tab) {
        return Component.translatable(switch (tab) {
            case AssemblyTableMenu.TAB_ASSEMBLY -> "tab.simplywings.assembly";
            case AssemblyTableMenu.TAB_COMPONENTS -> "tab.simplywings.components";
            default -> "tab.simplywings.energy";
        });
    }

    private static ItemStack tabIcon(int tab) {
        return switch (tab) {
            case AssemblyTableMenu.TAB_ASSEMBLY -> new ItemStack(ModItems.COMMON_WINGS.get());
            case AssemblyTableMenu.TAB_COMPONENTS -> new ItemStack(ModBlocks.ASSEMBLY_TABLE_ITEM.get());
            default -> {
                ItemStack feather = new ItemStack(Items.FEATHER);
                feather.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                yield feather;
            }
        };
    }
}
