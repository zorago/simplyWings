package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.client.ClientWings;
import com.zorago.simplywings.client.WingVisuals;
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

    private record Entry(ResourceLocation id, WingDefinition definition) {
    }

    private final List<Entry> entries = new ArrayList<>();
    private ResourceLocation selected;
    private int scroll;
    private int left;
    private int top;
    private Button equipButton;
    private Button unequipButton;

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
        updateButtons();
    }

    private boolean owned(ResourceLocation id) {
        return ClientWings.collection().contains(id);
    }

    private void updateButtons() {
        boolean isEquipped = selected != null && ClientWings.equipped().filter(selected::equals).isPresent();
        equipButton.active = selected != null && owned(selected) && !isEquipped;
        unequipButton.active = ClientWings.equipped().isPresent();
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
        renderStats(g);
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
            Component name = Component.translatable(WingItem.translationKey(entry.id()));
            g.drawString(font, name, x + 6, rowY + 2, color, false);
            Component sub = isOwned
                    ? Component.translatable("gui.simplywings.rarity." + entry.definition().rarity().getSerializedName())
                    : Component.translatable("gui.simplywings.locked");
            g.drawString(font, sub, x + 6, rowY + 11, 0xFF8A949C, false);
            if (ClientWings.equipped().filter(entry.id()::equals).isPresent()) {
                g.drawString(font, "✔", x + LIST_WIDTH - 16, rowY + 6, 0xFF7CFC9A, false);
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
        WingVisuals.setPreview(Optional.ofNullable(selected));
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

    private void renderStats(GuiGraphics g) {
        int x = left + LIST_WIDTH + PREVIEW_WIDTH + 12;
        int y = top + 24;
        Optional<Entry> entry = entries.stream().filter(e -> e.id().equals(selected)).findFirst();
        if (entry.isEmpty()) {
            return;
        }
        WingDefinition def = entry.get().definition();
        WingStats total = WingStats.ZERO;
        for (Entry e : entries) {
            if (owned(e.id())) {
                total = total.add(e.definition().global());
            }
        }
        g.drawString(font, Component.translatable(WingItem.translationKey(entry.get().id())), x, y,
                rarityColor(def.rarity()), false);
        y += 14;
        int col1 = x + 62;
        int col2 = x + 98;
        g.drawString(font, Component.translatable("gui.simplywings.equipped_stats"), col1 - 8, y, 0xFF8A949C, false);
        g.drawString(font, Component.translatable("gui.simplywings.global_stats"), col2 + 4, y, 0xFF8A949C, false);
        y += 12;
        WingStats eq = def.equipped();
        WingStats gl = def.global();
        y = row(g, "stamina", eq.stamina(), gl.stamina(), x, col1, col2, y);
        y = row(g, "acceleration", eq.acceleration(), gl.acceleration(), x, col1, col2, y);
        y = row(g, "max_speed", eq.maxSpeed(), gl.maxSpeed(), x, col1, col2, y);
        y = row(g, "handling", eq.handling(), gl.handling(), x, col1, col2, y);
        y = row(g, "stabilization", eq.stabilization(), gl.stabilization(), x, col1, col2, y);
        y = row(g, "min_speed", def.minSpeed(), Float.NaN, x, col1, col2, y);
        y += 4;
        g.drawString(font, Component.translatable("gui.simplywings.collection_total"), x, y, 0xFF8A949C, false);
        y += 11;
        String totals = String.format(Locale.ROOT, "%.0f / %.1f / %.1f / %.0f / %.1f", total.stamina(),
                total.acceleration(), total.maxSpeed(), total.handling(), total.stabilization());
        g.drawString(font, totals, x, y, 0xFFE8ECFF, false);
    }

    private int row(GuiGraphics g, String key, float equipped, float global, int x, int col1, int col2, int y) {
        g.drawString(font, Component.translatable("gui.simplywings.stat." + key), x, y, 0xFFC8D0D6, false);
        g.drawString(font, format(equipped), col1, y, 0xFFFFFFFF, false);
        if (!Float.isNaN(global)) {
            g.drawString(font, "+" + format(global), col2 + 4, y, 0xFF9BE7B0, false);
        }
        return y + 11;
    }

    private static String format(float v) {
        return v == Math.floor(v) ? String.valueOf((int) v) : String.format(Locale.ROOT, "%.1f", v);
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
