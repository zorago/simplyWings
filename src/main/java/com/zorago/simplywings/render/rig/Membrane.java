package com.zorago.simplywings.render.rig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Membrane {
    private static final float LAYER = 0.01f / 16f;
    private static final float BILLOW_SPAN = 0.12f;

    private record Anchor(List<ModelPart> chain, Vector3f local, float u, float v) {
    }

    private record Panel(Anchor[] a, Anchor[] b, int rows) {
    }

    private final List<Panel> panels = new ArrayList<>();
    private final Map<List<ModelPart>, Matrix4f> frame = new IdentityHashMap<>();
    private final float texWidth;
    private final float texHeight;
    private final float[] ventral;
    private final float[] dorsal;
    private float billow;

    private Membrane(float texWidth, float texHeight, float[] ventral, float[] dorsal) {
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.ventral = ventral;
        this.dorsal = dorsal;
    }

    public static Membrane parse(JsonObject json, ModelPart root, Map<String, List<String>> paths, int texWidth, int texHeight) {
        JsonObject sheet = json.getAsJsonObject("sheet");
        float sheetX = sheet.get("x").getAsFloat();
        float sheetY = sheet.get("y").getAsFloat();
        Membrane membrane = new Membrane(texWidth, texHeight, pair(sheet, "ventral"), pair(sheet, "dorsal"));
        Map<String, List<ModelPart>> chains = new java.util.HashMap<>();
        for (JsonElement element : json.getAsJsonArray("panels")) {
            JsonObject panel = element.getAsJsonObject();
            int rows = panel.has("rows") ? panel.get("rows").getAsInt() : 1;
            for (int side : new int[]{1, -1}) {
                Anchor[] a = anchors(panel.getAsJsonArray("a"), side, sheetX, sheetY, root, paths, chains);
                Anchor[] b = anchors(panel.getAsJsonArray("b"), side, sheetX, sheetY, root, paths, chains);
                if (a != null && b != null && a.length == b.length && a.length > 1) {
                    membrane.panels.add(new Panel(a, b, Math.max(1, rows)));
                }
            }
        }
        return membrane.panels.isEmpty() ? null : membrane;
    }

    private static float[] pair(JsonObject o, String key) {
        JsonArray array = o.getAsJsonArray(key);
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat()};
    }

    private static Anchor[] anchors(JsonArray points, int side, float sheetX, float sheetY, ModelPart root,
                                    Map<String, List<String>> paths, Map<String, List<ModelPart>> chains) {
        Anchor[] out = new Anchor[points.size()];
        for (int i = 0; i < out.length; i++) {
            JsonArray p = points.get(i).getAsJsonArray();
            String bone = p.get(0).getAsString();
            String name = bone.equals("root") ? "root" : (side > 0 ? "right_" : "left_") + bone;
            List<ModelPart> chain = chains.computeIfAbsent(name, n -> chain(root, paths.get(n), n));
            if (chain == null) {
                return null;
            }
            float x = p.get(1).getAsFloat();
            float y = p.get(2).getAsFloat();
            float z = p.get(3).getAsFloat();
            Vector3f model = new Vector3f(-side * x / 16f, (24f - y) / 16f, z / 16f);
            Matrix4f rest = matrix(new PoseStack(), chain).invert();
            out[i] = new Anchor(chain, rest.transformPosition(model), x - sheetX, sheetY - y);
        }
        return out;
    }

    private static List<ModelPart> chain(ModelPart root, List<String> path, String name) {
        List<ModelPart> chain = new ArrayList<>();
        chain.add(root);
        if (name.equals("root")) {
            return chain;
        }
        if (path == null) {
            return null;
        }
        ModelPart part = root;
        for (String step : path) {
            part = part.getChild(step);
            chain.add(part);
        }
        return chain;
    }

    private static Matrix4f matrix(PoseStack stack, List<ModelPart> chain) {
        stack.pushPose();
        for (ModelPart part : chain) {
            part.translateAndRotate(stack);
        }
        Matrix4f m = new Matrix4f(stack.last().pose());
        stack.popPose();
        return m;
    }

    public void billow(float billow) {
        this.billow = billow;
    }

    public float billow() {
        return billow;
    }

    public void render(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
        frame.clear();
        for (Panel panel : panels) {
            int n = panel.a().length;
            int rows = panel.rows();
            Vector3f[][] pos = new Vector3f[n][rows + 1];
            Vector3f[] up = new Vector3f[n];
            float[][] u = new float[n][rows + 1];
            float[][] v = new float[n][rows + 1];
            for (int i = 0; i < n; i++) {
                Anchor a = panel.a()[i];
                Anchor b = panel.b()[i];
                Matrix4f ma = frame.computeIfAbsent(a.chain(), c -> matrix(stack, c));
                Matrix4f mb = frame.computeIfAbsent(b.chain(), c -> matrix(stack, c));
                Vector3f pa = ma.transformPosition(a.local(), new Vector3f());
                Vector3f pb = mb.transformPosition(b.local(), new Vector3f());
                up[i] = mb.transformDirection(new Vector3f(0f, 0f, 1f)).normalize();
                float bulge = billow * BILLOW_SPAN * pa.distance(pb);
                for (int r = 0; r <= rows; r++) {
                    float t = r / (float) rows;
                    pos[i][r] = new Vector3f(pa).lerp(pb, t).fma(bulge * Mth.sin((float) Math.PI * t), up[i]);
                    u[i][r] = Mth.lerp(t, a.u(), b.u());
                    v[i][r] = Mth.lerp(t, a.v(), b.v());
                }
            }
            for (int i = 0; i + 1 < n; i++) {
                for (int r = 0; r < rows; r++) {
                    quad(consumer, pos, u, v, up[i], i, r, light, overlay, color);
                }
            }
        }
    }

    private void quad(VertexConsumer consumer, Vector3f[][] pos, float[][] u, float[][] v, Vector3f up, int i, int r,
                      int light, int overlay, int color) {
        int[][] corners = {{i, r}, {i + 1, r}, {i + 1, r + 1}, {i, r + 1}};
        Vector3f p0 = pos[i][r];
        Vector3f normal = new Vector3f(pos[i + 1][r + 1]).sub(p0).cross(new Vector3f(pos[i][r + 1]).sub(pos[i + 1][r]));
        if (normal.lengthSquared() < 1e-12f) {
            normal.set(up);
        }
        normal.normalize();
        if (normal.dot(up) < 0f) {
            normal.negate();
        }
        for (int layer = 0; layer < 2; layer++) {
            float side = layer == 0 ? 1f : -1f;
            float[] offset = layer == 0 ? dorsal : ventral;
            for (int k = 0; k < 4; k++) {
                int[] c = corners[layer == 0 ? k : 3 - k];
                Vector3f p = pos[c[0]][c[1]];
                consumer.addVertex(p.x + side * LAYER * normal.x, p.y + side * LAYER * normal.y, p.z + side * LAYER * normal.z)
                        .setColor(color)
                        .setUv((offset[0] + u[c[0]][c[1]]) / texWidth, (offset[1] + v[c[0]][c[1]]) / texHeight)
                        .setOverlay(overlay)
                        .setLight(light)
                        .setNormal(side * normal.x, side * normal.y, side * normal.z);
            }
        }
    }
}
