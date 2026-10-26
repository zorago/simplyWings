package com.zorago.simplywings.client.gui;

import com.mojang.math.Axis;
import com.zorago.simplywings.gacha.GachaData;
import com.zorago.simplywings.gacha.GachaTier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class CardDeck {
    public static final int W = 70;
    public static final int H = 96;
    private static final float[] PRE = {0f, 0f, 0.16f, 0.32f, 0.48f, 0.6f};
    private static final float[] FLIP = {0.2f, 0.28f, 0.42f, 0.62f, 0.8f, 0.95f};
    private static final float[] GAP = {0.1f, 0.14f, 0.3f, 0.45f, 0.6f, 0.7f};
    private static final int[] SPARKS = {6, 12, 30, 48, 72, 96};
    private static final int[] RAYS = {0, 0, 8, 12, 18, 24};
    private static final float[] FLASH = {0f, 0f, 0.22f, 0.38f, 0.55f, 0.7f};
    private static final float[] SHAKE = {0f, 0f, 1f, 2.5f, 4f, 5f};

    private final List<GachaData.Entry> results;
    private final float[] deal;
    private final float[] antic;
    private final float[] flipStart;
    private final float[] flipEnd;
    private final boolean[] opened;
    private final GachaTier best;
    private final List<float[]> sparks = new ArrayList<>();
    private final List<float[]> bursts = new ArrayList<>();
    private final Random random = new Random();
    private float offset;
    private float flash;
    private int flashColor;
    private float shake;
    private float last;

    public CardDeck(List<GachaData.Entry> results) {
        this.results = results;
        int n = results.size();
        deal = new float[n];
        antic = new float[n];
        flipStart = new float[n];
        flipEnd = new float[n];
        opened = new boolean[n];
        GachaTier top = GachaTier.B;
        float f = 0.07f * n + 0.35f;
        for (int i = 0; i < n; i++) {
            GachaTier t = results.get(i).rank();
            if (t.atLeast(top)) {
                top = t;
            }
            int r = t.ordinal();
            deal[i] = 0.07f * i;
            antic[i] = f;
            flipStart[i] = f + PRE[r];
            flipEnd[i] = flipStart[i] + FLIP[r];
            f = flipStart[i] + FLIP[r] * 0.6f + GAP[r];
        }
        best = top;
    }

    public boolean done(float t) {
        return results.isEmpty() || t + offset > flipEnd[flipEnd.length - 1] + 0.25f;
    }

    public void finish(float t) {
        if (!results.isEmpty()) {
            offset = Math.max(offset, flipEnd[flipEnd.length - 1] + 0.3f - t);
        }
    }

    private static void sound(SoundEvent event, float pitch, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
    }

    private static void openSound(GachaTier tier) {
        switch (tier) {
            case B -> sound(SoundEvents.ITEM_PICKUP, 1.2f, 0.6f);
            case A -> sound(SoundEvents.NOTE_BLOCK_CHIME.value(), 1.4f, 0.8f);
            case S -> {
                sound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.8f, 1f);
                sound(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0f, 0.9f);
            }
            case SS -> {
                sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.1f, 0.9f);
                sound(SoundEvents.FIREWORK_ROCKET_BLAST, 0.9f, 0.8f);
            }
            case SSS -> {
                sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1f);
                sound(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 0.8f, 0.9f);
                sound(SoundEvents.ENDER_DRAGON_GROWL, 1.2f, 0.35f);
            }
            case MYTHIC -> {
                sound(SoundEvents.END_PORTAL_SPAWN, 1.0f, 0.6f);
                sound(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 0.6f, 1f);
                sound(SoundEvents.ENDER_DRAGON_GROWL, 0.7f, 0.5f);
            }
        }
    }

    private float[] slot(int i, int width, int height, float scale) {
        int n = results.size();
        if (n == 1) {
            return new float[]{width / 2f, height / 2f - 4, scale * 1.7f};
        }
        int cols = Math.min(5, n);
        int rows = (n + cols - 1) / cols;
        float gap = 12 * scale;
        float totalW = cols * W * scale + (cols - 1) * gap;
        float totalH = rows * H * scale + (rows - 1) * gap;
        float x0 = (width - totalW) / 2f + W * scale / 2f;
        float y0 = (height - totalH) / 2f + H * scale / 2f + 8;
        return new float[]{x0 + (i % cols) * (W * scale + gap), y0 + (i / cols) * (H * scale + gap), scale};
    }

    public GachaData.Entry render(GuiGraphics g, Font font, float time, int width, int height, int mouseX, int mouseY) {
        float t = time + offset;
        float dt = Mth.clamp(t - last, 0f, 0.1f);
        last = t;
        int n = results.size();
        float scale = Math.min(1f, Math.min((width - 40) / (5f * W + 48f), (height - 60) / (2f * H + 12f)));
        for (int i = 0; i < n; i++) {
            if (!opened[i] && t >= flipEnd[i]) {
                opened[i] = true;
                open(i, width, height, scale);
            }
        }
        shake = Math.max(0, shake - dt * 18f);
        float sx = (random.nextFloat() - 0.5f) * shake;
        float sy = (random.nextFloat() - 0.5f) * shake;
        GachaData.Entry hovered = null;
        for (float[] b : bursts) {
            drawRays(g, b, t);
        }
        bursts.removeIf(b -> t - b[3] > 0.9f);
        for (int i = 0; i < n; i++) {
            if (t < deal[i]) {
                continue;
            }
            GachaTier tier = results.get(i).rank();
            float[] s = slot(i, width, height, scale);
            float p = Mth.clamp((t - deal[i]) / 0.28f, 0f, 1f);
            float e = 1f + 2.7f * (float) Math.pow(p - 1, 3) + 1.7f * (float) Math.pow(p - 1, 2);
            float x = Mth.lerp(e, width / 2f, s[0]) + sx;
            float y = Mth.lerp(e, height + H, s[1]) + sy;
            float rot = (1f - p) * 25f * (i % 2 == 0 ? 1 : -1);
            float lift = 0;
            float glow = 0;
            if (t >= antic[i] && t < flipEnd[i] && tier.atLeast(GachaTier.S)) {
                float k = Mth.clamp((t - antic[i]) / Math.max(0.01f, flipStart[i] - antic[i]), 0f, 1f);
                x += (float) Math.sin(t * 70) * 1.6f * k * tier.ordinal() / 3f;
                lift = -7 * k * s[2];
                glow = k;
            }
            float angle = 0;
            boolean front = opened[i];
            if (t >= flipStart[i] && t < flipEnd[i]) {
                float u = (t - flipStart[i]) / (flipEnd[i] - flipStart[i]);
                float turns = tier.atLeast(GachaTier.SS) ? 3f : 1f;
                angle = (float) (Math.PI * turns * (0.5 - 0.5 * Math.cos(Math.PI * u)));
                front = ((int) Math.floor((angle + Math.PI / 2) / Math.PI)) % 2 == 1;
                lift -= 4 * (float) Math.sin(Math.PI * u) * s[2];
            }
            float squash = angle == 0 ? 1f : Math.max(0.04f, Math.abs((float) Math.cos(angle)));
            g.pose().pushPose();
            g.pose().translate(x, y + lift, 200 + i);
            g.pose().mulPose(Axis.ZP.rotationDegrees(rot));
            g.pose().scale(s[2] * squash, s[2], 1f);
            if (glow > 0) {
                int c = (int) (glow * 160) << 24 | (tier.color() & 0xFFFFFF);
                g.fill(-W / 2 - 3, -H / 2 - 3, W / 2 + 3, H / 2 + 3, c);
            }
            if (front && opened[i] && tier == best && best.atLeast(GachaTier.S)) {
                int a = (int) (90 + 70 * Math.sin(t * 5));
                g.fill(-W / 2 - 4, -H / 2 - 4, W / 2 + 4, H / 2 + 4, a << 24 | (tier.color() & 0xFFFFFF));
            }
            if (front) {
                drawFront(g, font, -W / 2, -H / 2, results.get(i));
            } else {
                drawBack(g, -W / 2, -H / 2, t);
            }
            g.pose().popPose();
            float hw = W * s[2] / 2f;
            float hh = H * s[2] / 2f;
            if (opened[i] && mouseX >= s[0] - hw && mouseX < s[0] + hw && mouseY >= s[1] - hh && mouseY < s[1] + hh) {
                hovered = results.get(i);
            }
        }
        g.pose().pushPose();
        g.pose().translate(0, 0, 400);
        sparks.removeIf(p -> (p[4] -= dt) <= 0);
        for (float[] p : sparks) {
            p[0] += p[2] * dt;
            p[1] += p[3] * dt;
            p[3] += 140 * dt;
            p[2] *= 0.985f;
            float life = p[4] / p[5];
            int size = Math.max(1, Math.round(p[7] * (0.4f + 0.6f * life)));
            int a = (int) (255 * Math.min(1f, life * 1.5f));
            g.fill((int) p[0], (int) p[1], (int) p[0] + size, (int) p[1] + size, a << 24 | ((int) p[6] & 0xFFFFFF));
        }
        if (flash > 0) {
            g.fill(0, 0, width, height, (int) (Math.min(1f, flash) * 255) << 24 | (flashColor & 0xFFFFFF));
            flash = Math.max(0, flash - dt * 3.2f);
        }
        g.pose().popPose();
        return hovered;
    }

    private void open(int i, int width, int height, float scale) {
        GachaTier tier = results.get(i).rank();
        int r = tier.ordinal();
        float[] s = slot(i, width, height, scale);
        openSound(tier);
        float power = 1f + r * 0.45f;
        for (int k = 0; k < SPARKS[r]; k++) {
            double a = random.nextDouble() * Math.PI * 2;
            float v = (40 + random.nextFloat() * 160) * power;
            int color = tier == GachaTier.MYTHIC ? (k % 2 == 0 ? 0xFFFFFF : 0x6F7BFF) : (k % 3 == 0 ? 0xFFFFFF : tier.color());
            sparks.add(new float[]{s[0], s[1], (float) Math.cos(a) * v, (float) Math.sin(a) * v - 60, 0.5f + random.nextFloat() * 0.6f,
                    1.1f, color, 1.5f + random.nextFloat() * (1.5f + r)});
        }
        if (RAYS[r] > 0) {
            bursts.add(new float[]{s[0], s[1], r, last});
        }
        if (FLASH[r] > 0) {
            flash = Math.max(flash, FLASH[r]);
            flashColor = tier == GachaTier.MYTHIC ? 0xFFFFFF : tier.color();
        }
        shake = Math.max(shake, SHAKE[r] * 2);
    }

    private void drawRays(GuiGraphics g, float[] b, float t) {
        int r = (int) b[2];
        GachaTier tier = GachaTier.values()[r];
        float p = Mth.clamp((t - b[3]) / 0.9f, 0f, 1f);
        float reach = (60 + 60 * r) * (0.3f + 0.7f * (1 - (1 - p) * (1 - p)));
        int alpha = (int) (170 * (1 - p));
        g.pose().pushPose();
        g.pose().translate(b[0], b[1], 150);
        g.pose().mulPose(Axis.ZP.rotationDegrees(t * 40));
        for (int i = 0; i < RAYS[r]; i++) {
            g.pose().pushPose();
            g.pose().mulPose(Axis.ZP.rotationDegrees(360f / RAYS[r] * i));
            int color = tier == GachaTier.MYTHIC ? (i % 2 == 0 ? 0xFFFFFF : 0x6F7BFF) : tier.color();
            g.fillGradient(-2 - r / 2, 0, 2 + r / 2, (int) reach, alpha << 24 | (color & 0xFFFFFF), 0);
            g.pose().popPose();
        }
        g.pose().popPose();
    }

    private static void drawBack(GuiGraphics g, int x, int y, float t) {
        g.fillGradient(x, y, x + W, y + H, 0xFF2C1A46, 0xFF110A1C);
        g.renderOutline(x, y, W, H, 0xFF9B5CFF);
        g.renderOutline(x + 3, y + 3, W - 6, H - 6, 0x669B5CFF);
        int cx = x + W / 2;
        int cy = y + H / 2;
        for (int k = 0; k < 9; k++) {
            int w = 9 - k;
            g.fill(cx - w, cy - 9 + k, cx + w, cy - 8 + k, 0xFF6A3CB8);
            g.fill(cx - w, cy + 8 - k, cx + w, cy + 9 - k, 0xFF6A3CB8);
        }
        g.fill(cx - 2, cy - 2, cx + 2, cy + 2, 0xFFE8D8FF);
    }

    static void drawFront(GuiGraphics g, Font font, int x, int y, GachaData.Entry entry) {
        GachaTier tier = entry.rank();
        int edge = tier.color();
        int dark = 0xFF000000 | ((edge & 0xFEFEFE) >> 1 & 0x7F7F7F);
        g.fillGradient(x, y, x + W, y + H, 0xFF1A1226, dark);
        g.renderOutline(x, y, W, H, edge);
        g.renderOutline(x + 2, y + 2, W - 4, H - 4, (edge & 0xFFFFFF) | 0x66000000);
        g.drawCenteredString(font, tier.label(), x + W / 2, y + 6, edge);
        g.pose().pushPose();
        g.pose().translate(x + W / 2f - 24, y + H / 2f - 22, 0);
        g.pose().scale(3f, 3f, 1f);
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(entry.item()), entry.count());
        g.renderItem(stack, 0, 0);
        g.renderItemDecorations(font, stack, 0, 0);
        g.pose().popPose();
    }
}
