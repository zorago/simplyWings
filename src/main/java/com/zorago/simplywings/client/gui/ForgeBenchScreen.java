package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.block.ForgeBenchMenu;
import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.item.WingedEnergy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ForgeBenchScreen extends EssenceBenchScreen<ForgeBenchMenu> {
    private static final ResourceLocation COMPONENTS = texture("forge_bench_components");
    private static final ResourceLocation ENERGY = texture("forge_bench_energy");
    private static final ResourceLocation TAB_ON = texture("tab_on");
    private static final ResourceLocation TAB_OFF = texture("tab_off");
    private static final int TAB_W = 28;
    private static final int TAB_SPACING = 30;

    private Button energy;

    public ForgeBenchScreen(ForgeBenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        energy = addRenderableWidget(Button.builder(Component.translatable("button.simplywings.imbue"),
                b -> press(ForgeBenchMenu.BUTTON_ENERGY)).bounds(leftPos + 58, topPos + 80, 60, 16).build());
        updateButtons();
    }

    private void selectTab(int tab) {
        if (menu.tab() != tab) {
            menu.setTab(tab);
            press(tab == ForgeBenchMenu.TAB_COMPONENTS ? ForgeBenchMenu.BUTTON_TAB_COMPONENTS : ForgeBenchMenu.BUTTON_TAB_ENERGY);
            updateButtons();
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    private void updateButtons() {
        ItemStack stack = menu.energyItem();
        energy.visible = menu.tab() == ForgeBenchMenu.TAB_ENERGY;
        energy.active = !stack.isEmpty();
        energy.setMessage(Component.translatable(WingedEnergy.has(stack) ? "button.simplywings.cleanse" : "button.simplywings.imbue"));
    }

    @Override
    protected int costX() {
        return 140;
    }

    @Override
    protected int costY() {
        return 78;
    }

    @Override
    protected boolean showCost() {
        return menu.tab() == ForgeBenchMenu.TAB_COMPONENTS;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        for (int tab = 0; tab < ForgeBenchMenu.TABS; tab++) {
            if (isIn(mouseX, mouseY, tabX(tab) - leftPos, -28, TAB_W, 28)) {
                graphics.renderTooltip(font, tabTitle(tab), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int active = menu.tab();
        for (int tab = 0; tab < ForgeBenchMenu.TABS; tab++) {
            if (tab != active) {
                graphics.blit(TAB_OFF, tabX(tab), topPos - 26, 0, 0, TAB_W, 28, TAB_W, 28);
                graphics.renderItem(tabIcon(tab), tabX(tab) + 6, topPos - 20);
            }
        }
        graphics.blit(active == ForgeBenchMenu.TAB_COMPONENTS ? COMPONENTS : ENERGY, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        graphics.blit(TAB_ON, tabX(active), topPos - 28, 0, 0, TAB_W, 32, TAB_W, 32);
        graphics.renderItem(tabIcon(active), tabX(active) + 6, topPos - 21);
        renderGauges(graphics);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int tab = 0; tab < ForgeBenchMenu.TABS; tab++) {
            if (button == 0 && isIn(mouseX, mouseY, tabX(tab) - leftPos, -28, TAB_W, 28)) {
                selectTab(tab);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int tabX(int tab) {
        return leftPos + 2 + tab * TAB_SPACING;
    }

    private static Component tabTitle(int tab) {
        return Component.translatable(tab == ForgeBenchMenu.TAB_COMPONENTS ? "tab.simplywings.components" : "tab.simplywings.energy");
    }

    private static ItemStack tabIcon(int tab) {
        if (tab == ForgeBenchMenu.TAB_COMPONENTS) {
            return new ItemStack(ModBlocks.FORGE_BENCH_ITEM.get());
        }
        ItemStack feather = new ItemStack(Items.FEATHER);
        feather.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return feather;
    }
}
