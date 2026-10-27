package com.zorago.simplywings.client.show;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.client.FadingSound;
import com.zorago.simplywings.gacha.GachaTier;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ShowSounds {
    public static final ResourceLocation LOCATION = SimplyWings.id("gacha/show_sounds.json");
    private static final String[] ELEMENTS = {"fire", "dark", "earth", "water", "light", "air"};
    private static final Map<String, List<Cue>> SHOWS = new HashMap<>();
    private static final Map<String, List<Cue>> CARDS = new HashMap<>();
    private static final Map<ResourceLocation, SoundEvent> EVENTS = new HashMap<>();
    private static final List<FadingSound> CARD_BEDS = new ArrayList<>();

    static final class Cue {
        final ResourceLocation[] sounds;
        final String from;
        final float fallback;
        final boolean fallbackEnd;
        final String each;
        final float[] t;
        final float window;
        final float volume;
        final float pitch;
        final float volumeStep;
        final float pitchStep;
        final boolean byTier;
        final float pitchRandom;
        final float volumePower;
        final String at;
        final Vec3 offset;
        final float sample;
        final int index;
        final int edge;
        final float minRank;
        final float maxRank;
        final String until;
        final float untilT;
        final float fade;
        final String except;
        final int tier;
        final int minTier;
        final int maxTier;
        final String meta;

        Cue(JsonObject o) {
            JsonElement s = o.get("sound");
            if (s.isJsonArray()) {
                JsonArray a = s.getAsJsonArray();
                sounds = new ResourceLocation[a.size()];
                for (int i = 0; i < sounds.length; i++) {
                    sounds[i] = ResourceLocation.parse(a.get(i).getAsString());
                }
            } else {
                sounds = new ResourceLocation[]{ResourceLocation.parse(s.getAsString())};
            }
            from = string(o, "from", "start");
            JsonElement or = o.get("or");
            fallbackEnd = or != null && or.isJsonPrimitive() && or.getAsJsonPrimitive().isString() && or.getAsString().equals("end");
            fallback = or == null || fallbackEnd ? 0 : or.getAsFloat();
            each = string(o, "each", null);
            t = floats(o.get("t"));
            window = number(o, "window", -1);
            volume = number(o, "volume", 1);
            pitch = number(o, "pitch", 1);
            volumeStep = number(o, "volume_step", 0);
            pitchStep = number(o, "pitch_step", 0);
            byTier = string(o, "step_by", "index").equals("tier");
            pitchRandom = number(o, "pitch_random", 0);
            volumePower = number(o, "volume_power", 0);
            at = string(o, "at", "altar");
            float[] off = o.has("offset") ? floats(o.get("offset")) : new float[3];
            offset = new Vec3(off[0], off[1], off[2]);
            sample = number(o, "sample", 0);
            index = (int) number(o, "index", -1);
            edge = (int) number(o, "edge", -1);
            minRank = number(o, "min_rank", -Float.MAX_VALUE);
            maxRank = number(o, "max_rank", Float.MAX_VALUE);
            until = string(o, "until", null);
            untilT = number(o, "until_t", 0);
            fade = number(o, "fade", 0);
            except = string(o, "except", null);
            tier = tier(o.get("tier"), -1);
            minTier = tier(o.get("min_tier"), Integer.MIN_VALUE);
            maxTier = tier(o.get("max_tier"), Integer.MAX_VALUE);
            meta = string(o, "meta", null);
        }

        SoundEvent sound(int i) {
            return event(sounds[Math.max(0, Math.min(sounds.length - 1, i))]);
        }

        boolean tierMatches(int value) {
            return (tier < 0 || value == tier) && value >= minTier && value <= maxTier;
        }

        float pitch(float step, float random) {
            return pitch + pitchStep * step + random;
        }
    }

    private static String string(JsonObject o, String key, String fallback) {
        return o.has(key) ? o.get(key).getAsString() : fallback;
    }

    private static float number(JsonObject o, String key, float fallback) {
        return o.has(key) ? o.get(key).getAsFloat() : fallback;
    }

    private static float[] floats(JsonElement e) {
        if (e == null) {
            return new float[]{0};
        }
        if (!e.isJsonArray()) {
            return new float[]{e.getAsFloat()};
        }
        JsonArray a = e.getAsJsonArray();
        float[] out = new float[a.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = a.get(i).getAsFloat();
        }
        return out;
    }

    private static int tier(JsonElement e, int fallback) {
        if (e == null) {
            return fallback;
        }
        if (e.getAsJsonPrimitive().isNumber()) {
            return e.getAsInt();
        }
        return GachaTier.valueOf(e.getAsString().toUpperCase(Locale.ROOT)).ordinal();
    }

    static SoundEvent event(ResourceLocation id) {
        return EVENTS.computeIfAbsent(id, k -> {
            SoundEvent registered = BuiltInRegistries.SOUND_EVENT.get(k);
            return registered != null ? registered : SoundEvent.createVariableRangeEvent(k);
        });
    }

    public static void reload(ResourceManager manager) {
        SHOWS.clear();
        CARDS.clear();
        EVENTS.clear();
        Optional<Resource> resource = manager.getResource(LOCATION);
        if (resource.isEmpty()) {
            return;
        }
        try (Reader reader = resource.get().openAsReader()) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> e : root.entrySet()) {
                if (e.getKey().equals("cards")) {
                    for (Map.Entry<String, JsonElement> c : e.getValue().getAsJsonObject().entrySet()) {
                        List<Cue> cues = new ArrayList<>();
                        if (c.getValue().isJsonObject()) {
                            for (Map.Entry<String, JsonElement> byTier : c.getValue().getAsJsonObject().entrySet()) {
                                for (JsonElement cue : byTier.getValue().getAsJsonArray()) {
                                    JsonObject o = cue.getAsJsonObject().deepCopy();
                                    o.addProperty("tier", byTier.getKey());
                                    cues.add(new Cue(o));
                                }
                            }
                        } else {
                            cues.addAll(list(c.getValue().getAsJsonArray()));
                        }
                        CARDS.put(c.getKey(), cues);
                    }
                } else {
                    SHOWS.put(e.getKey(), list(e.getValue().getAsJsonArray()));
                }
            }
        } catch (Exception e) {
            SimplyWings.LOGGER.error("Could not load the show sounds", e);
            SHOWS.clear();
            CARDS.clear();
        }
    }

    private static List<Cue> list(JsonArray array) {
        List<Cue> out = new ArrayList<>();
        for (JsonElement e : array) {
            out.add(new Cue(e.getAsJsonObject()));
        }
        return out;
    }

    public static void card(String key, GachaTier tier, Random random) {
        List<Cue> cues = CARDS.get(key);
        if (cues == null) {
            return;
        }
        for (Cue c : cues) {
            if (!c.tierMatches(tier.ordinal())) {
                continue;
            }
            float pitch = c.pitch(tier.ordinal(), c.pitchRandom > 0 ? random.nextFloat() * c.pitchRandom : 0);
            if (c.fade > 0) {
                FadingSound bed = new FadingSound(c.sound(0), SoundSource.MASTER, c.volume, pitch, null, c.fade, () -> true);
                CARD_BEDS.add(bed);
                Minecraft.getInstance().getSoundManager().play(bed);
            } else {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(c.sound(0), pitch, c.volume));
            }
        }
    }

    public static void fadeCards() {
        CARD_BEDS.forEach(FadingSound::fadeOut);
        CARD_BEDS.clear();
    }

    static void tick(ClientShows.Show show, ShowModel model, float now, float power) {
        List<Cue> cues = SHOWS.getOrDefault(show.anim.name, SHOWS.get("default"));
        if (cues == null) {
            return;
        }
        ShowModel.Anim a = show.anim;
        float rank = a.meta("rank", 0);
        for (Cue c : cues) {
            if (c.meta != null && !(a.meta(c.meta, 0) > 0) || rank < c.minRank || rank > c.maxRank || a.name.equals(c.except)) {
                continue;
            }
            if (c.each == null) {
                float base = c.from.equals("start") ? 0 : a.meta(c.from, c.fallbackEnd ? a.length : c.fallback);
                play(show, model, now, power, c, base, 0, 0);
                continue;
            }
            if (!a.meta.has(c.each) || !a.meta.get(c.each).isJsonArray()) {
                continue;
            }
            JsonArray points = a.meta.getAsJsonArray(c.each);
            int n = 0;
            for (JsonElement p : points) {
                if (p.isJsonArray()) {
                    float start = p.getAsJsonArray().get(0).getAsFloat();
                    float dur = p.getAsJsonArray().get(1).getAsFloat();
                    play(show, model, now, power, c, start, n++, 0);
                    play(show, model, now, power, c, start + dur, n++, 1);
                } else {
                    play(show, model, now, power, c, p.getAsFloat(), n++, 0);
                }
            }
        }
    }

    private static void play(ClientShows.Show show, ShowModel model, float now, float power, Cue c, float base, int i, int edge) {
        if (c.index >= 0 && i != c.index || c.edge >= 0 && edge != c.edge) {
            return;
        }
        int tier = i < show.payload.tiers().size() ? show.payload.tiers().get(i) : 0;
        if (!c.tierMatches(tier)) {
            return;
        }
        boolean fire = false;
        for (float t : c.t) {
            float at = base + t;
            if (ClientShows.crossed(show, at, now) && (c.window < 0 || now < at + c.window)) {
                fire = true;
            }
        }
        if (!fire) {
            return;
        }
        Vec3 pos = anchor(show, model, c.at, i, base + c.sample);
        if (pos == null) {
            return;
        }
        float step = c.byTier ? tier : i;
        float volume = c.volume + c.volumeStep * step + c.volumePower * power;
        float pitch = c.pitch(step, c.pitchRandom > 0 ? show.random.nextFloat() * c.pitchRandom : 0);
        if (c.fade > 0) {
            float stop = c.until == null ? Float.MAX_VALUE : show.anim.meta(c.until, show.anim.length) + c.untilT;
            Vec3 at = pos.add(c.offset);
            Minecraft.getInstance().getSoundManager().play(new FadingSound(c.sound(i), SoundSource.BLOCKS, volume, pitch, at, c.fade,
                    () -> ClientShows.alive(show) && show.lastTime < stop));
            return;
        }
        ClientShows.sound(show, pos.add(c.offset), c.sound(i), volume, pitch);
    }

    private static Vec3 anchor(ClientShows.Show show, ShowModel model, String at, int i, float time) {
        return switch (at) {
            case "altar" -> ClientShows.toWorld(show, new Vector3f(0, 16, 0));
            case "crystal" -> ClientShows.toWorld(show, new Vector3f(0, 50, 0));
            case "hover" -> ClientShows.toWorld(show, new Vector3f(0, 96, 0));
            case "top" -> ClientShows.toWorld(show, new Vector3f(0, show.anim.meta("top", 384), 0));
            case "flame" -> {
                if (i >= ELEMENTS.length) {
                    yield null;
                }
                ShowModel.Bone bone = model.bones[Math.max(0, model.bone("flame_" + ELEMENTS[i]))];
                yield ClientShows.toWorld(show, new Vector3f(bone.px, bone.py, bone.pz)).add(0, 1.6, 0);
            }
            default -> ClientShows.bone(model, show, at.replace("{i}", Integer.toString(i)), time);
        };
    }

    private ShowSounds() {
    }
}
