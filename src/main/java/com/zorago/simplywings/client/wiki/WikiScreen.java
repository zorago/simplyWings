package com.zorago.simplywings.client.wiki;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import com.zorago.simplywings.client.WingVisuals;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WikiScreen extends Screen {
    private static final int HEADER = 22;
    private static final String[] STATS = {"endurance", "acceleration", "speed", "handling", "stability", "glide", "lift"};

    private record Hit(int x, int y, int w, int h, Runnable action, ItemStack tooltip) {
    }

    private final Screen parent;
    private final Deque<String> history = new ArrayDeque<>();
    private String current;
    private double scroll;
    private int contentHeight;
    private final List<Hit> hits = new ArrayList<>();

    public WikiScreen(Screen parent, String start) {
        super(Component.translatable("gui.simplywings.wiki"));
        this.parent = parent;
        if (start != null && WikiData.exists(start)) {
            List<String> path = new ArrayList<>();
            if (find(null, start, path)) {
                for (int i = 0; i < path.size() - 1; i++) {
                    history.push(path.get(i));
                }
                current = start;
            }
        }
    }

    public static void open(Screen parent, String start) {
        Minecraft.getInstance().setScreen(new WikiScreen(parent, start));
    }

    private static boolean find(String node, String target, List<String> path) {
        List<String> children = node == null ? WikiData.root() : WikiData.category(node) != null ? WikiData.category(node).children() : List.of();
        path.add(node);
        if (target.equals(node)) {
            return true;
        }
        for (String child : children) {
            if (find(child, target, path)) {
                return true;
            }
        }
        path.remove(path.size() - 1);
        return false;
    }

    private void go(String id) {
        if (id == null || !WikiData.exists(id) && !id.isEmpty()) {
            return;
        }
        history.push(current == null ? "" : current);
        current = id.isEmpty() ? null : id;
        scroll = 0;
    }

    private void back() {
        if (history.isEmpty()) {
            onClose();
            return;
        }
        String prev = history.pop();
        current = prev == null || prev.isEmpty() ? null : prev;
        scroll = 0;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int panelW() {
        return Math.min(width - 24, 380);
    }

    private int left() {
        return (width - panelW()) / 2;
    }

    private int top() {
        return 12;
    }

    private int bottom() {
        return height - 12;
    }

    private static ItemStack stack(String id) {
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null || !BuiltInRegistries.ITEM.containsKey(rl)) {
            return new ItemStack(Items.BARRIER);
        }
        return new ItemStack(BuiltInRegistries.ITEM.get(rl));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        hits.clear();
        int x0 = left();
        int x1 = x0 + panelW();
        int y0 = top();
        int y1 = bottom();
        g.fill(x0, y0, x1, y1, 0xE0120C1C);
        g.renderOutline(x0, y0, x1 - x0, y1 - y0, 0xFF6A3CB8);
        g.fill(x0 + 1, y0 + 1, x1 - 1, y0 + HEADER, 0xFF221638);
        boolean hoverBack = mouseX >= x0 + 4 && mouseX < x0 + 22 && mouseY >= y0 + 3 && mouseY < y0 + 19;
        g.fill(x0 + 4, y0 + 3, x0 + 22, y0 + 19, hoverBack ? 0xFF6A3CB8 : 0xFF3A2660);
        g.drawCenteredString(font, "<", x0 + 13, y0 + 7, 0xFFFFFFFF);
        hits.add(new Hit(x0 + 4, y0 + 3, 18, 16, this::back, null));
        String crumbs = breadcrumb();
        g.drawString(font, font.plainSubstrByWidth(crumbs, x1 - x0 - 34), x0 + 28, y0 + 7, 0xFFE8DDFF, false);
        int cx = x0 + 10;
        int cw = x1 - x0 - 20;
        int ctop = y0 + HEADER + 4;
        int cbottom = y1 - 4;
        g.enableScissor(x0 + 1, ctop - 2, x1 - 1, cbottom);
        int y = ctop - (int) scroll;
        int startY = y;
        WikiData.Page page = current == null ? null : WikiData.page(current);
        if (page != null) {
            y = drawPage(g, page, cx, y, cw, mouseX, mouseY, ctop, cbottom);
        } else {
            List<String> children = current == null ? WikiData.root() : WikiData.category(current) != null ? WikiData.category(current).children() : List.of();
            if (current != null) {
                g.drawString(font, WikiData.title(current), cx, y, 0xFFFFD27A, true);
                y += 16;
            } else {
                g.drawString(font, Component.translatable("gui.simplywings.wiki.title"), cx, y, 0xFFFFD27A, true);
                y += 12;
                for (FormattedCharSequence line : font.split(Component.translatable("gui.simplywings.wiki.intro"), cw)) {
                    g.drawString(font, line, cx, y, 0xFFB8A8D8, false);
                    y += 10;
                }
                y += 6;
            }
            for (String child : children) {
                boolean hover = mouseX >= cx && mouseX < cx + cw && mouseY >= y && mouseY < y + 20 && mouseY >= ctop && mouseY < cbottom;
                g.fill(cx, y, cx + cw, y + 20, hover ? 0x806A3CB8 : 0x40302048);
                g.renderItem(stack(WikiData.icon(child)), cx + 2, y + 2);
                g.drawString(font, WikiData.title(child), cx + 24, y + 6, 0xFFF2E6FF, false);
                g.drawString(font, WikiData.category(child) != null ? ">>" : ">", cx + cw - 16, y + 6, 0xFF9C8CBC, false);
                String target = child;
                hits.add(new Hit(cx, y, cw, 20, () -> go(target), null));
                y += 22;
            }
        }
        contentHeight = y - startY;
        g.disableScissor();
        if (contentHeight > cbottom - ctop) {
            int track = cbottom - ctop;
            int bar = Math.max(16, track * track / contentHeight);
            int by = ctop + (int) ((track - bar) * scroll / Math.max(1, contentHeight - track));
            g.fill(x1 - 5, by, x1 - 2, by + bar, 0xFF6A3CB8);
        }
        for (Hit h : hits) {
            if (h.tooltip() != null && mouseX >= h.x() && mouseX < h.x() + h.w() && mouseY >= h.y() && mouseY < h.y() + h.h()
                    && mouseY >= ctop && mouseY < cbottom) {
                g.renderTooltip(font, h.tooltip(), mouseX, mouseY);
            }
        }
    }

    private String breadcrumb() {
        List<String> parts = new ArrayList<>();
        parts.add("Wiki");
        List<String> stack = new ArrayList<>(history);
        java.util.Collections.reverse(stack);
        for (String s : stack) {
            if (s != null && !s.isEmpty()) {
                parts.add(WikiData.title(s));
            }
        }
        if (current != null) {
            parts.add(WikiData.title(current));
        }
        if (parts.size() > 4) {
            parts = parts.subList(parts.size() - 4, parts.size());
            parts.add(0, "...");
        }
        return String.join(" > ", parts);
    }

    private int drawText(GuiGraphics g, String text, int x, int y, int w, int color) {
        for (String para : text.split("\n")) {
            for (FormattedCharSequence line : font.split(FormattedText.of(para), w)) {
                g.drawString(font, line, x, y, color, false);
                y += 10;
            }
        }
        return y;
    }

    private int drawPage(GuiGraphics g, WikiData.Page page, int x, int y, int w, int mouseX, int mouseY, int ctop, int cbottom) {
        g.renderItem(stack(page.icon()), x, y - 1);
        g.drawString(font, page.title(), x + 20, y + 4, 0xFFFFD27A, true);
        y += 22;
        for (JsonElement el : page.blocks()) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject b = el.getAsJsonObject();
            String type = b.has("type") ? b.get("type").getAsString() : "text";
            switch (type) {
                case "heading" -> {
                    y += 4;
                    g.drawString(font, b.get("text").getAsString(), x, y, 0xFFC9A6FF, true);
                    g.fill(x, y + 10, x + w, y + 11, 0x806A3CB8);
                    y += 16;
                }
                case "text" -> y = drawText(g, b.get("text").getAsString(), x, y, w, 0xFFE0D8EE) + 5;
                case "items" -> {
                    int ix = x;
                    for (JsonElement ie : b.getAsJsonArray("items")) {
                        if (ix + 18 > x + w) {
                            ix = x;
                            y += 20;
                        }
                        y = item(g, ie.getAsString(), ix, y, mouseX, mouseY);
                        ix += 20;
                    }
                    y += 20;
                    y = caption(g, b, x, y, w);
                }
                case "block3d" -> {
                    int h = 70;
                    if (y + h > ctop && y < cbottom) {
                        render3d(g, stack(b.get("block").getAsString()), x + w / 2, y + h / 2);
                    }
                    y += h;
                    y = caption(g, b, x, y, w);
                }
                case "wings3d" -> {
                    int h = 104;
                    if (y + h > ctop && y < cbottom) {
                        renderWings(g, b.get("wing").getAsString(), x + w / 2, y + h - 8, ctop, cbottom);
                    }
                    y += h;
                    y = caption(g, b, x, y, w);
                }
                case "grid" -> y = grid(g, b, x, y, w, mouseX, mouseY);
                case "recipe" -> {
                    int ix = x;
                    for (JsonElement ie : b.getAsJsonArray("inputs")) {
                        slot(g, ix, y);
                        item(g, ie.getAsString(), ix + 1, y + 1, mouseX, mouseY);
                        ix += 20;
                    }
                    g.drawString(font, "->", ix + 2, y + 5, 0xFFE8DDFF, false);
                    slot(g, ix + 18, y);
                    item(g, b.get("output").getAsString(), ix + 19, y + 1, mouseX, mouseY);
                    y += 22;
                    if (b.has("note")) {
                        y = drawText(g, b.get("note").getAsString(), x, y, w, 0xFF9C8CBC) + 4;
                    }
                }
                case "stats" -> {
                    JsonObject v = b.getAsJsonObject("values");
                    for (String s : STATS) {
                        float value = v.has(s) ? v.get(s).getAsFloat() : 0;
                        g.drawString(font, Component.translatable("gui.simplywings.stat." + s), x, y + 1, 0xFFD8CCE8, false);
                        int bx = x + 80;
                        int bw = w - 110;
                        g.fill(bx, y + 2, bx + bw, y + 8, 0xFF2A1E3A);
                        g.fill(bx, y + 2, bx + (int) (bw * Mth.clamp(value / 100f, 0f, 1.2f) / 1.2f), y + 8, value > 100 ? 0xFFFFD27A : 0xFFB46CFF);
                        g.drawString(font, String.valueOf(Math.round(value)), bx + bw + 4, y + 1, 0xFFE8DDFF, false);
                        y += 11;
                    }
                    y += 4;
                }
                case "link" -> {
                    String target = b.get("page").getAsString();
                    if (WikiData.exists(target)) {
                        String text = "> " + (b.has("text") ? b.get("text").getAsString() : WikiData.title(target));
                        boolean hover = mouseX >= x && mouseX < x + font.width(text) && mouseY >= y && mouseY < y + 10;
                        g.drawString(font, text, x, y, hover ? 0xFFFFFFFF : 0xFF7FD8FF, false);
                        if (hover) {
                            g.fill(x, y + 9, x + font.width(text), y + 10, 0xFFFFFFFF);
                        }
                        hits.add(new Hit(x, y, font.width(text), 10, () -> go(target), null));
                        y += 14;
                    }
                }
                default -> {
                }
            }
        }
        return y + 8;
    }

    private int caption(GuiGraphics g, JsonObject b, int x, int y, int w) {
        if (b.has("caption")) {
            y = drawText(g, b.get("caption").getAsString(), x, y, w, 0xFF9C8CBC) + 4;
        }
        return y;
    }

    private void slot(GuiGraphics g, int x, int y) {
        g.fill(x, y, x + 18, y + 18, 0xFF2A1E3A);
        g.renderOutline(x, y, 18, 18, 0xFF4A3670);
    }

    private int item(GuiGraphics g, String id, int x, int y, int mouseX, int mouseY) {
        ItemStack s = stack(id);
        g.renderItem(s, x, y);
        String target = "items/" + (id.contains(":") ? id.substring(id.indexOf(':') + 1) : id);
        String wing = null;
        if (id.startsWith("simplywings:") && id.endsWith("_wings")) {
            String pair = id.substring("simplywings:".length(), id.length() - "_wings".length());
            for (String r : new String[]{"common", "rare", "epic", "legendary", "mythic"}) {
                if (WikiData.page("wings/" + r + "/" + pair) != null) {
                    wing = "wings/" + r + "/" + pair;
                }
            }
        }
        String dest = wing != null ? wing : WikiData.page(target) != null && !target.equals(current) ? target : null;
        hits.add(new Hit(x, y, 16, 16, dest == null ? () -> { } : () -> go(dest), s));
        return y;
    }

    private int grid(GuiGraphics g, JsonObject b, int x, int y, int w, int mouseX, int mouseY) {
        var pattern = b.getAsJsonArray("pattern");
        JsonObject key = b.getAsJsonObject("key");
        int rows = pattern.size();
        int cols = 0;
        for (JsonElement r : pattern) {
            cols = Math.max(cols, r.getAsString().length());
        }
        for (int r = 0; r < rows; r++) {
            String row = pattern.get(r).getAsString();
            for (int c = 0; c < cols; c++) {
                int sx = x + c * 18;
                int sy = y + r * 18;
                slot(g, sx, sy);
                char ch = c < row.length() ? row.charAt(c) : ' ';
                if (ch != ' ' && key.has(String.valueOf(ch))) {
                    item(g, key.get(String.valueOf(ch)).getAsString(), sx + 1, sy + 1, mouseX, mouseY);
                }
            }
        }
        int ax = x + cols * 18 + 6;
        int ay = y + rows * 9 - 9;
        g.drawString(font, "->", ax, ay + 5, 0xFFE8DDFF, false);
        slot(g, ax + 16, ay);
        item(g, b.get("output").getAsString(), ax + 17, ay + 1, mouseX, mouseY);
        if (b.has("count") && b.get("count").getAsInt() > 1) {
            g.drawString(font, "x" + b.get("count").getAsInt(), ax + 38, ay + 5, 0xFFE8DDFF, false);
        }
        y += rows * 18 + 4;
        if (b.has("note")) {
            y = drawText(g, b.get("note").getAsString(), x, y, w, 0xFF9C8CBC) + 4;
        }
        return y;
    }

    private void render3d(GuiGraphics g, ItemStack s, int cx, int cy) {
        Minecraft mc = Minecraft.getInstance();
        float angle = (Util.getMillis() % 8000L) / 8000f * 360f;
        g.pose().pushPose();
        g.pose().translate(cx, cy, 150);
        g.pose().scale(48, -48, 48);
        g.pose().mulPose(Axis.XP.rotationDegrees(22));
        g.pose().mulPose(Axis.YP.rotationDegrees(angle));
        Lighting.setupFor3DItems();
        mc.getItemRenderer().renderStatic(s, ItemDisplayContext.FIXED, 0xF000F0, OverlayTexture.NO_OVERLAY, g.pose(), g.bufferSource(), mc.level, 0);
        g.flush();
        Lighting.setupForFlatItems();
        g.pose().popPose();
    }

    private void renderWings(GuiGraphics g, String wing, int cx, int baseY, int ctop, int cbottom) {
        LocalPlayer player = Minecraft.getInstance().player;
        ResourceLocation id = ResourceLocation.tryParse(wing);
        if (player == null || id == null) {
            return;
        }
        float angle = (float) Math.toRadians(180 + 35 * Math.sin(Util.getMillis() / 1600.0));
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
        WingVisuals.setPreview(Optional.of(id));
        InventoryScreen.renderEntityInInventory(g, cx, baseY, 36f, new Vector3f(0, player.getBbHeight() / 2f + 0.0625f, 0), pose, null, player);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            back();
            return true;
        }
        int ctop = top() + HEADER + 2;
        for (Hit h : List.copyOf(hits)) {
            boolean inHeader = h.y() < top() + HEADER;
            if (mouseX >= h.x() && mouseX < h.x() + h.w() && mouseY >= h.y() && mouseY < h.y() + h.h() && (inHeader || mouseY >= ctop && mouseY < bottom() - 4)) {
                h.action().run();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int view = bottom() - 4 - (top() + HEADER + 4);
        scroll = Mth.clamp(scroll - scrollY * 24, 0, Math.max(0, contentHeight - view));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == 259) {
            back();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
