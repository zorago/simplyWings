package com.zorago.simplywings.client.show;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zorago.simplywings.SimplyWings;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class ShowModel {
    public static final ResourceLocation LOCATION = SimplyWings.id("gacha/altar_show.json.gz");
    private static ShowModel instance;

    public record Tex(ResourceLocation path, float uw, float uh, int frames, int frameTime) {
    }

    public static final class Face {
        final float[] pos = new float[12];
        final float[] uv = new float[8];
        final float nx;
        final float ny;
        final float nz;
        final int tex;

        Face(float[] pos, float[] uv, Vector3f normal, int tex) {
            System.arraycopy(pos, 0, this.pos, 0, 12);
            System.arraycopy(uv, 0, this.uv, 0, 8);
            this.nx = normal.x;
            this.ny = normal.y;
            this.nz = normal.z;
            this.tex = tex;
        }
    }

    public static final class Bone {
        public final String name;
        public final int parent;
        public final float px;
        public final float py;
        public final float pz;
        public final float rx;
        public final float ry;
        public final float rz;
        public final boolean lit;
        public final Face[] faces;
        int[] children = new int[0];

        Bone(String name, int parent, float[] pivot, float[] rot, boolean lit, Face[] faces) {
            this.name = name;
            this.parent = parent;
            this.px = pivot[0];
            this.py = pivot[1];
            this.pz = pivot[2];
            this.rx = rot[0];
            this.ry = rot[1];
            this.rz = rot[2];
            this.lit = lit;
            this.faces = faces;
        }
    }

    public static final class Anim {
        public final String name;
        public final float length;
        public final boolean loop;
        public final JsonObject meta;
        final float[][][] tracks;

        Anim(String name, float length, boolean loop, JsonObject meta, float[][][] tracks) {
            this.name = name;
            this.length = length;
            this.loop = loop;
            this.meta = meta;
            this.tracks = tracks;
        }

        public float meta(String key, float fallback) {
            return meta.has(key) ? meta.get(key).getAsFloat() : fallback;
        }

        public float[] metaArray(String key) {
            if (!meta.has(key) || !meta.get(key).isJsonArray()) {
                return new float[0];
            }
            JsonArray a = meta.getAsJsonArray(key);
            float[] out = new float[a.size()];
            for (int i = 0; i < out.length; i++) {
                out[i] = a.get(i).getAsFloat();
            }
            return out;
        }

        public boolean has(int bone, int channel) {
            return tracks[bone][channel] != null;
        }

        public void sample(int bone, int channel, float time, float[] out, float fallback) {
            float[] k = tracks[bone][channel];
            if (k == null) {
                out[0] = fallback;
                out[1] = fallback;
                out[2] = fallback;
                return;
            }
            int n = k.length / 5;
            if (time <= k[0] || n == 1) {
                copy(k, 0, out);
                return;
            }
            if (time >= k[(n - 1) * 5]) {
                copy(k, n - 1, out);
                return;
            }
            int lo = 0;
            int hi = n - 1;
            while (hi - lo > 1) {
                int mid = (lo + hi) >>> 1;
                if (k[mid * 5] <= time) {
                    lo = mid;
                } else {
                    hi = mid;
                }
            }
            int i = lo;
            float t0 = k[i * 5];
            float t1 = k[(i + 1) * 5];
            float u = (time - t0) / Math.max(1e-6f, t1 - t0);
            int interpA = (int) k[i * 5 + 4];
            int interpB = (int) k[(i + 1) * 5 + 4];
            if (interpA == 2) {
                copy(k, i, out);
                return;
            }
            if (interpA == 1 || interpB == 1) {
                int a = Math.max(0, i - 1);
                int d = Math.min(n - 1, i + 2);
                for (int c = 0; c < 3; c++) {
                    float p0 = k[a * 5 + 1 + c];
                    float p1 = k[i * 5 + 1 + c];
                    float p2 = k[(i + 1) * 5 + 1 + c];
                    float p3 = k[d * 5 + 1 + c];
                    float u2 = u * u;
                    float u3 = u2 * u;
                    out[c] = 0.5f * (2 * p1 + (-p0 + p2) * u + (2 * p0 - 5 * p1 + 4 * p2 - p3) * u2 + (-p0 + 3 * p1 - 3 * p2 + p3) * u3);
                }
                return;
            }
            for (int c = 0; c < 3; c++) {
                out[c] = k[i * 5 + 1 + c] + (k[(i + 1) * 5 + 1 + c] - k[i * 5 + 1 + c]) * u;
            }
        }

        private static void copy(float[] k, int i, float[] out) {
            out[0] = k[i * 5 + 1];
            out[1] = k[i * 5 + 2];
            out[2] = k[i * 5 + 3];
        }
    }

    public final Tex[] textures;
    public final Bone[] bones;
    public final Map<String, Anim> anims;
    public final Map<String, Integer> boneIndex = new HashMap<>();

    private ShowModel(Tex[] textures, Bone[] bones, Map<String, Anim> anims) {
        this.textures = textures;
        this.bones = bones;
        this.anims = anims;
        for (int i = 0; i < bones.length; i++) {
            boneIndex.put(bones[i].name, i);
        }
        int[] counts = new int[bones.length];
        for (Bone b : bones) {
            if (b.parent >= 0) {
                counts[b.parent]++;
            }
        }
        for (int i = 0; i < bones.length; i++) {
            bones[i].children = new int[counts[i]];
            counts[i] = 0;
        }
        for (int i = 0; i < bones.length; i++) {
            int p = bones[i].parent;
            if (p >= 0) {
                bones[p].children[counts[p]++] = i;
            }
        }
    }

    public static Optional<ShowModel> get() {
        return Optional.ofNullable(instance);
    }

    public int bone(String name) {
        return boneIndex.getOrDefault(name, -1);
    }

    public static void reload(ResourceManager manager) {
        Optional<Resource> resource = manager.getResource(LOCATION);
        if (resource.isEmpty()) {
            instance = null;
            return;
        }
        try (InputStream in = new GZIPInputStream(resource.get().open());
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            instance = parse(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (Exception e) {
            SimplyWings.LOGGER.error("Could not load the altar show", e);
            instance = null;
        }
    }

    private static float[] floats(JsonArray a) {
        float[] out = new float[a.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = a.get(i).getAsFloat();
        }
        return out;
    }

    private static ShowModel parse(JsonObject root) {
        JsonArray texArray = root.getAsJsonArray("textures");
        Tex[] textures = new Tex[texArray.size()];
        for (int i = 0; i < textures.length; i++) {
            JsonObject t = texArray.get(i).getAsJsonObject();
            textures[i] = new Tex(ResourceLocation.parse(t.get("path").getAsString()), t.get("uw").getAsFloat(), t.get("uh").getAsFloat(),
                    t.get("frames").getAsInt(), t.get("frameTime").getAsInt());
        }
        JsonArray boneArray = root.getAsJsonArray("bones");
        Bone[] bones = new Bone[boneArray.size()];
        for (int i = 0; i < bones.length; i++) {
            JsonObject b = boneArray.get(i).getAsJsonObject();
            JsonArray cubes = b.getAsJsonArray("cubes");
            java.util.List<Face> faces = new java.util.ArrayList<>();
            for (JsonElement ce : cubes) {
                buildCube(ce.getAsJsonObject(), textures, faces);
            }
            bones[i] = new Bone(b.get("name").getAsString(), b.get("parent").getAsInt(), floats(b.getAsJsonArray("pivot")),
                    floats(b.getAsJsonArray("rot")), b.get("lit").getAsBoolean(), faces.toArray(new Face[0]));
        }
        Map<String, Anim> anims = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("anims").entrySet()) {
            JsonObject a = e.getValue().getAsJsonObject();
            float[][][] tracks = new float[bones.length][3][];
            for (JsonElement te : a.getAsJsonArray("tracks")) {
                JsonObject t = te.getAsJsonObject();
                tracks[t.get("bone").getAsInt()][t.get("ch").getAsInt()] = floats(t.getAsJsonArray("keys"));
            }
            anims.put(e.getKey(), new Anim(e.getKey(), a.get("length").getAsFloat(), a.get("loop").getAsBoolean(), a.getAsJsonObject("meta"), tracks));
        }
        return new ShowModel(textures, bones, anims);
    }

    private static void buildCube(JsonObject c, Tex[] textures, java.util.List<Face> out) {
        float[] f = floats(c.getAsJsonArray("f"));
        float[] t = floats(c.getAsJsonArray("t"));
        float[] o = floats(c.getAsJsonArray("o"));
        float[] r = floats(c.getAsJsonArray("r"));
        Matrix4f m = new Matrix4f().translate(o[0], o[1], o[2])
                .rotateZ((float) Math.toRadians(r[2])).rotateY((float) Math.toRadians(r[1])).rotateX((float) Math.toRadians(r[0]))
                .translate(-o[0], -o[1], -o[2]);
        float x0 = f[0], y0 = f[1], z0 = f[2], x1 = t[0], y1 = t[1], z1 = t[2];
        float[][][] corners = {
                {{x1, y1, z0}, {x0, y1, z0}, {x0, y0, z0}, {x1, y0, z0}},
                {{x1, y1, z1}, {x1, y1, z0}, {x1, y0, z0}, {x1, y0, z1}},
                {{x0, y1, z1}, {x1, y1, z1}, {x1, y0, z1}, {x0, y0, z1}},
                {{x0, y1, z0}, {x0, y1, z1}, {x0, y0, z1}, {x0, y0, z0}},
                {{x0, y1, z0}, {x1, y1, z0}, {x1, y1, z1}, {x0, y1, z1}},
                {{x0, y0, z1}, {x1, y0, z1}, {x1, y0, z0}, {x0, y0, z0}},
        };
        float[][] normals = {{0, 0, -1}, {1, 0, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}};
        JsonArray faces = c.getAsJsonArray("faces");
        for (int i = 0; i < 6; i++) {
            JsonElement fe = faces.get(i);
            if (fe == null || fe.isJsonNull()) {
                continue;
            }
            JsonArray fa = fe.getAsJsonArray();
            int tex = fa.get(4).getAsInt();
            int rot = fa.get(5).getAsInt();
            Tex tx = textures[tex];
            float u0 = fa.get(0).getAsFloat() / tx.uw();
            float v0 = fa.get(1).getAsFloat() / tx.uh();
            float u1 = fa.get(2).getAsFloat() / tx.uw();
            float v1 = fa.get(3).getAsFloat() / tx.uh();
            float[] uv = {u0, v0, u1, v0, u1, v1, u0, v1};
            int steps = ((rot / 90) % 4 + 4) % 4;
            for (int s = 0; s < steps; s++) {
                uv = new float[]{uv[6], uv[7], uv[0], uv[1], uv[2], uv[3], uv[4], uv[5]};
            }
            float[] pos = new float[12];
            for (int k = 0; k < 4; k++) {
                Vector3f p = m.transformPosition(new Vector3f(corners[i][k][0], corners[i][k][1], corners[i][k][2]));
                pos[k * 3] = p.x;
                pos[k * 3 + 1] = p.y;
                pos[k * 3 + 2] = p.z;
            }
            Vector3f n = m.transformDirection(new Vector3f(normals[i][0], normals[i][1], normals[i][2])).normalize();
            out.add(new Face(pos, uv, n, tex));
        }
    }
}
