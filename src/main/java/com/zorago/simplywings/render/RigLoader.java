package com.zorago.simplywings.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zorago.simplywings.SimplyWings;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;

public final class RigLoader {
    private static final float ROOT_Y = 24f;
    private static final float DEPTH_STEP = 0.012f;
    private static final int DEPTH_LAYERS = 5;

    public record Rig(LayerDefinition layer, Map<String, List<String>> paths) {
    }

    private record Group(String name, float[] origin, float[] rotation, boolean export, List<Object> children) {
    }

    private record Element(float[] from, float[] to, float[] origin, float[] rotation, int u, int v, float inflate,
                           boolean mirror, boolean export) {
    }

    private static final Map<String, Rig> CACHE = new HashMap<>();

    public static ResourceLocation file(String name) {
        return SimplyWings.id("rigs/" + name + ".bbmodel");
    }

    public static Optional<Rig> load(String name) {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(file(name));
        if (resource.isEmpty()) {
            SimplyWings.LOGGER.warn("Wing rig {} not found, falling back to the legacy model", file(name));
            return Optional.empty();
        }
        try (Reader reader = resource.get().openAsReader()) {
            Rig rig = parse(JsonParser.parseReader(reader).getAsJsonObject());
            CACHE.put(name, rig);
            SimplyWings.LOGGER.info("Loaded wing rig {} with {} bones", file(name), rig.paths().size());
            return Optional.of(rig);
        } catch (Exception e) {
            SimplyWings.LOGGER.error("Failed to load wing rig {}", file(name), e);
            return Optional.empty();
        }
    }

    public static Optional<Rig> cached(String name) {
        Rig rig = CACHE.get(name);
        return rig != null ? Optional.of(rig) : load(name);
    }

    private static Rig parse(JsonObject json) {
        JsonObject resolution = json.getAsJsonObject("resolution");
        int width = resolution.get("width").getAsInt();
        int height = resolution.get("height").getAsInt();
        Map<String, JsonObject> groupData = new HashMap<>();
        if (json.has("groups")) {
            for (JsonElement e : json.getAsJsonArray("groups")) {
                JsonObject o = e.getAsJsonObject();
                groupData.put(o.get("uuid").getAsString(), o);
            }
        }
        Map<String, Element> elements = new HashMap<>();
        int index = 0;
        for (JsonElement e : json.getAsJsonArray("elements")) {
            JsonObject o = e.getAsJsonObject();
            if (!"cube".equals(string(o, "type", "cube"))) {
                continue;
            }
            float[] uv = vec(o, "uv_offset", 2);
            elements.put(o.get("uuid").getAsString(), new Element(vec(o, "from", 3), vec(o, "to", 3), vec(o, "origin", 3),
                    vec(o, "rotation", 3), (int) uv[0], (int) uv[1], number(o, "inflate", 0f) + DEPTH_STEP * (index++ % DEPTH_LAYERS),
                    bool(o, "mirror_uv", false), bool(o, "export", true)));
        }
        List<Group> roots = new ArrayList<>();
        for (JsonElement node : json.getAsJsonArray("outliner")) {
            if (node.isJsonObject()) {
                roots.add(group(node.getAsJsonObject(), groupData));
            }
        }
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Map<String, List<String>> paths = new HashMap<>();
        for (Group g : roots) {
            build(root, g, null, elements, new ArrayList<>(), paths);
        }
        return new Rig(LayerDefinition.create(mesh, width, height), paths);
    }

    private static Group group(JsonObject node, Map<String, JsonObject> groupData) {
        JsonObject data = groupData.getOrDefault(node.get("uuid").getAsString(), node);
        List<Object> children = new ArrayList<>();
        for (JsonElement child : node.getAsJsonArray("children")) {
            children.add(child.isJsonObject() ? group(child.getAsJsonObject(), groupData) : child.getAsString());
        }
        return new Group(data.get("name").getAsString(), vec(data, "origin", 3), vec(data, "rotation", 3),
                bool(data, "export", true), children);
    }

    private static void build(PartDefinition parent, Group g, Group parentGroup, Map<String, Element> elements,
                              List<String> path, Map<String, List<String>> paths) {
        if (!g.export()) {
            return;
        }
        CubeListBuilder cubes = CubeListBuilder.create();
        List<Element> rotated = new ArrayList<>();
        for (Object child : g.children()) {
            if (child instanceof String uuid) {
                Element e = elements.get(uuid);
                if (e == null || !e.export()) {
                    continue;
                }
                if (e.rotation()[0] != 0 || e.rotation()[1] != 0 || e.rotation()[2] != 0) {
                    rotated.add(e);
                } else {
                    addBox(cubes, e, g.origin());
                }
            }
        }
        PartDefinition part = parent.addOrReplaceChild(g.name(), cubes,
                pose(g.origin(), parentGroup == null ? null : parentGroup.origin(), g.rotation()));
        List<String> own = new ArrayList<>(path);
        own.add(g.name());
        paths.putIfAbsent(g.name(), own);
        for (int i = 0; i < rotated.size(); i++) {
            Element e = rotated.get(i);
            CubeListBuilder single = CubeListBuilder.create();
            addBox(single, e, e.origin());
            part.addOrReplaceChild(g.name() + "_r" + i, single, pose(e.origin(), g.origin(), e.rotation()));
        }
        for (Object child : g.children()) {
            if (child instanceof Group sub) {
                build(part, sub, g, elements, own, paths);
            }
        }
    }

    private static void addBox(CubeListBuilder builder, Element e, float[] pivot) {
        float sx = e.to()[0] - e.from()[0];
        float sy = e.to()[1] - e.from()[1];
        float sz = e.to()[2] - e.from()[2];
        float x = pivot[0] - e.to()[0];
        float y = -e.from()[1] - sy + pivot[1];
        float z = e.from()[2] - pivot[2];
        builder.texOffs(e.u(), e.v()).mirror(e.mirror())
                .addBox(x, y, z, sx, sy, sz, new CubeDeformation(e.inflate()));
    }

    private static PartPose pose(float[] origin, float[] parentOrigin, float[] rotation) {
        float x = origin[0] - (parentOrigin == null ? 0f : parentOrigin[0]);
        float y = origin[1] - (parentOrigin == null ? 0f : parentOrigin[1]);
        float z = origin[2] - (parentOrigin == null ? 0f : parentOrigin[2]);
        x = -x;
        y = -y + (parentOrigin == null ? ROOT_Y : 0f);
        float xRot = -rotation[0] * Mth.DEG_TO_RAD;
        float yRot = -rotation[1] * Mth.DEG_TO_RAD;
        float zRot = rotation[2] * Mth.DEG_TO_RAD;
        return PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot);
    }

    private static float[] vec(JsonObject o, String key, int size) {
        float[] out = new float[size];
        if (o.has(key) && o.get(key).isJsonArray()) {
            JsonArray array = o.getAsJsonArray(key);
            for (int i = 0; i < size && i < array.size(); i++) {
                out[i] = array.get(i).getAsFloat();
            }
        }
        return out;
    }

    private static float number(JsonObject o, String key, float fallback) {
        return o.has(key) ? o.get(key).getAsFloat() : fallback;
    }

    private static boolean bool(JsonObject o, String key, boolean fallback) {
        return o.has(key) ? o.get(key).getAsBoolean() : fallback;
    }

    private static String string(JsonObject o, String key, String fallback) {
        return o.has(key) ? o.get(key).getAsString() : fallback;
    }

    private RigLoader() {
    }
}
