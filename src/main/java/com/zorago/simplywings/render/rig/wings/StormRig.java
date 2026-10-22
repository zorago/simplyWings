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

public final class StormRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int CHARGE = 2;
    private static final int[] IDLES = {30, 48, 40};
    private static final int ARCS = 6;
    private static final float UP = 0.4f;
    private static final float DOWN = 0.12f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.75f, 0.1f, 1.05f, 1.1f, -0.4f, -0.95f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.22f, -0.15f, -0.25f, -0.08f, 0.3f, 0.35f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.5f, 0.22f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.6f, 0.3f, -0.1f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.12f, -0.06f, -0.28f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.05f, -0.2f, 0.1f, 0.3f, 0.2f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private Side right;
    private Side left;

    public StormRig() {
        super("storm");
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
        final Bone[] arc = new Bone[ARCS];

        Side(Bones b, String p, float s) {
            this.s = s;
            wing = new FeatherWing(b, p, s, 1.25f, 2.75f, 2.85f);
            for (int k = 0; k < ARCS; k++) {
                arc[k] = b.get(p + "arc_" + (k + 1));
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
        memory.spring(IDLE, m.rouse, 0.35f, 0.55f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.35f, 0.55f);
        boolean strike = m.beating() && m.beat >= UP && m.beat < UP + DOWN + 0.08f;
        memory.spring(CHARGE, strike ? m.beatWeight : 0f, strike ? 0.7f : 0.3f, 0.45f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        java.util.Arrays.fill(drive.v, 0f);
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float discharge = Mth.clamp(memory.get(CHARGE, pt), 0f, 1.2f);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float groundFold = 1f;
        float groundStroke = 0.12f;
        float groundFan = 0f;
        float spark = 0f;
        float twitch = 0f;
        if (idle > 0.001f) {
            switch (variant) {
                case 0 -> {
                    spark = idle;
                    twitch = idle * 0.6f;
                }
                case 1 -> {
                    float flash = idle * Mth.sin((float) Math.PI * Mth.clamp(p * 1.3f, 0f, 1f));
                    groundFold = Mth.lerp(flash, 1f, 0.35f);
                    groundStroke = Mth.lerp(flash, 0.12f, 0.55f);
                    groundFan = flash * 1.2f;
                    spark = flash;
                }
                default -> {
                    twitch = idle;
                    float jolt = idle * Math.max(0f, Mth.sin(time * 0.9f));
                    groundStroke += 0.12f * jolt;
                    groundFold -= 0.15f * jolt;
                    spark = 0.5f * idle;
                }
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, groundFold).set(RigPose.STROKE, groundStroke).set(RigPose.FAN, groundFan));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.08f).set(RigPose.STROKE, 0.9f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.FAN, 1.25f).set(RigPose.TWIST, 0.1f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 0.9f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.05f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.06f).set(RigPose.SWEEP, -0.08f)
                .set(RigPose.FAN, 0.8f).set(RigPose.BEND_TIPS, 0.1f).set(RigPose.ASYM, m.bank * 0.4f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.62f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.2f, -0.55f)).set(RigPose.FAN, Mth.lerp(dd, 0.6f, 0.1f))
                .set(RigPose.STROKE, 0.04f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.38f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.25f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));

        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.1f));
            drive.add(RigPose.STROKE, m.stall * 0.6f * Mth.sin(time * 1.6f));
            drive.add(RigPose.TWIST, m.stall * 0.5f * Mth.sin(time * 1.3f));
            drive.add(RigPose.ASYM, m.stall * 0.4f * Mth.sin(time * 0.8f));
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
            twitch = Math.max(twitch, 0.5f * hw);
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
            drive.add(RigPose.STROKE, settle * 0.1f * Mth.sin(time * 2.4f));
            spark = Math.max(spark, settle);
        }
        float buzz = 0.012f * (1f + 2.5f * twitch) * (Mth.sin(time * 7.3f) + Mth.sin(time * 11.1f + 1f));
        drive.add(RigPose.STROKE, buzz);
        drive.add(RigPose.BEND_TIPS, buzz * 2f);
        drive.add(RigPose.FOLD, ground * (1f - idle) * -0.02f * (1f + Mth.sin(time * 0.1f)));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));

        float fold = drive.get(RigPose.FOLD);
        int frame = (int) (time * 0.5f);
        float energy = 0.18f + 0.25f * (1f - ground) + 0.6f * discharge + 0.5f * spark + 0.3f * m.w(WingMotion.BRAKE);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.12f * k);
        for (Side side : new Side[]{right, left}) {
            side.wing.apply(drive, tips);
            side.wing.stack(fold, 0.55f, 0.5f, 0.45f);
            float s = side.s;
            for (int k = 0; k < side.wing.primary.size(); k++) {
                float j = (0.03f + 0.05f * twitch + 0.06f * discharge) * Mth.sin(time * 9.7f + k * 2.9f);
                side.wing.primary.get(k).rotate(0f, s * j, s * j * 0.7f).scale(1f + 0.12f * discharge, 1f, 1f);
            }
            for (int k = 0; k < ARCS; k++) {
                float roll = flick(frame, k, s > 0 ? 0 : 7);
                boolean on = roll < energy * (1f - 0.7f * fold);
                Bone arc = side.arc[k];
                arc.visible(on);
                if (on) {
                    float jump = flick(frame, k, 13) - 0.5f;
                    arc.rotate(0f, 0f, s * jump * 1.4f).scale(0.7f + 0.6f * flick(frame, k, 21), 1f, 1f);
                }
            }
        }
    }

    private static float flick(int frame, int k, int seed) {
        int h = frame * 73856093 ^ (k + 1) * 19349663 ^ seed * 83492791;
        h ^= h >>> 13;
        h *= 0x5bd1e995;
        h ^= h >>> 15;
        return (h & 0xFFFF) / 65535f;
    }

    private void add(float weight, RigPose pose) {
        if (weight == 0f) {
            return;
        }
        for (int i = 0; i < RigPose.SIZE; i++) {
            drive.v[i] += weight * pose.v[i];
        }
    }
}
