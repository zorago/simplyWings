package com.zorago.simplywings.client.wiki;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class WikiData {
    public record Category(String id, String title, String icon, List<String> children) {
    }

    public record Page(String id, String title, String icon, JsonArray blocks) {
    }

    private static List<String> root = List.of();
    private static Map<String, Category> categories = Map.of();
    private static Map<String, Page> pages = Map.of();

    public static List<String> root() {
        return root;
    }

    public static Category category(String id) {
        return categories.get(id);
    }

    public static Page page(String id) {
        return pages.get(id);
    }

    public static boolean exists(String id) {
        return categories.containsKey(id) || pages.containsKey(id);
    }

    public static String title(String id) {
        Category c = categories.get(id);
        if (c != null) {
            return c.title();
        }
        Page p = pages.get(id);
        return p != null ? p.title() : id;
    }

    public static String icon(String id) {
        Category c = categories.get(id);
        if (c != null) {
            return c.icon();
        }
        Page p = pages.get(id);
        return p != null ? p.icon() : "minecraft:book";
    }

    private static String str(JsonObject o, String key, String fallback) {
        return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsString() : fallback;
    }

    public static void reload(ResourceManager manager) {
        List<String> newRoot = new ArrayList<>();
        Map<String, Category> newCategories = new HashMap<>();
        Map<String, Page> newPages = new HashMap<>();
        try {
            Resource tree = manager.getResource(SimplyWings.id("wiki/tree.json")).orElse(null);
            if (tree != null) {
                try (Reader reader = tree.openAsReader()) {
                    JsonObject o = JsonParser.parseReader(reader).getAsJsonObject();
                    for (JsonElement e : o.getAsJsonArray("root")) {
                        newRoot.add(e.getAsString());
                    }
                    for (Map.Entry<String, JsonElement> e : o.getAsJsonObject("categories").entrySet()) {
                        JsonObject c = e.getValue().getAsJsonObject();
                        List<String> children = new ArrayList<>();
                        for (JsonElement ch : c.getAsJsonArray("children")) {
                            children.add(ch.getAsString());
                        }
                        newCategories.put(e.getKey(), new Category(e.getKey(), str(c, "title", e.getKey()), str(c, "icon", "minecraft:book"), children));
                    }
                }
            }
            for (Map.Entry<ResourceLocation, Resource> e : manager.listResources("wiki/pages",
                    rl -> rl.getNamespace().equals(SimplyWings.MOD_ID) && rl.getPath().endsWith(".json")).entrySet()) {
                String path = e.getKey().getPath();
                String id = path.substring("wiki/pages/".length(), path.length() - ".json".length()).replace("__", "/");
                try (Reader reader = e.getValue().openAsReader()) {
                    JsonObject o = JsonParser.parseReader(reader).getAsJsonObject();
                    newPages.put(id, new Page(id, str(o, "title", id), str(o, "icon", "minecraft:book"),
                            o.has("blocks") ? o.getAsJsonArray("blocks") : new JsonArray()));
                } catch (Exception ex) {
                    SimplyWings.LOGGER.error("Bad wiki page {}", e.getKey(), ex);
                }
            }
        } catch (Exception e) {
            SimplyWings.LOGGER.error("Could not load the wiki", e);
        }
        root = newRoot;
        categories = newCategories;
        pages = newPages;
        SimplyWings.LOGGER.info("Loaded the wiki: {} categories, {} pages", newCategories.size(), newPages.size());
    }

    private WikiData() {
    }
}
