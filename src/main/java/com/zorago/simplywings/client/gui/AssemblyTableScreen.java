package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.block.AssemblyTableBlockEntity;
import com.zorago.simplywings.block.AssemblyTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AssemblyTableScreen extends EssenceBenchScreen<AssemblyTableMenu> {
    private static final ResourceLocation BACKGROUND = texture("assembly_table_assembly");

    private Button assemble;

    public AssemblyTableScreen(AssemblyTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        assemble = addRenderableWidget(Button.builder(Component.translatable("button.simplywings.assemble"),
                b -> press(AssemblyTableMenu.BUTTON_ASSEMBLE)).bounds(leftPos + 52, topPos + 66, 72, 16).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.translatable("gui.simplywings.wiki.button"),
                b -> com.zorago.simplywings.client.wiki.WikiScreen.open(this, "workshops/assembly_table")).bounds(leftPos + imageWidth - 34, topPos + 4, 30, 14).build());
        updateButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    private void updateButtons() {
        assemble.active = menu.affordable() && menu.progress() < 0 && menu.outputEmpty();
    }

    @Override
    protected int costX() {
        return 140;
    }

    @Override
    protected int costY() {
        return 66;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderGauges(graphics);
        if (menu.progress() >= 0) {
            int width = Math.round(20.0F * menu.progress() / AssemblyTableBlockEntity.ASSEMBLY_TICKS);
            graphics.blit(ARROW, leftPos + 125, topPos + 45, 0, 0, width, 15, 20, 15);
        }
    }
}
