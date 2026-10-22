package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class FireRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int HEAT = 2;
    private static final int STREAM = 3;
    private static final int[] IDLES = {40, 64, 30};
    private static final int TERTIALS = 3;
    private static final int SECONDARIES = 4;
    private static final int EMBERS = 4;
    private static final float UP = 0.45f;
    private static final float DOWN = 0.12f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.7f, 0.1f, 1.05f, 1.12f, -0.45f, -0.95f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.18f, -0.28f, -0.1f, 0.3f, 0.35f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.5f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.65f, 0.3f, -0.1f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.12f, -0.08f, -0.3f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.05f, -0.15f, 0.15f, 0.3f, 0.2f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private Side right;
    private Side left;

    public FireRig() {
        super("fire");
    }

    @Override
    public Material material() {
        return Material.EMISSIVE;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    private static final class Side {
        final FeatherWing wing;
        final float s;
        final Bone[] tertial = new Bone[TERTIALS];
        final Bone[] secondary = new Bone[SECONDARIES];
        final Bone[] ember = new Bone[EMBERS];

        Side(Bones b, String p, float s) {
            this.s = s;
            wing = new FeatherWing(b, p, s, 1.25f, 2.75f, 2.85f);
            for (int k = 0; k < TERTIALS; k++) {
                tertial[k] = b.get(p + "tertial_" + (k + 1));
            }
            for (int k = 0; k < SECONDARIES; k++) {
                secondary[k] = b.get(p + "secondary_" + (k + 1));
            }
            for (int k = 0; k < EMBERS; k++) {
                ember[k] = b.get(p + "ember_" + (k + 1));
            }
        }
    }

    @Override
    protected void onBind(Bones bones) {
        right = new Side(bones, "right_", 1f);
        left = new Side(bones, "left_", -1f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.25f, 0.65f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.6f);
        float flare = 0f;
        if (m.beating()) {
            float t = m.beat;
            if (t >= UP - 0.05f && t <= UP + DOWN + 0.15f) {
                flare = m.beatWeight;
            }
        }
        float heat = 1f + 0.45f * flare + 0.25f * m.w(WingMotion.CHARGE) + 0.2f * m.w(WingMotion.BRAKE)
                - 0.15f * m.w(WingMotion.TUCK);
        memory.spring(HEAT, heat, flare > 0f ? 0.5f : 0.18f, 0.55f);
        memory.spring(STREAM, Mth.clamp(m.speed / 35f, 0f, 1f), 0.12f, 0.75f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        java.util.Arrays.fill(drive.v, 0f);
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float heat = memory.get(HEAT, pt);
        float stream = memory.get(STREAM, pt);
        float ground = m.w(WingMotion.GROUND);
        float flying = 1f - ground - m.w(WingMotion.CHARGE);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float groundFold = 1f;
        float groundStroke = 0.12f;
        float groundFan = 0f;
        float groundSweep = 0f;
        float scaleIdle = 1f;
        float flicker = 1f;
        float scatter = 0f;
        if (idle > 0.001f) {
            switch (variant) {
                case 0 -> {
                    float open = idle * Mth.sin((float) Math.PI * Mth.clamp(p * 1.15f, 0f, 1f));
                    groundFold = Mth.lerp(open, 1f, 0.5f);
                    groundStroke = Mth.lerp(open, 0.12f, 0.4f);
                    groundFan = open * 1.1f;
                    scaleIdle = 1f + 0.55f * open;
                    flicker = 1f + 0.8f * open;
                }
                case 1 -> {
                    float dim = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((0.92f - p) * 5f, 0f, 1f));
                    float pop = idle * Math.max(0f, Mth.sin((float) Math.PI * Mth.clamp((p - 0.85f) / 0.15f, 0f, 1f)));
                    groundStroke = 0.12f - 0.1f * dim + 0.25f * pop;
                    groundSweep = -0.08f * dim;
                    scaleIdle = 1f - 0.45f * dim + 0.5f * pop;
                    flicker = 1f - 0.6f * dim;
                }
                default -> {
                    float flick = idle * Mth.sin((float) Math.PI * Mth.clamp(p * 2.2f, 0f, 1f));
                    float snap = idle * Mth.sin((float) Math.PI * 2f * Mth.clamp(p * 2.2f, 0f, 1f));
                    groundFold = 1f - 0.35f * flick;
                    groundStroke = 0.12f + 0.3f * flick;
                    groundSweep = 0.25f * snap;
                    groundFan = 0.6f * flick;
                    scatter = idle * smooth(Mth.clamp(p * 1.6f, 0f, 1f));
                }
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, groundFold).set(RigPose.STROKE, groundStroke)
                .set(RigPose.SWEEP, groundSweep).set(RigPose.FAN, groundFan));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.9f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.FAN, 1.2f).set(RigPose.TWIST, 0.1f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.9f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 0.9f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.28f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.06f).set(RigPose.SWEEP, -0.04f)
                .set(RigPose.FAN, 0.9f).set(RigPose.BEND_TIPS, 0.15f).set(RigPose.ASYM, m.bank * 0.3f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(dd, 0.6f, 0.1f))
                .set(RigPose.STROKE, 0.04f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.38f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.25f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));

        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.add(RigPose.STROKE, m.stall * 0.6f * Mth.sin(time * 1.3f));
            drive.add(RigPose.TWIST, m.stall * 0.5f * Mth.sin(time * 1f));
            drive.add(RigPose.ASYM, m.stall * 0.4f * Mth.sin(time * 0.7f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float sn = Mth.sin(ph);
            float cs = Mth.cos(ph);
            float back = Math.max(0f, -sn);
            drive.add(RigPose.STROKE, hw * 0.75f * cs);
            drive.add(RigPose.SWEEP, hw * 0.2f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.5f * sn);
            drive.add(RigPose.ELBOW, hw * 0.35f * back);
            drive.add(RigPose.WRIST, hw * 0.5f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.5f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.5f * (Mth.cos(ph - 0.5f) - cs));
            drive.add(RigPose.STROKE, hw * 0.06f * Mth.sin(time * 4.3f));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t));
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.FAN, a * FAN.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.15f * STROKE.lagged(t, 0.03f));
            drive.add(RigPose.BEND_WRIST, a * 0.25f * STROKE.lagged(t, 0.05f));
            drive.add(RigPose.BEND_TIPS, a * 0.4f * STROKE.lagged(t, 0.08f));
        }
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.12f * Mth.sin(time * 1.8f));
            drive.add(RigPose.FAN, settle * 0.25f * Mth.sin(time * 2.2f));
        }
        drive.add(RigPose.FOLD, ground * (1f - idle) * -0.02f * (1f + Mth.sin(time * 0.09f)));
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.025f * Mth.sin(time * 0.09f + 0.5f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.03f * Mth.sin(time * 0.5f));
        drive.add(RigPose.BEND_TIPS, glide * 0.06f * Mth.sin(time * 0.8f));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));

        float gutter = 1f - 0.35f * settle * (0.5f + 0.5f * Mth.cos(time * 0.9f));
        float size = heat * scaleIdle * gutter;
        float fold = drive.get(RigPose.FOLD);
        float back = 0.35f * stream * flying;
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.1f * k);
        for (Side side : new Side[]{right, left}) {
            side.wing.apply(drive, tips);
            side.wing.stack(fold, 0.55f, 0.5f, 0.4f);
            float s = side.s;
            for (int k = 0; k < side.wing.primary.size(); k++) {
                float n = flicker * (0.07f * Mth.sin(time * 1.9f + k * 1.3f) + 0.05f * Mth.sin(time * 3.7f + k * 2.1f));
                float len = size * (1f + flicker * (0.09f * Mth.sin(time * 2.3f + k * 0.9f) + 0.06f * Mth.sin(time * 5.1f + k))) + 0.25f * stream * flying;
                side.wing.primary.get(k).rotate(0f, 0f, s * (n - back * (0.4f + 0.1f * k))).scale(Math.max(0.2f, len), 1f + 0.08f * n, 1f);
            }
            flame(side.tertial, s, time, size, flicker, back, 3.1f);
            flame(side.secondary, s, time, size, flicker, back, 1.7f);
            for (int k = 0; k < EMBERS; k++) {
                float e = Cycle.wrap(time * (0.035f + 0.006f * k) + k * 0.27f);
                float life = Mth.sin((float) Math.PI * e);
                float rise = 7f * e * (1f - flying) + scatter * 4f;
                float trail = 9f * e * flying;
                float drift = 1.2f * Mth.sin(time * 0.4f + k * 2f) + scatter * 3f * (k % 2 == 0 ? 1f : -1f);
                Bone ember = side.ember[k];
                ember.move(s * drift, -rise + trail, trail * 0.2f);
                float sz = life * (0.6f + 0.4f * heat) * (1f - 0.6f * fold * (1f - ground));
                ember.scale(sz, sz, sz);
                ember.rotate(time * 0.2f + k, time * 0.13f, 0f);
            }
        }
    }

    private static void flame(Bone[] bones, float s, float time, float size, float flicker, float back, float seed) {
        for (int k = 0; k < bones.length; k++) {
            float n = flicker * (0.08f * Mth.sin(time * 2.1f + k * 1.7f + seed) + 0.05f * Mth.sin(time * 4.3f + k + seed));
            float len = size * (1f + flicker * (0.1f * Mth.sin(time * 2.6f + k * 1.1f + seed) + 0.05f * Mth.sin(time * 6.1f + k)));
            bones[k].rotate(0f, 0f, s * (n - back * 0.6f)).scale(1f + 0.06f * n, Math.max(0.2f, len), 1f);
        }
    }

    private void add(float weight, RigPose pose) {
        if (weight == 0f) {
            return;
        }
        for (int i = 0; i < RigPose.SIZE; i++) {
            drive.v[i] += weight * pose.v[i];
        }
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
