package com.zorago.simplywings.gacha;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zorago.simplywings.SimplyWings;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public final class OfferingValues extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static volatile Map<Item, Double> values = Map.of();

    private OfferingValues() {
        super(GSON, SimplyWings.MOD_ID + "_offering");
    }

    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(new OfferingValues());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        Map<Item, Double> out = new HashMap<>();
        for (JsonElement element : files.values()) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject object = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                if (id == null) {
                    continue;
                }
                Item item = BuiltInRegistries.ITEM.get(id);
                if (item != Items.AIR) {
                    out.put(item, entry.getValue().getAsDouble());
                }
            }
        }
        values = Map.copyOf(out);
        SimplyWings.LOGGER.info("Loaded {} offering values", out.size());
    }

    public static double value(Item item) {
        return values.getOrDefault(item, 0.0);
    }

    public static double value(ItemStack stack) {
        return stack.isEmpty() ? 0.0 : value(stack.getItem()) * stack.getCount();
    }
}
