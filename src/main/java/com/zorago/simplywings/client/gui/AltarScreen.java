package com.zorago.simplywings.client.gui;

import com.mojang.math.Axis;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.client.ClientGacha;
import com.zorago.simplywings.client.show.ClientShows;
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
    private enum Phase { IDLE, SHOW, CARDS, OUTRO }

    private enum Tab { NONE, HISTORY, RATES }

    private Phase phase = Phase.IDLE;
    private Tab tab = Tab.NONE;
    private int seen;
    private List<GachaData.Entry> results = List.of();
    private long phaseStart;
    private CardDeck deck;

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
            deck = new CardDeck(results);
            phase = Phase.SHOW;
            phaseStart = now();
            tab = Tab.NONE;
        }
        if (phase == Phase.SHOW) {
            float t = ClientShows.summonerTime();
            if (t >= 0 ? t >= ClientShows.summonerCardsAt() : now() - phaseStart > 1500) {
                startCards();
            }
        }
        if (phase == Phase.OUTRO && (!ClientShows.summonerShowRunning() || now() - phaseStart > 4000)) {
            phase = Phase.IDLE;
        }
    }

    private void startCards() {
        phase = Phase.CARDS;
        phaseStart = now();
        ClientShows.hold(true);
    }

    private float cardTime() {
        return (now() - phaseStart) / 1000f;
    }

    private void exitCards() {
        ClientShows.release();
        PacketDistributor.sendToServer(new Payloads.GachaClaim());
        phase = Phase.OUTRO;
        phaseStart = now();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        switch (phase) {
            case SHOW -> hint(g, Component.translatable("screen.simplywings.altar.skip"));
            case CARDS -> {
                float t = cardTime();
                GachaData.Entry hovered = deck.render(g, font, t, width, height, mouseX, mouseY);
                hint(g, Component.translatable(deck.done(t) ? "screen.simplywings.altar.close" : "screen.simplywings.altar.skip"));
                if (hovered != null) {
                    g.renderTooltip(font, stackOf(hovered), mouseX, mouseY);
                }
            }
            case OUTRO -> {
            }
            default -> {
                drawCover(g);
                drawIdle(g, mouseX, mouseY);
            }
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
        int w = 0;
        for (String key : keys) {
            w = Math.max(w, font.width(Component.translatable("screen.simplywings.altar." + key)));
        }
        g.fill(x - 6, y - 6, x + w + 8, y + 18 + keys.length * 11, 0xCC0C0614);
        g.drawString(font, Component.translatable("screen.simplywings.altar.rates_title"), x, y, 0xFFE8DDFF, true);
        for (int i = 0; i < keys.length; i++) {
            g.drawString(font, Component.translatable("screen.simplywings.altar." + keys[i]), x, y + 14 + i * 11, 0xFFCFC6DE, false);
        }
    }





    private ItemStack stackOf(GachaData.Entry entry) {
        return new ItemStack(BuiltInRegistries.ITEM.get(entry.item()), entry.count());
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
            case SHOW -> {
                startCards();
                return true;
            }
            case CARDS -> {
                float t = cardTime();
                if (deck.done(t)) {
                    exitCards();
                } else {
                    deck.finish(t);
                }
                return true;
            }
            default -> {
                return true;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (phase != Phase.IDLE && keyCode == 256) {
            if (phase == Phase.SHOW || phase == Phase.CARDS) {
                exitCards();
            } else {
                phase = Phase.IDLE;
            }
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
            sound(com.zorago.simplywings.sound.ModSounds.DENIED.get(), 1f, 0.7f);
            return;
        }
        PacketDistributor.sendToServer(new Payloads.GachaPull(count));
    }

    @Override
    public void removed() {
        ClientShows.skip();
        super.removed();
    }
}
