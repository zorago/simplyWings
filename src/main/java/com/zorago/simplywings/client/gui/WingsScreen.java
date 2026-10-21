package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.client.ClientWings;
import com.zorago.simplywings.client.WingVisuals;
import com.zorago.simplywings.data.EffectiveStats;
import com.zorago.simplywings.data.FurySet;
import com.zorago.simplywings.data.StatType;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRarity;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.data.WingStats;
import com.zorago.simplywings.item.WingItem;
import com.zorago.simplywings.network.Payloads;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WingsScreen extends Screen {
    private static final int WIDTH = 330;
    private static final int HEIGHT = 196;
    private static final int LIST_WIDTH = 116;
    private static final int ROW_HEIGHT = 20;
    private static final int PREVIEW_WIDTH = 80;
    private static final int PANEL = 0xE0101418;
    private static final int PANEL_LIGHT = 0xFF1E252B;
    private static final int BORDER = 0xFF3A4650;
    private static final int CELL_WIDTH = 9;
    private static final int CELL_HEIGHT = 4;
    private static final int BAR_WIDTH = 10 * CELL_WIDTH + 9;
    private static final int STAT_HEIGHT = 18;

    private Component hoveredStat;

    private record Entry(ResourceLocation id, WingDefinition definition) {
    }

    private final List<Entry> entries = new ArrayList<>();
    private ResourceLocation selected;
    private int scroll;
    private int left;
    private int top;
    private Button equipButton;
    private Button unequipButton;
    private Button appearanceButton;

    public WingsScreen() {
        super(Component.translatable("gui.simplywings.title"));
    }

    @Override
    protected void init() {
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;
        entries.clear();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Optional<Registry<WingDefinition>> registry = mc.level.registryAccess().registry(WingRegistries.WING);
            registry.ifPresent(r -> r.entrySet().forEach(e -> entries.add(new Entry(e.getKey().location(), e.getValue()))));
        }
        entries.sort(Comparator.comparing((Entry e) -> e.definition().rarity()).thenComparing(e -> e.id().toString()));
        if (selected == null) {
            selected = ClientWings.equipped().orElse(entries.isEmpty() ? null : entries.get(0).id());
        }
        int buttonY = top + HEIGHT - 26;
        int statsX = left + LIST_WIDTH + PREVIEW_WIDTH + 12;
        equipButton = addRenderableWidget(Button.builder(Component.translatable("gui.simplywings.equip"), b -> equip())
                .bounds(statsX, buttonY, 60, 20).build());
        unequipButton = addRenderableWidget(Button.builder(Component.translatable("gui.simplywings.unequip"), b -> unequip())
                .bounds(statsX + 64, buttonY, 60, 20).build());
        appearanceButton = addRenderableWidget(Button.builder(Component.empty(), b -> toggleAppearance())
                .bounds(statsX, buttonY - 22, 124, 20).build());
        updateButtons();
    }

    private boolean owned(ResourceLocation id) {
        return ClientWings.collection().contains(id);
    }

    private boolean hidden(Entry entry) {
        return entry.definition().rarity() == WingRarity.MYTHIC && !owned(entry.id());
    }

    private boolean selectedHidden() {
        return entries.stream().anyMatch(e -> e.id().equals(selected) && hidden(e));
    }

    private void updateButtons() {
        boolean isEquipped = selected != null && ClientWings.equipped().filter(selected::equals).isPresent();
        equipButton.active = selected != null && owned(selected) && !isEquipped;
        unequipButton.active = ClientWings.equipped().isPresent();
        boolean isAppearance = selected != null && ClientWings.appearance().filter(selected::equals).isPresent();
        appearanceButton.visible = FurySet.transmog(ClientWings.collection());
        appearanceButton.active = isAppearance || (selected != null && owned(selected));
        appearanceButton.setMessage(Component.translatable(isAppearance
                ? "gui.simplywings.reset_appearance" : "gui.simplywings.equip_appearance"));
    }

    private void toggleAppearance() {
        boolean isAppearance = selected != null && ClientWings.appearance().filter(selected::equals).isPresent();
        if (isAppearance) {
            PacketDistributor.sendToServer(new Payloads.EquipAppearance(Optional.empty()));
        } else if (selected != null && owned(selected)) {
            PacketDistributor.sendToServer(new Payloads.EquipAppearance(Optional.of(selected)));
        }
    }

    private void equip() {
        if (selected != null && owned(selected)) {
            PacketDistributor.sendToServer(new Payloads.EquipWing(Optional.of(selected)));
        }
    }

    private void unequip() {
        PacketDistributor.sendToServer(new Payloads.EquipWing(Optional.empty()));
    }

    @Override
    public void tick() {
        updateButtons();
    }

    private int visibleRows() {
        return (HEIGHT - 30) / ROW_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int listX = left + 6;
        int listY = top + 24;
        if (mouseX >= listX && mouseX < listX + LIST_WIDTH - 6 && mouseY >= listY) {
            int row = (int) ((mouseY - listY) / ROW_HEIGHT) + scroll;
            if (row >= 0 && row < entries.size() && row - scroll < visibleRows()) {
                selected = entries.get(row).id();
                updateButtons();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int max = Math.max(0, entries.size() - visibleRows());
        scroll = Mth.clamp(scroll - (int) Math.signum(scrollY), 0, max);
        return true;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(g, mouseX, mouseY, partialTick);
        g.fill(left, top, left + WIDTH, top + HEIGHT, PANEL);
        g.renderOutline(left, top, WIDTH, HEIGHT, BORDER);
        g.drawString(font, title, left + 8, top + 8, 0xFFFFFFFF, false);
        renderList(g, mouseX, mouseY);
        renderPreview(g);
        renderStats(g, mouseX, mouseY);
    }

    private void renderList(GuiGraphics g, int mouseX, int mouseY) {
        int x = left + 6;
        int y = top + 24;
        g.fill(x - 1, y - 1, x + LIST_WIDTH - 5, top + HEIGHT - 6, PANEL_LIGHT);
        int rows = visibleRows();
        for (int i = 0; i < rows && i + scroll < entries.size(); i++) {
            Entry entry = entries.get(i + scroll);
            int rowY = y + i * ROW_HEIGHT;
            boolean isOwned = owned(entry.id());
            boolean isSelected = entry.id().equals(selected);
            boolean hovered = mouseX >= x && mouseX < x + LIST_WIDTH - 6 && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            if (isSelected) {
                g.fill(x, rowY, x + LIST_WIDTH - 6, rowY + ROW_HEIGHT - 1, 0xFF34424E);
            } else if (hovered) {
                g.fill(x, rowY, x + LIST_WIDTH - 6, rowY + ROW_HEIGHT - 1, 0xFF283239);
            }
            int color = isOwned ? rarityColor(entry.definition().rarity()) : 0xFF6A6A6A;
            g.fill(x, rowY, x + 2, rowY + ROW_HEIGHT - 1, color);
            Component name = hidden(entry) ? Component.literal("???") : Component.translatable(WingItem.translationKey(entry.id()));
            g.drawString(font, name, x + 6, rowY + 2, color, false);
            Component sub = hidden(entry) ? Component.literal("???") : isOwned
                    ? Component.translatable("gui.simplywings.rarity." + entry.definition().rarity().getSerializedName())
                    : Component.translatable("gui.simplywings.locked");
            g.drawString(font, sub, x + 6, rowY + 11, 0xFF8A949C, false);
            if (ClientWings.equipped().filter(entry.id()::equals).isPresent()) {
                g.drawString(font, "✔", x + LIST_WIDTH - 16, rowY + 6, 0xFF7CFC9A, false);
            }
            if (ClientWings.appearance().filter(entry.id()::equals).isPresent()) {
                g.drawString(font, "◆", x + LIST_WIDTH - 24, rowY + 6, 0xFFFFD86A, false);
            }
        }
    }

    private void renderPreview(GuiGraphics g) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        int x0 = left + LIST_WIDTH + 4;
        int y0 = top + 24;
        int y1 = top + HEIGHT - 6;
        g.fill(x0, y0, x0 + PREVIEW_WIDTH, y1, PANEL_LIGHT);
        float angle = (Util.getMillis() % 12000L) / 12000f * (float) (Math.PI * 2);
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI).rotateY(angle);
        float bodyRot = player.yBodyRot;
        float bodyRotO = player.yBodyRotO;
        float yRot = player.getYRot();
        float yRotO = player.yRotO;
        float xRot = player.getXRot();
        float xRotO = player.xRotO;
        float headRot = player.yHeadRot;
        float headRotO = player.yHeadRotO;
        player.yBodyRot = 180f;
        player.yBodyRotO = 180f;
        player.setYRot(180f);
        player.yRotO = 180f;
        player.setXRot(0f);
        player.xRotO = 0f;
        player.yHeadRot = 180f;
        player.yHeadRotO = 180f;
        WingVisuals.setPreview(selectedHidden() ? Optional.empty() : Optional.ofNullable(selected));
        g.enableScissor(x0, y0, x0 + PREVIEW_WIDTH, y1);
        InventoryScreen.renderEntityInInventory(g, x0 + PREVIEW_WIDTH / 2f, y1 - 34, 34f,
                new Vector3f(0, player.getBbHeight() / 2f + 0.0625f, 0), pose, null, player);
        g.disableScissor();
        WingVisuals.setPreview(null);
        player.yBodyRot = bodyRot;
        player.yBodyRotO = bodyRotO;
        player.setYRot(yRot);
        player.yRotO = yRotO;
        player.setXRot(xRot);
        player.xRotO = xRotO;
        player.yHeadRot = headRot;
        player.yHeadRotO = headRotO;
    }

    private void renderStats(GuiGraphics g, int mouseX, int mouseY) {
        hoveredStat = null;
        int x = left + LIST_WIDTH + PREVIEW_WIDTH + 12;
        int y = top + 24;
        Optional<Entry> entry = entries.stream().filter(e -> e.id().equals(selected)).findFirst();
        Minecraft mc = Minecraft.getInstance();
        if (entry.isEmpty() || mc.level == null) {
            return;
        }
        if (hidden(entry.get())) {
            g.drawString(font, "???", x, y, 0xFF6A6A6A, false);
            return;
        }
        WingDefinition def = entry.get().definition();
        WingStats base = def.equipped();
        WingStats total = EffectiveStats.total(mc.level.registryAccess(), ClientWings.collection(), def);
        int color = rarityColor(def.rarity());
        g.drawString(font, Component.translatable(WingItem.translationKey(entry.get().id())), x, y, color, false);
        y += 14;
        for (StatType type : StatType.values()) {
            if (!type.visible()) {
                continue;
            }
            float own = Math.min(StatType.MAX_SCORE, type.score(base));
            float full = type.score(total);
            g.drawString(font, Component.translatable("gui.simplywings.stat." + type.key()), x, y, 0xFFC8D0D6, false);
            String value = String.format(Locale.ROOT, "%.1f/10", full / 10f);
            g.drawString(font, value, x + BAR_WIDTH - font.width(value), y, 0xFFFFFFFF, false);
            renderBar(g, x, y + 10, own, full, color);
            if (mouseX >= x && mouseX < x + BAR_WIDTH && mouseY >= y && mouseY < y + STAT_HEIGHT) {
                hoveredStat = Component.translatable("gui.simplywings.stat_value." + type.key(),
                        String.format(Locale.ROOT, "%.1f", type.value(full)));
            }
            y += STAT_HEIGHT;
        }
    }

    private static void renderBar(GuiGraphics g, int x, int y, float own, float full, int color) {
        for (int i = 0; i < 10; i++) {
            int cx = x + i * (CELL_WIDTH + 1);
            g.fill(cx, y, cx + CELL_WIDTH, y + CELL_HEIGHT, 0xFF2A3238);
            float from = i * 10f;
            int fullWidth = Math.round(Mth.clamp((full - from) / 10f, 0f, 1f) * CELL_WIDTH);
            int ownWidth = Math.round(Mth.clamp((own - from) / 10f, 0f, 1f) * CELL_WIDTH);
            if (fullWidth > 0) {
                g.fill(cx, y, cx + fullWidth, y + CELL_HEIGHT, 0xFF7FD99A);
            }
            if (ownWidth > 0) {
                g.fill(cx, y, cx + ownWidth, y + CELL_HEIGHT, color);
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        if (hoveredStat != null) {
            g.renderTooltip(font, hoveredStat, mouseX, mouseY);
        }
    }

    private static int rarityColor(WingRarity rarity) {
        return switch (rarity) {
            case COMMON -> 0xFFD8D8D8;
            case RARE -> 0xFF5FB8FF;
            case EPIC -> 0xFFC07CFF;
            case LEGENDARY -> 0xFFFFB534;
            case MYTHIC -> 0xFFFF5F7A;
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
