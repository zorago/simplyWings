package com.zorago.simplywings.anim;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.render.WingModel;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public record WingStyle(List<Slot> slots, float upFraction, float downFraction, float strokeAmplitude,
                        float hoverAmplitude, float vibration, float vibrationRate, float breath, boolean folds,
                        boolean flat, Render render, ResourceLocation glow, Effects effects) {
    public static final int MAX_SLOTS_PER_SIDE = 3;
    public static final float SCALE = 1.5f;

    public enum Render {
        CUTOUT,
        TRANSLUCENT,
        ETHEREAL
    }

    public enum Effects {
        NONE,
        ANGEL,
        DEMON,
        SERAPH,
        PSYCHE,
        THRUSTERS,
        FIRE,
        WATER,
        EARTH,
        AIR,
        FOSSIL,
        PTERO,
        STORM,
        AURORA,
        SHADOW,
        NEBULA,
        FOREST,
        FEATHERS,
        AMETHYST,
        COPPER
    }

    public record Slot(WingModel.Shape shape, float x, float y, float z, float sweep, float lift, float phase,
                       float amplitude) {
        public static Slot single(WingModel.Shape shape) {
            return new Slot(shape, 0f, 0f, 0f, 0f, 0f, 0f, 1f);
        }
    }

    public float strikeAt() {
        return upFraction + downFraction;
    }

    public float stroke(float t) {
        if (t < upFraction) {
            return smooth(t / upFraction);
        }
        if (t < upFraction + downFraction) {
            float x = (t - upFraction) / downFraction;
            return 1f - 2f * x * x;
        }
        float x = (t - upFraction - downFraction) / (1f - upFraction - downFraction);
        return -1f + smooth(x);
    }

    public float curl(float t) {
        if (t < upFraction || t >= upFraction + downFraction) {
            return 0f;
        }
        float x = (t - upFraction) / downFraction;
        return (float) Math.sin(Math.PI * x);
    }

    public float span() {
        float span = 0f;
        for (WingModel.Segment segment : slots.get(0).shape().segments()) {
            span += segment.length();
        }
        return span * SCALE / 16f + 0.2f;
    }

    private static float smooth(float x) {
        float c = Math.max(0f, Math.min(1f, x));
        return c * c * (3f - 2f * c);
    }

    private static ResourceLocation glow(String name) {
        return SimplyWings.id("textures/entity/" + name + "_glow.png");
    }

    public static final WingStyle BASIC = new WingStyle(List.of(Slot.single(WingModel.BASIC)),
            0.45f, 0.25f, 0.7f, 0.8f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle BAT = new WingStyle(List.of(Slot.single(WingModel.BAT)),
            0.4f, 0.22f, 0.8f, 0.9f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle ANGEL = new WingStyle(List.of(Slot.single(WingModel.ANGEL)),
            0.62f, 0.1f, 1.0f, 1.0f, 0f, 0f, 0.05f, true, false, Render.CUTOUT, glow("angel"), Effects.ANGEL);

    public static final WingStyle DRAGONFLY = new WingStyle(List.of(
            new Slot(WingModel.DRAGONFLY, 0f, -0.5f, 0f, 0.2f, 0.05f, 0f, 1f),
            new Slot(WingModel.DRAGONFLY, -0.5f, 3.5f, 0.3f, -0.35f, -0.1f, (float) Math.PI, 0.9f)),
            0.5f, 0.5f, 0.2f, 0f, 0.35f, 25f, 0.02f, false, true, Render.TRANSLUCENT, null, Effects.FOSSIL);

    public static final WingStyle DEMON = new WingStyle(List.of(Slot.single(WingModel.DEMON)),
            0.55f, 0.08f, 1.1f, 1.0f, 0f, 0f, 0.05f, true, false, Render.CUTOUT, glow("demon"), Effects.DEMON);

    public static final WingStyle SERAPH = new WingStyle(List.of(
            new Slot(WingModel.ANGEL, 0f, -2.5f, 0.2f, 0.1f, 0.75f, -0.35f, 0.8f),
            Slot.single(WingModel.ANGEL),
            new Slot(WingModel.ANGEL, 0f, 3f, 0.4f, -0.15f, -0.6f, 0.35f, 0.8f)),
            0.55f, 0.15f, 0.9f, 0.9f, 0f, 0f, 0.05f, true, false, Render.CUTOUT, glow("seraph"), Effects.SERAPH);

    public static final WingStyle PSYCHE = new WingStyle(List.of(
            new Slot(WingModel.PSYCHE_FORE, 0f, 0f, 0f, 0.15f, 0.15f, 0f, 1f),
            new Slot(WingModel.PSYCHE_HIND, 0f, 3.5f, 0.3f, -0.35f, -0.25f, 0.6f, 0.9f)),
            0.5f, 0.3f, 0.6f, 0.4f, 0f, 0f, 0.22f, false, false, Render.ETHEREAL, null, Effects.PSYCHE);

    public static final WingStyle DAEDALUS = new WingStyle(List.of(Slot.single(WingModel.DAEDALUS)),
            0.5f, 0.5f, 0.15f, 0f, 0.05f, 30f, 0.02f, true, false, Render.CUTOUT, null, Effects.THRUSTERS);

    public static final WingStyle FIRE = new WingStyle(List.of(Slot.single(WingModel.FIRE)),
            0.45f, 0.12f, 0.9f, 0.9f, 0.03f, 11f, 0.08f, true, false, Render.ETHEREAL, null, Effects.FIRE);

    public static final WingStyle WATER = new WingStyle(List.of(Slot.single(WingModel.WATER)),
            0.5f, 0.3f, 0.8f, 0.7f, 0f, 0f, 0.14f, true, false, Render.TRANSLUCENT, null, Effects.WATER);

    public static final WingStyle EARTH = new WingStyle(List.of(Slot.single(WingModel.EARTH)),
            0.6f, 0.2f, 0.6f, 0.5f, 0f, 0f, 0.02f, true, false, Render.CUTOUT, glow("earth"), Effects.EARTH);

    public static final WingStyle AIR = new WingStyle(List.of(
            new Slot(WingModel.AIR, 0f, -0.5f, 0f, 0.1f, 0.1f, 0f, 1f),
            new Slot(WingModel.AIR, 0f, 2.5f, 0.3f, -0.25f, -0.2f, 0.3f, 0.85f)),
            0.42f, 0.2f, 0.85f, 0.9f, 0f, 0f, 0.1f, true, false, Render.TRANSLUCENT, null, Effects.AIR);

    public static final WingStyle PTERO = new WingStyle(List.of(Slot.single(WingModel.PTERO)),
            0.5f, 0.18f, 1.0f, 0.9f, 0f, 0f, 0.03f, true, false, Render.CUTOUT, null, Effects.PTERO);

    public static final WingStyle STORM = new WingStyle(List.of(Slot.single(WingModel.STORM)),
            0.4f, 0.12f, 0.9f, 0.9f, 0.04f, 17f, 0.05f, true, false, Render.ETHEREAL, null, Effects.STORM);

    public static final WingStyle AURORA = new WingStyle(List.of(
            new Slot(WingModel.AURORA, 0f, -0.5f, 0f, 0.1f, 0.15f, 0f, 1f),
            new Slot(WingModel.AURORA, 0f, 3f, 0.3f, -0.3f, -0.25f, 0.5f, 0.8f)),
            0.55f, 0.3f, 0.6f, 0.4f, 0f, 0f, 0.2f, false, false, Render.ETHEREAL, null, Effects.AURORA);

    public static final WingStyle SHADOW = new WingStyle(List.of(Slot.single(WingModel.SHADOW)),
            0.45f, 0.14f, 0.95f, 0.9f, 0f, 0f, 0.08f, true, false, Render.TRANSLUCENT, glow("shadow"), Effects.SHADOW);

    public static final WingStyle NEBULA = new WingStyle(List.of(Slot.single(WingModel.NEBULA)),
            0.55f, 0.2f, 0.85f, 0.8f, 0f, 0f, 0.1f, true, false, Render.TRANSLUCENT, glow("nebula"), Effects.NEBULA);

    public static final WingStyle FOREST = new WingStyle(List.of(
            new Slot(WingModel.FOREST, 0f, -0.5f, 0f, 0.15f, 0.1f, 0f, 1f),
            new Slot(WingModel.FOREST, 0f, 3f, 0.3f, -0.3f, -0.2f, 0.4f, 0.85f)),
            0.5f, 0.25f, 0.8f, 0.7f, 0f, 0f, 0.06f, true, false, Render.CUTOUT, null, Effects.FOREST);

    public static final WingStyle CROW = new WingStyle(List.of(Slot.single(WingModel.CROW)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle OWL = new WingStyle(List.of(Slot.single(WingModel.OWL)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle GULL = new WingStyle(List.of(Slot.single(WingModel.GULL)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle HUMMINGBIRD = new WingStyle(List.of(Slot.single(WingModel.HUMMINGBIRD)),
            0.5f, 0.5f, 0.25f, 0f, 0.3f, 30f, 0.02f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle EAGLE = new WingStyle(List.of(Slot.single(WingModel.EAGLE)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle SWIFT = new WingStyle(List.of(Slot.single(WingModel.SWIFT)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle PIGEON = new WingStyle(List.of(Slot.single(WingModel.PIGEON)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle PEACOCK = new WingStyle(List.of(Slot.single(WingModel.PEACOCK)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle PARROT = new WingStyle(List.of(Slot.single(WingModel.PARROT)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.FEATHERS);

    public static final WingStyle BEE = new WingStyle(List.of(
            new Slot(WingModel.BEE, 0f, -0.5f, 0f, 0.2f, 0.05f, 0f, 1f),
            new Slot(WingModel.BEE, -0.5f, 2.5f, 0.3f, -0.35f, -0.1f, (float) Math.PI, 0.85f)),
            0.5f, 0.5f, 0.2f, 0f, 0.35f, 28f, 0.02f, false, true, Render.TRANSLUCENT, null, Effects.NONE);

    public static final WingStyle MOTH = new WingStyle(List.of(
            new Slot(WingModel.MOTH, 0f, 0f, 0f, 0.15f, 0.15f, 0f, 1f),
            new Slot(WingModel.MOTH, 0f, 3f, 0.3f, -0.35f, -0.3f, 0.5f, 0.85f)),
            0.5f, 0.3f, 0.7f, 0.5f, 0f, 0f, 0.08f, false, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle BEETLE = new WingStyle(List.of(
            new Slot(WingModel.BEETLE, 0f, 0.5f, 0.4f, 0f, 0f, 0f, 1f),
            new Slot(WingModel.BEETLE_SHELL, 0f, -0.5f, -0.1f, 0.6f, 0.9f, 0f, 0.3f)),
            0.5f, 0.5f, 0.25f, 0f, 0.25f, 20f, 0.02f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle SQUIRREL = new WingStyle(List.of(Slot.single(WingModel.SQUIRREL)),
            0.5f, 0.3f, 0.4f, 0.5f, 0f, 0f, 0.03f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle FLYING_FISH = new WingStyle(List.of(Slot.single(WingModel.FLYING_FISH)),
            0.4f, 0.2f, 0.6f, 0.7f, 0.05f, 8f, 0.03f, true, false, Render.TRANSLUCENT, null, Effects.NONE);

    public static final WingStyle PHANTOM = new WingStyle(List.of(Slot.single(WingModel.PHANTOM)),
            0.5f, 0.2f, 0.9f, 0.9f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.PTERO);

    public static final WingStyle AMETHYST = new WingStyle(List.of(Slot.single(WingModel.AMETHYST)),
            0.5f, 0.2f, 0.7f, 0.6f, 0f, 0f, 0.03f, true, false, Render.CUTOUT, glow("amethyst"), Effects.AMETHYST);

    public static final WingStyle ORNITHOPTER = new WingStyle(List.of(Slot.single(WingModel.ORNITHOPTER)),
            0.5f, 0.3f, 0.7f, 0.6f, 0f, 0f, 0.03f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle KITE = new WingStyle(List.of(Slot.single(WingModel.KITE)),
            0.5f, 0.3f, 0.4f, 0.6f, 0f, 0f, 0.12f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle COPPER = new WingStyle(List.of(Slot.single(WingModel.COPPER)),
            0.5f, 0.2f, 0.75f, 0.7f, 0.02f, 10f, 0.02f, true, false, Render.CUTOUT, null, Effects.COPPER);

    public static final WingStyle PATCHWORK = new WingStyle(List.of(Slot.single(WingModel.PATCHWORK)),
            0.45f, 0.22f, 0.85f, 0.85f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle NIGHT = new WingStyle(List.of(
            Slot.single(WingModel.NIGHT_MAIN),
            new Slot(WingModel.NIGHT_HIND, 0f, 5f, 0.4f, -0.4f, -0.3f, 0.3f, 0.8f)),
            0.5f, 0.2f, 0.95f, 0.9f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.NONE);

    public static final WingStyle LIGHT = new WingStyle(List.of(
            Slot.single(WingModel.LIGHT_MAIN),
            new Slot(WingModel.LIGHT_HIND, 0f, 5f, 0.4f, -0.4f, -0.3f, 0.3f, 0.8f)),
            0.5f, 0.2f, 0.95f, 0.9f, 0f, 0f, 0.04f, true, false, Render.CUTOUT, null, Effects.NONE);

    private static final Map<ResourceLocation, WingStyle> STYLES = Map.ofEntries(
            Map.entry(SimplyWings.id("basic"), BASIC),
            Map.entry(SimplyWings.id("bat"), BAT),
            Map.entry(SimplyWings.id("angel"), ANGEL),
            Map.entry(SimplyWings.id("dragonfly"), DRAGONFLY),
            Map.entry(SimplyWings.id("demon"), DEMON),
            Map.entry(SimplyWings.id("seraph"), SERAPH),
            Map.entry(SimplyWings.id("psyche"), PSYCHE),
            Map.entry(SimplyWings.id("daedalus"), DAEDALUS),
            Map.entry(SimplyWings.id("fire"), FIRE),
            Map.entry(SimplyWings.id("water"), WATER),
            Map.entry(SimplyWings.id("earth"), EARTH),
            Map.entry(SimplyWings.id("air"), AIR),
            Map.entry(SimplyWings.id("ptero"), PTERO),
            Map.entry(SimplyWings.id("storm"), STORM),
            Map.entry(SimplyWings.id("aurora"), AURORA),
            Map.entry(SimplyWings.id("shadow"), SHADOW),
            Map.entry(SimplyWings.id("nebula"), NEBULA),
            Map.entry(SimplyWings.id("forest"), FOREST),
            Map.entry(SimplyWings.id("night"), NIGHT),
            Map.entry(SimplyWings.id("light"), LIGHT),
            Map.entry(SimplyWings.id("crow"), CROW),
            Map.entry(SimplyWings.id("owl"), OWL),
            Map.entry(SimplyWings.id("gull"), GULL),
            Map.entry(SimplyWings.id("hummingbird"), HUMMINGBIRD),
            Map.entry(SimplyWings.id("eagle"), EAGLE),
            Map.entry(SimplyWings.id("swift"), SWIFT),
            Map.entry(SimplyWings.id("pigeon"), PIGEON),
            Map.entry(SimplyWings.id("peacock"), PEACOCK),
            Map.entry(SimplyWings.id("parrot"), PARROT),
            Map.entry(SimplyWings.id("bee"), BEE),
            Map.entry(SimplyWings.id("moth"), MOTH),
            Map.entry(SimplyWings.id("beetle"), BEETLE),
            Map.entry(SimplyWings.id("squirrel"), SQUIRREL),
            Map.entry(SimplyWings.id("flying_fish"), FLYING_FISH),
            Map.entry(SimplyWings.id("phantom"), PHANTOM),
            Map.entry(SimplyWings.id("amethyst"), AMETHYST),
            Map.entry(SimplyWings.id("ornithopter"), ORNITHOPTER),
            Map.entry(SimplyWings.id("kite"), KITE),
            Map.entry(SimplyWings.id("copper"), COPPER),
            Map.entry(SimplyWings.id("patchwork"), PATCHWORK));

    public static WingStyle of(ResourceLocation model) {
        return STYLES.getOrDefault(model, BASIC);
    }
}
