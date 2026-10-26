package com.zorago.simplywings.client.gui;

import com.zorago.simplywings.sound.ModSounds;
import com.zorago.simplywings.SimplyWings;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WingsScreen extends Screen {
    private static final ResourceLocation BACKGROUND = SimplyWings.id("textures/gui/wings/wings_screen.png");
    private static final int WIDTH = 320;
    private static final int HEIGHT = 188;
    private static final int LIST_X = 8;
    private static final int LIST_Y = 19;
    private static final int LIST_W = 108;
    private static final int ROWS = 8;
    private static final int ROW_HEIGHT = 20;
    private static final int SCROLL_X = 116;
    private static final int SCROLL_W = 6;
    private static final int PREVIEW_X = 128;
    private static final int PREVIEW_W = 78;
    private static final int INFO_X = 214;
    private static final int INFO_W = 96;
    private static final int CELL_WIDTH = 8;
    private static final int CELL_HEIGHT = 4;
    private static final int STAT_HEIGHT = 17;
    private static final int TEXT_DARK = 0xFF404040;
    private static final int TEXT_DIM = 0xFF8A949C;
    private static final int TEXT_LABEL = 0xFFC8D0D6;
    private static final String ELLIPSIS = "…";

    private record Entry(ResourceLocation id, WingDefinition definition, ItemStack icon) {
    }

    private final List<Entry> entries = new ArrayList<>();
    private ResourceLocation selected;
    private int scroll;
    private int left;
    private int top;
    private Button equipButton;
    private Button unequipButton;
    private Button appearanceButton;
    private Component hoverText;

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
            registry.ifPresent(r -> r.entrySet().forEach(e -> {
                ResourceLocation id = e.getKey().location();
                entries.add(new Entry(id, e.getValue(), icon(id)));
            }));
        }
        entries.sort(Comparator.comparing((Entry e) -> e.definition().rarity()).thenComparing(e -> e.id().toString()));
        if (selected == null) {
            selected = ClientWings.equipped().orElse(entries.isEmpty() ? null : entries.get(0).id());
        }
        scroll = Mth.clamp(scroll, 0, maxScroll());
        int x = left + INFO_X;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.translatable("gui.simplywings.wiki.button"),
                b -> com.zorago.simplywings.client.wiki.WikiScreen.open(this, null)).bounds(width - 36, 4, 30, 14).build());
        equipButton = addRenderableWidget(Button.builder(Component.translatable("gui.simplywings.equip"), b -> equip())
                .bounds(x, top + 160, 47, 16).build());
        unequipButton = addRenderableWidget(Button.builder(Component.translatable("gui.simplywings.unequip"), b -> unequip())
                .bounds(x + 49, top + 160, 47, 16).build());
        appearanceButton = addRenderableWidget(Button.builder(Component.empty(), b -> toggleAppearance())
                .bounds(x, top + 141, INFO_W, 16).build());
        updateButtons();
    }

    private static ItemStack icon(ResourceLocation id) {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_wings");
        return BuiltInRegistries.ITEM.getOptional(itemId).map(ItemStack::new).orElse(ItemStack.EMPTY);
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
            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.UI_EQUIP.get(), 1.0F, 0.7F));
        }
    }

    private void unequip() {
        PacketDistributor.sendToServer(new Payloads.EquipWing(Optional.empty()));
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(ModSounds.UI_EQUIP.get(), 0.75F, 0.5F));
    }

    @Override
    public void tick() {
        updateButtons();
    }

    private int maxScroll() {
        return Math.max(0, entries.size() - ROWS);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int listX = left + LIST_X;
        int listY = top + LIST_Y;
        if (mouseX >= listX && mouseX < listX + LIST_W && mouseY >= listY && mouseY < listY + ROWS * ROW_HEIGHT) {
            int row = (int) ((mouseY - listY) / ROW_HEIGHT) + scroll;
            if (row < entries.size()) {
                selected = entries.get(row).id();
                updateButtons();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Mth.clamp(scroll - (int) Math.signum(scrollY), 0, maxScroll());
        return true;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(g, mouseX, mouseY, partialTick);
        hoverText = null;
        g.blit(BACKGROUND, left, top, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        g.drawString(font, title, left + 8, top + 7, TEXT_DARK, false);
        renderList(g, mouseX, mouseY);
        renderPreview(g);
        renderInfo(g, mouseX, mouseY);
    }

    private void renderList(GuiGraphics g, int mouseX, int mouseY) {
        int x = left + LIST_X;
        int y = top + LIST_Y;
        for (int i = 0; i < ROWS && i + scroll < entries.size(); i++) {
            Entry entry = entries.get(i + scroll);
            int rowY = y + i * ROW_HEIGHT;
            boolean isOwned = owned(entry.id());
            boolean isHidden = hidden(entry);
            boolean hovered = mouseX >= x && mouseX < x + LIST_W && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            if (entry.id().equals(selected)) {
                g.fill(x, rowY, x + LIST_W, rowY + ROW_HEIGHT, 0xFF34424E);
            } else if (hovered) {
                g.fill(x, rowY, x + LIST_W, rowY + ROW_HEIGHT, 0xFF283239);
            }
            int color = isOwned ? rarityColor(entry.definition().rarity()) : 0xFF6A6A6A;
            g.fill(x, rowY + 1, x + 2, rowY + ROW_HEIGHT - 1, color);
            if (isHidden) {
                g.drawString(font, "?", x + 9, rowY + 6, TEXT_DIM, false);
            } else {
                g.renderItem(entry.icon(), x + 4, rowY + 2);
                if (!isOwned) {
                    g.pose().pushPose();
                    g.pose().translate(0, 0, 200);
                    g.fill(x + 4, rowY + 2, x + 20, rowY + 18, 0xA01E2226);
                    g.pose().popPose();
                }
            }
            int textX = x + 23;
            int markers = markerWidth(entry);
            int textW = LIST_W - 23 - markers - 2;
            Component name = isHidden ? Component.literal("???") : Component.translatable(WingItem.translationKey(entry.id()));
            boolean cut = drawFitted(g, name, textX, rowY + 2, textW, color);
            Component sub = isHidden ? Component.literal("???") : isOwned
                    ? Component.translatable("gui.simplywings.rarity." + entry.definition().rarity().getSerializedName())
                    : Component.translatable("gui.simplywings.locked");
            drawFitted(g, sub, textX, rowY + 11, textW, TEXT_DIM);
            int markerX = x + LIST_W - 9;
            if (ClientWings.equipped().filter(entry.id()::equals).isPresent()) {
                g.drawString(font, "✔", markerX, rowY + 2, 0xFF7CFC9A, false);
            }
            if (ClientWings.appearance().filter(entry.id()::equals).isPresent()) {
                g.drawString(font, "◆", markerX, rowY + 11, 0xFFFFD86A, false);
            }
            if (hovered && cut) {
                hoverText = name;
            }
        }
        renderScrollbar(g);
    }

    private int markerWidth(Entry entry) {
        boolean marked = ClientWings.equipped().filter(entry.id()::equals).isPresent()
                || ClientWings.appearance().filter(entry.id()::equals).isPresent();
        return marked ? 10 : 0;
    }

    private void renderScrollbar(GuiGraphics g) {
        int trackTop = top + LIST_Y;
        int trackHeight = ROWS * ROW_HEIGHT;
        int x = left + SCROLL_X;
        if (maxScroll() == 0) {
            return;
        }
        int thumb = Math.max(12, trackHeight * ROWS / entries.size());
        int thumbY = trackTop + (trackHeight - thumb) * scroll / maxScroll();
        g.fill(x + 1, thumbY, x + SCROLL_W - 1, thumbY + thumb, 0xFF8B8B8B);
        g.fill(x + 1, thumbY, x + SCROLL_W - 2, thumbY + thumb - 1, 0xFFC6C6C6);
    }

    private void renderPreview(GuiGraphics g) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        int x0 = left + PREVIEW_X;
        int y0 = top + LIST_Y;
        int y1 = y0 + ROWS * ROW_HEIGHT;
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
        g.enableScissor(x0, y0, x0 + PREVIEW_W, y1);
        InventoryScreen.renderEntityInInventory(g, x0 + PREVIEW_W / 2f, top + 128, 34f,
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

    private void renderInfo(GuiGraphics g, int mouseX, int mouseY) {
        int x = left + INFO_X;
        int y = top + LIST_Y + 3;
        Optional<Entry> entry = entries.stream().filter(e -> e.id().equals(selected)).findFirst();
        Minecraft mc = Minecraft.getInstance();
        if (entry.isEmpty() || mc.level == null) {
            return;
        }
        if (hidden(entry.get())) {
            g.drawString(font, "???", x, y, TEXT_DIM, false);
            return;
        }
        WingDefinition def = entry.get().definition();
        int color = rarityColor(def.rarity());
        List<FormattedCharSequence> lines = font.split(Component.translatable(WingItem.translationKey(entry.get().id())), INFO_W);
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            g.drawString(font, lines.get(i), x, y + i * 9, color, false);
        }
        Component rarity = Component.translatable("gui.simplywings.rarity." + def.rarity().getSerializedName());
        drawFitted(g, owned(entry.get().id()) ? rarity : Component.translatable("gui.simplywings.locked"), x, y + 19, INFO_W, TEXT_DIM);

        WingStats base = def.equipped();
        WingStats total = EffectiveStats.total(mc.level.registryAccess(), ClientWings.collection(), def);
        int statY = y + 32;
        for (StatType type : StatType.values()) {
            if (!type.visible()) {
                continue;
            }
            float own = Math.min(StatType.MAX_SCORE, type.score(base));
            float full = type.score(total);
            String value = String.format(Locale.ROOT, "%.1f", full / 10f);
            int valueW = font.width(value);
            g.drawString(font, value, x + INFO_W - valueW, statY, 0xFFFFFFFF, false);
            drawFitted(g, Component.translatable("gui.simplywings.stat." + type.key()), x, statY, INFO_W - valueW - 4, TEXT_LABEL);
            renderBar(g, x, statY + 10, own, full, color);
            if (mouseX >= x && mouseX < x + INFO_W && mouseY >= statY && mouseY < statY + STAT_HEIGHT) {
                hoverText = Component.translatable("gui.simplywings.stat_value." + type.key(),
                        String.format(Locale.ROOT, "%.1f", type.value(full)));
            }
            statY += STAT_HEIGHT;
        }
    }

    private boolean drawFitted(GuiGraphics g, Component text, int x, int y, int maxWidth, int color) {
        if (font.width(text) <= maxWidth) {
            g.drawString(font, text, x, y, color, false);
            return false;
        }
        String cut = font.plainSubstrByWidth(text.getString(), maxWidth - font.width(ELLIPSIS)).stripTrailing();
        g.drawString(font, cut + ELLIPSIS, x, y, color, false);
        return true;
    }

    private static void renderBar(GuiGraphics g, int x, int y, float own, float full, int color) {
        for (int i = 0; i < 10; i++) {
            int cx = x + i * (CELL_WIDTH + 1);
            g.fill(cx, y, cx + CELL_WIDTH, y + CELL_HEIGHT, 0xFF3A4048);
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
        if (hoverText != null) {
            g.renderTooltip(font, hoverText, mouseX, mouseY);
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
