package com.zorago.simplywings.client.gui;

import com.mojang.math.Axis;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.client.ClientGacha;
import com.zorago.simplywings.gacha.AltarMenu;
import com.zorago.simplywings.gacha.GachaData;
import com.zorago.simplywings.gacha.GachaTier;
import com.zorago.simplywings.network.Payloads;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class AltarScreen extends AbstractContainerScreen<AltarMenu> {
    private static final ResourceLocation COVER = SimplyWings.id("textures/gui/summon_cover.png");
    private static final int COVER_W = 480;
    private static final int COVER_H = 270;
    private static final long LAUNCH_MS = 1900;
    private static final long LAUNCH_RARE_MS = 2700;
    private static final long CARD_MS = 450;
    private static final long CARD_RARE_MS = 1300;
    private static final int CARD_W = 70;
    private static final int CARD_H = 96;

    private enum Phase { IDLE, LAUNCH, REVEAL, SUMMARY }

    private enum Tab { NONE, HISTORY, RATES }

    private Phase phase = Phase.IDLE;
    private Tab tab = Tab.NONE;
    private int seen;
    private List<GachaData.Entry> results = List.of();
    private GachaTier best = GachaTier.B;
    private long phaseStart;
    private int card;
    private boolean flashPlayed;
    private boolean cardSound;

    public AltarScreen(AltarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        seen = ClientGacha.serial();
    }

    @Override
    protected void init() {
        imageWidth = width;
        imageHeight = height;
        super.init();
        leftPos = 0;
        topPos = 0;
    }

    private long now() {
        return Util.getMillis();
    }

    private void sound(SoundEvent event, float pitch, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (ClientGacha.serial() != seen) {
            seen = ClientGacha.serial();
            results = ClientGacha.pending();
            best = GachaTier.B;
            for (GachaData.Entry entry : results) {
                if (entry.rank().atLeast(best)) {
                    best = entry.rank();
                }
            }
            phase = Phase.LAUNCH;
            phaseStart = now();
            flashPlayed = false;
            tab = Tab.NONE;
            sound(SoundEvents.BEACON_ACTIVATE, 1.5f, 0.8f);
            if (best.atLeast(GachaTier.S)) {
                sound(SoundEvents.ENDER_EYE_LAUNCH, 0.6f, 0.7f);
            }
        }
        if (phase == Phase.LAUNCH && now() - phaseStart > launchLength()) {
            startCard(0);
        }
    }

    private long launchLength() {
        return best.atLeast(GachaTier.S) ? LAUNCH_RARE_MS : LAUNCH_MS;
    }

    private void startCard(int index) {
        phase = Phase.REVEAL;
        card = index;
        phaseStart = now();
        cardSound = false;
    }

    private void cardSound(GachaTier tier) {
        switch (tier) {
            case B -> sound(SoundEvents.ITEM_PICKUP, 1.2f, 0.6f);
            case A -> sound(SoundEvents.NOTE_BLOCK_CHIME.value(), 1.4f, 0.8f);
            case S -> {
                sound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.8f, 1f);
                sound(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0f, 0.9f);
            }
            case SS -> sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.1f, 0.9f);
            case SSS -> {
                sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1f);
                sound(SoundEvents.ENDER_DRAGON_GROWL, 1.2f, 0.35f);
            }
            case MYTHIC -> {
                sound(SoundEvents.END_PORTAL_SPAWN, 1.0f, 0.5f);
                sound(SoundEvents.ENDER_DRAGON_GROWL, 0.7f, 0.5f);
            }
        }
    }

    private long cardLength(GachaTier tier) {
        return tier.atLeast(GachaTier.S) ? CARD_RARE_MS : CARD_MS;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        drawCover(g);
        switch (phase) {
            case IDLE -> drawIdle(g, mouseX, mouseY);
            case LAUNCH -> drawLaunch(g);
            case REVEAL -> drawReveal(g);
            case SUMMARY -> drawSummary(g, mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
    }

    private void drawCover(GuiGraphics g) {
        float scale = Math.max(width / (float) COVER_W, height / (float) COVER_H);
        int w = Math.round(COVER_W * scale);
        int h = Math.round(COVER_H * scale);
        g.blit(COVER, (width - w) / 2, (height - h) / 2, w, h, 0, 0, COVER_W, COVER_H, COVER_W, COVER_H);
        g.fillGradient(0, height - 90, width, height, 0x00000000, 0xC0000000);
    }

    private void drawIdle(GuiGraphics g, int mouseX, int mouseY) {
        Payloads.GachaState state = ClientGacha.state();
        g.pose().pushPose();
        g.pose().translate(18, 16, 0);
        g.pose().scale(2f, 2f, 1f);
        g.drawString(font, Component.translatable("screen.simplywings.altar.title"), 0, 0, 0xFFF2E6FF, true);
        g.pose().popPose();
        g.drawString(font, Component.translatable("screen.simplywings.altar.subtitle"), 18, 38, 0xFFB8A8D8, true);

        int px = width - 178;
        int py = 18;
        g.fill(px - 8, py - 8, width - 10, py + 96, 0x99100818);
        g.renderOutline(px - 8, py - 8, width - 10 - (px - 8), 104, 0x66B46CFF);
        g.drawString(font, Component.translatable("screen.simplywings.altar.pity"), px, py, 0xFFE8DDFF, true);
        bar(g, px, py + 16, GachaTier.S, state.pityS(), 100, 80);
        bar(g, px, py + 42, GachaTier.SS, state.pitySS(), 500, 400);
        bar(g, px, py + 68, GachaTier.SSS, state.pitySSS(), 1000, 800);

        int by = height - 46;
        int one = width - 250;
        int ten = width - 130;
        button(g, one, by, 112, 30, Component.translatable("screen.simplywings.altar.pull_one"), Component.literal("×1"),
                state.feathers() >= 1 || creative(), mouseX, mouseY);
        button(g, ten, by, 112, 30, Component.translatable("screen.simplywings.altar.pull_ten"), Component.literal("×10"),
                state.feathers() >= 10 || creative(), mouseX, mouseY);
        g.drawString(font, Component.translatable("screen.simplywings.altar.feathers", state.feathers()), one, by - 14, 0xFFFFE9B0, true);
        g.drawString(font, Component.translatable("screen.simplywings.altar.total", state.total()), 18, height - 20, 0xFF9C8CBC, true);

        tabButton(g, 18, height - 46, Component.translatable("screen.simplywings.altar.history"), tab == Tab.HISTORY, mouseX, mouseY);
        tabButton(g, 98, height - 46, Component.translatable("screen.simplywings.altar.rates"), tab == Tab.RATES, mouseX, mouseY);
        if (tab == Tab.HISTORY) {
            drawHistory(g, state);
        } else if (tab == Tab.RATES) {
            drawRates(g);
        }
    }

    private boolean creative() {
        return minecraft != null && minecraft.player != null && minecraft.player.isCreative();
    }

    private void bar(GuiGraphics g, int x, int y, GachaTier tier, int count, int hard, int soft) {
        int w = 160;
        g.drawString(font, tier.label(), x, y, tier.color(), true);
        Component text = Component.literal(count + " / " + hard);
        g.drawString(font, text, x + w - font.width(text), y, 0xFFDDD5EE, true);
        int by = y + 11;
        g.fill(x, by, x + w, by + 6, 0xFF221A2E);
        int zone = Math.round(w * soft / (float) hard);
        g.fill(x + zone, by, x + w, by + 6, 0xFF3A2A48);
        int fill = Math.round(w * Math.min(1f, count / (float) hard));
        int color = count >= soft ? tier.color() : (tier.color() & 0x00FFFFFF) | 0xA0000000;
        g.fill(x, by, x + fill, by + 6, color);
        g.fill(x + zone, by - 1, x + zone + 1, by + 7, 0xFFFFFFFF);
    }

    private boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, Component label, Component badge, boolean enabled, int mx, int my) {
        boolean hover = enabled && inside(mx, my, x, y, w, h);
        int top = enabled ? (hover ? 0xFFE8C46A : 0xFFC9A24A) : 0xFF5A5060;
        int bottom = enabled ? (hover ? 0xFFB07C2A : 0xFF8E6224) : 0xFF3A3440;
        g.fillGradient(x, y, x + w, y + h, top, bottom);
        g.renderOutline(x, y, w, h, enabled ? 0xFFFFF0C0 : 0xFF6A6070);
        g.drawCenteredString(font, label, x + w / 2, y + 6, enabled ? 0xFF2A1406 : 0xFF9A90A0);
        g.drawCenteredString(font, badge, x + w / 2, y + 17, enabled ? 0xFF3A1E08 : 0xFF8A8090);
    }

    private void tabButton(GuiGraphics g, int x, int y, Component label, boolean active, int mx, int my) {
        int w = 72;
        boolean hover = inside(mx, my, x, y, w, 18);
        g.fill(x, y, x + w, y + 18, active ? 0xCC6A3CA8 : hover ? 0xAA3A2458 : 0x88201430);
        g.renderOutline(x, y, w, 18, 0x88B46CFF);
        g.drawCenteredString(font, label, x + w / 2, y + 5, 0xFFF0E8FF);
    }

    private void drawHistory(GuiGraphics g, Payloads.GachaState state) {
        int x = 18;
        int y = 60;
        int w = 260;
        int rows = Math.min(16, state.history().size());
        g.fill(x - 6, y - 6, x + w, y + 14 + rows * 11, 0xCC0C0614);
        g.drawString(font, Component.translatable("screen.simplywings.altar.history_title"), x, y, 0xFFE8DDFF, true);
        for (int i = 0; i < rows; i++) {
            GachaData.Entry entry = state.history().get(i);
            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(entry.item()), entry.count());
            int ry = y + 14 + i * 11;
            g.drawString(font, entry.rank().label(), x, ry, entry.rank().color(), true);
            Component name = entry.count() > 1 ? Component.literal(entry.count() + " × ").append(stack.getHoverName()) : stack.getHoverName();
            g.drawString(font, name, x + 28, ry, entry.rank().atLeast(GachaTier.S) ? entry.rank().color() : 0xFFCFC6DE, false);
        }
        if (rows == 0) {
            g.drawString(font, Component.translatable("screen.simplywings.altar.history_empty"), x, y + 14, 0xFF9C8CBC, false);
        }
    }

    private void drawRates(GuiGraphics g) {
        int x = 18;
        int y = 60;
        String[] keys = {"rates_b", "rates_a", "rates_s", "rates_ss", "rates_sss", "rates_mythic", "rates_pity_s", "rates_pity_ss",
                "rates_pity_sss", "rates_reset", "rates_no_5050"};
        g.fill(x - 6, y - 6, x + 330, y + 18 + keys.length * 11, 0xCC0C0614);
        g.drawString(font, Component.translatable("screen.simplywings.altar.rates_title"), x, y, 0xFFE8DDFF, true);
        for (int i = 0; i < keys.length; i++) {
            g.drawString(font, Component.translatable("screen.simplywings.altar." + keys[i]), x, y + 14 + i * 11, 0xFFCFC6DE, false);
        }
    }

    private void drawLaunch(GuiGraphics g) {
        long t = now() - phaseStart;
        float p = Mth.clamp(t / (float) launchLength(), 0f, 1f);
        g.fill(0, 0, width, height, (int) (0xC0 * Math.min(1f, p * 3f)) << 24);
        stars(g, t);
        GachaTier shown = best.atLeast(GachaTier.S) && p < 0.55f ? GachaTier.A : best;
        int color = shown.color();
        float travel = Mth.clamp(p / 0.8f, 0f, 1f);
        float ease = 1f - (1f - travel) * (1f - travel);
        float sx = width * 0.92f;
        float sy = -20f;
        float cx = Mth.lerp(ease, sx, width / 2f);
        float cy = Mth.lerp(ease, sy, height / 2f);
        for (int i = 0; i < 26; i++) {
            float k = i / 26f;
            float tx = Mth.lerp(k * 0.35f, cx, sx);
            float ty = Mth.lerp(k * 0.35f, cy, sy);
            int r = Math.max(1, Math.round(9 * (1 - k)));
            int alpha = (int) (220 * (1 - k));
            g.fill(Math.round(tx) - r, Math.round(ty) - r, Math.round(tx) + r, Math.round(ty) + r, (alpha << 24) | (color & 0xFFFFFF));
        }
        int core = 6 + (int) (4 * Math.sin(t * 0.03));
        g.fill(Math.round(cx) - core, Math.round(cy) - core, Math.round(cx) + core, Math.round(cy) + core, 0xFFFFFFFF);
        if (best.atLeast(GachaTier.S) && p >= 0.55f && p < 0.6f) {
            g.fill(0, 0, width, height, 0x60000000 | (best.color() & 0xFFFFFF));
        }
        if (p > 0.82f) {
            if (!flashPlayed) {
                flashPlayed = true;
                sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1.6f, 1f);
            }
            float f = 1f - Mth.clamp((p - 0.82f) / 0.18f, 0f, 1f);
            g.fill(0, 0, width, height, ((int) (255 * (1 - f * f) * 0.9f) << 24) | 0xFFFFFF);
        }
        hint(g, Component.translatable("screen.simplywings.altar.skip"));
    }

    private void stars(GuiGraphics g, long t) {
        for (int i = 0; i < 90; i++) {
            int x = (int) ((i * 7919L) % Math.max(1, width));
            int y = (int) ((i * 104729L + t / 6 * (1 + i % 3)) % Math.max(1, height));
            int a = 80 + (int) (100 * (0.5 + 0.5 * Math.sin(t * 0.004 + i)));
            g.fill(x, y, x + 1 + i % 2, y + 1 + i % 2, (a << 24) | 0xE8E0FF);
        }
    }

    private void drawReveal(GuiGraphics g) {
        g.fill(0, 0, width, height, 0xD0000000);
        GachaData.Entry entry = results.get(card);
        GachaTier tier = entry.rank();
        long t = now() - phaseStart;
        if (!cardSound) {
            cardSound = true;
            cardSound(tier);
        }
        float p = Mth.clamp(t / (float) cardLength(tier), 0f, 1f);
        if (tier.atLeast(GachaTier.S)) {
            rays(g, tier, t, p);
        }
        float pop = p < 0.35f ? Mth.clamp(p / 0.35f, 0f, 1f) : 1f;
        float scale = 0.3f + 0.7f * (1f - (1f - pop) * (1f - pop)) + (tier.atLeast(GachaTier.S) ? 0.04f * (float) Math.sin(t * 0.01) : 0f);
        g.pose().pushPose();
        g.pose().translate(width / 2f, height / 2f - 6, 0);
        g.pose().scale(scale * 1.7f, scale * 1.7f, 1f);
        drawCard(g, -CARD_W / 2, -CARD_H / 2, entry, true);
        g.pose().popPose();
        if (tier.atLeast(GachaTier.S) && p > 0.35f) {
            g.pose().pushPose();
            g.pose().translate(width / 2f, 26, 0);
            float big = 3f + 0.4f * (float) Math.sin(t * 0.008);
            g.pose().scale(big, big, 1f);
            g.drawCenteredString(font, tier.label(), 0, 0, tier.color());
            g.pose().popPose();
        }
        ItemStack stack = stackOf(entry);
        g.drawCenteredString(font, stack.getHoverName(), width / 2, height / 2 + CARD_H, tier.color());
        if (results.size() > 1) {
            g.drawCenteredString(font, Component.literal((card + 1) + " / " + results.size()), width / 2, height - 30, 0xFFB8A8D8);
        }
        hint(g, Component.translatable(p >= 1f ? "screen.simplywings.altar.next" : "screen.simplywings.altar.skip"));
    }

    private void rays(GuiGraphics g, GachaTier tier, long t, float p) {
        int count = tier == GachaTier.MYTHIC ? 24 : tier == GachaTier.SSS ? 18 : tier == GachaTier.SS ? 14 : 10;
        float reach = Math.max(width, height) * (0.25f + 0.6f * Mth.clamp(p * 2f, 0f, 1f));
        g.pose().pushPose();
        g.pose().translate(width / 2f, height / 2f - 6, 0);
        g.pose().mulPose(Axis.ZP.rotationDegrees(t * 0.02f));
        for (int i = 0; i < count; i++) {
            g.pose().pushPose();
            g.pose().mulPose(Axis.ZP.rotationDegrees(360f / count * i));
            int color = tier == GachaTier.MYTHIC ? (i % 2 == 0 ? 0x70FFFFFF : 0x70101010) : 0x50000000 | (tier.color() & 0xFFFFFF);
            g.fillGradient(-3, 0, 3, (int) reach, color, 0x00000000);
            g.pose().popPose();
        }
        g.pose().popPose();
    }

    private ItemStack stackOf(GachaData.Entry entry) {
        return new ItemStack(BuiltInRegistries.ITEM.get(entry.item()), entry.count());
    }

    private void drawCard(GuiGraphics g, int x, int y, GachaData.Entry entry, boolean big) {
        GachaTier tier = entry.rank();
        int edge = tier.color();
        int dark = 0xFF000000 | ((edge & 0xFEFEFE) >> 1 & 0x7F7F7F);
        g.fillGradient(x, y, x + CARD_W, y + CARD_H, 0xFF1A1226, dark);
        g.renderOutline(x, y, CARD_W, CARD_H, edge);
        g.renderOutline(x + 2, y + 2, CARD_W - 4, CARD_H - 4, (edge & 0xFFFFFF) | 0x66000000);
        g.drawCenteredString(font, tier.label(), x + CARD_W / 2, y + 6, edge);
        g.pose().pushPose();
        g.pose().translate(x + CARD_W / 2f - 24, y + CARD_H / 2f - 22, 0);
        g.pose().scale(3f, 3f, 1f);
        ItemStack stack = stackOf(entry);
        g.renderItem(stack, 0, 0);
        g.renderItemDecorations(font, stack, 0, 0);
        g.pose().popPose();
    }

    private void drawSummary(GuiGraphics g, int mouseX, int mouseY) {
        g.fill(0, 0, width, height, 0xC0000000);
        int cols = Math.min(5, results.size());
        int rows = (results.size() + cols - 1) / cols;
        int gap = 10;
        int totalW = cols * CARD_W + (cols - 1) * gap;
        int totalH = rows * CARD_H + (rows - 1) * gap;
        int x0 = (width - totalW) / 2;
        int y0 = (height - totalH) / 2;
        GachaData.Entry hovered = null;
        for (int i = 0; i < results.size(); i++) {
            int x = x0 + (i % cols) * (CARD_W + gap);
            int y = y0 + (i / cols) * (CARD_H + gap);
            drawCard(g, x, y, results.get(i), false);
            if (inside(mouseX, mouseY, x, y, CARD_W, CARD_H)) {
                hovered = results.get(i);
            }
        }
        g.drawCenteredString(font, Component.translatable("screen.simplywings.altar.summary"), width / 2, y0 - 18, 0xFFF2E6FF);
        hint(g, Component.translatable("screen.simplywings.altar.close"));
        if (hovered != null) {
            g.renderTooltip(font, stackOf(hovered), mouseX, mouseY);
        }
    }

    private void hint(GuiGraphics g, Component text) {
        g.drawCenteredString(font, text, width / 2, height - 16, 0xFF9C8CBC);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        switch (phase) {
            case IDLE -> {
                int by = height - 46;
                if (inside(mx, my, width - 250, by, 112, 30)) {
                    pull(1);
                    return true;
                }
                if (inside(mx, my, width - 130, by, 112, 30)) {
                    pull(10);
                    return true;
                }
                if (inside(mx, my, 18, height - 46, 72, 18)) {
                    tab = tab == Tab.HISTORY ? Tab.NONE : Tab.HISTORY;
                    return true;
                }
                if (inside(mx, my, 98, height - 46, 72, 18)) {
                    tab = tab == Tab.RATES ? Tab.NONE : Tab.RATES;
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }
            case LAUNCH -> {
                startCard(0);
                return true;
            }
            case REVEAL -> {
                GachaTier tier = results.get(card).rank();
                if (now() - phaseStart < cardLength(tier)) {
                    phaseStart = now() - cardLength(tier);
                } else if (card + 1 < results.size()) {
                    startCard(card + 1);
                } else {
                    phase = results.size() > 1 ? Phase.SUMMARY : Phase.IDLE;
                }
                return true;
            }
            default -> {
                phase = Phase.IDLE;
                return true;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (phase != Phase.IDLE && keyCode == 256) {
            phase = results.size() > 1 && phase != Phase.SUMMARY ? Phase.SUMMARY : Phase.IDLE;
            return true;
        }
        if (phase != Phase.IDLE && (keyCode == 32 || keyCode == 257)) {
            return mouseClicked(0, 0, 0);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void pull(int count) {
        Payloads.GachaState state = ClientGacha.state();
        if (state.feathers() < count && !creative()) {
            sound(SoundEvents.VILLAGER_NO, 1f, 0.6f);
            return;
        }
        PacketDistributor.sendToServer(new Payloads.GachaPull(count));
    }
}
