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
        THRUSTERS
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
        return span / 16f + 0.2f;
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
            0.5f, 0.5f, 0.2f, 0f, 0.35f, 25f, 0.02f, false, true, Render.TRANSLUCENT, null, Effects.NONE);

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

    private static final Map<ResourceLocation, WingStyle> STYLES = Map.of(
            SimplyWings.id("basic"), BASIC,
            SimplyWings.id("bat"), BAT,
            SimplyWings.id("angel"), ANGEL,
            SimplyWings.id("dragonfly"), DRAGONFLY,
            SimplyWings.id("demon"), DEMON,
            SimplyWings.id("seraph"), SERAPH,
            SimplyWings.id("psyche"), PSYCHE,
            SimplyWings.id("daedalus"), DAEDALUS);

    public static WingStyle of(ResourceLocation model) {
        return STYLES.getOrDefault(model, BASIC);
    }
}
