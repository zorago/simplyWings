package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class EarthRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int CLACK = 2;
    private static final int[] IDLES = {36, 70, 56};
    private static final int TERTIALS = 2;
    private static final int SECONDARIES = 3;
    private static final int CRYSTALS = 5;
    private static final float UP = 0.6f;
    private static final float DOWN = 0.2f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.6f, 0.05f, 0.85f, 0.9f, -0.35f, -0.8f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.15f, -0.1f, -0.15f, -0.05f, 0.2f, 0.25f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.08f, 0.35f, 0.18f, 0f, 0f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.08f, 0.45f, 0.22f, -0.05f, -0.05f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.2f, 0.08f, -0.04f, -0.18f, -0.06f);
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/earth_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private Side right;
    private Side left;

    public EarthRig() {
        super("earth");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
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
        final Bone[] crystal = new Bone[CRYSTALS];

        Side(Bones b, String p, float s) {
            this.s = s;
            wing = new FeatherWing(b, p, s, 1.2f, 2.7f, 2.8f);
            for (int k = 0; k < TERTIALS; k++) {
                tertial[k] = b.get(p + "tertial_" + (k + 1));
            }
            for (int k = 0; k < SECONDARIES; k++) {
                secondary[k] = b.get(p + "secondary_" + (k + 1));
            }
            for (int k = 0; k < CRYSTALS; k++) {
                crystal[k] = b.get(p + "crystal_" + (k + 1));
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
        memory.spring(IDLE, m.rouse, 0.16f, 0.72f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.45f, 0.45f);
        boolean strike = m.beating() && m.beat >= UP + DOWN && m.beat < UP + DOWN + 0.1f;
        memory.spring(CLACK, strike ? m.beatWeight : 0f, strike ? 0.6f : 0.25f, 0.5f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        java.util.Arrays.fill(drive.v, 0f);
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float clack = memory.get(CLACK, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float groundFold = 1f;
        float groundStroke = 0.1f;
        float groundSweep = 0f;
        float groundFan = 0f;
        float tremor = 0f;
        float glint = 0f;
        float grind = 0f;
        if (idle > 0.001f) {
            switch (variant) {
                case 0 -> {
                    tremor = idle * Mth.sin((float) Math.PI * p);
                    groundStroke += 0.05f * tremor;
                }
                case 1 -> glint = idle * Mth.sin((float) Math.PI * p);
                default -> {
                    float open = idle * smooth(Mth.clamp(p * 2.5f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 2.5f, 0f, 1f));
                    groundFold = Mth.lerp(open, 1f, 0.55f);
                    groundStroke = Mth.lerp(open, 0.1f, 0.3f);
                    groundFan = 0.7f * open;
                    grind = open;
                }
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, groundFold).set(RigPose.STROKE, groundStroke)
                .set(RigPose.SWEEP, groundSweep).set(RigPose.FAN, groundFan));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.7f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 1f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.45f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 0.7f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.55f).set(RigPose.STROKE, 0.05f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 0.6f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.2f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 0.95f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.02f)
                .set(RigPose.SWEEP, -0.02f).set(RigPose.FAN, 0.85f).set(RigPose.ASYM, m.bank * 0.2f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.3f, 0.75f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.45f)).set(RigPose.FAN, Mth.lerp(dd, 0.5f, 0.05f)));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.45f).set(RigPose.TWIST, -0.6f).set(RigPose.FAN, 1.15f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.9f).set(RigPose.SWEEP, -0.5f)
                .set(RigPose.STROKE, 0.05f).set(RigPose.FAN, 0f));

        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.2f));
            drive.add(RigPose.STROKE, m.stall * 0.35f * Mth.sin(time * 0.9f));
            drive.add(RigPose.ASYM, m.stall * 0.25f * Mth.sin(time * 0.6f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float sn = Mth.sin(ph);
            float cs = Mth.cos(ph);
            float back = Math.max(0f, -sn);
            drive.add(RigPose.STROKE, hw * 0.55f * cs);
            drive.add(RigPose.SWEEP, hw * 0.12f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.35f * sn);
            drive.add(RigPose.ELBOW, hw * 0.25f * back);
            drive.add(RigPose.WRIST, hw * 0.35f * back);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t));
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.08f * STROKE.lagged(t, 0.06f));
            drive.add(RigPose.BEND_WRIST, a * 0.14f * STROKE.lagged(t, 0.1f));
            drive.add(RigPose.BEND_TIPS, a * 0.18f * STROKE.lagged(t, 0.14f));
        }
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * -0.2f * (0.6f + 0.4f * Mth.cos(time * 2.4f)));
            drive.add(RigPose.FAN, settle * 0.1f * Mth.sin(time * 3f));
        }
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.012f * Mth.sin(time * 0.05f));
        drive.add(RigPose.SWEEP, tremor * 0.035f * Mth.sin(time * 4.7f));
        drive.add(RigPose.STROKE, tremor * 0.03f * Mth.sin(time * 5.3f + 1f));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.2f));

        float fold = drive.get(RigPose.FOLD);
        float rattle = clack * 0.07f + settle * 0.05f + tremor * 0.04f + grind * 0.03f;
        FeatherWing.TipCurve tips = (k, n) -> 0.6f;
        for (Side side : new Side[]{right, left}) {
            side.wing.apply(drive, tips);
            side.wing.stack(fold, 0.5f, 0.45f, 0.35f);
            float s = side.s;
            for (int k = 0; k < side.wing.primary.size(); k++) {
                float j = rattle * Mth.sin(time * 6.1f + k * 2.3f + (s > 0 ? 0f : 1.3f));
                side.wing.primary.get(k).rotate(0f, s * j * 0.6f, s * j);
            }
            shake(side.secondary, s, time, rattle, 0.7f);
            shake(side.tertial, s, time, rattle, 1.9f);
            for (int k = 0; k < CRYSTALS; k++) {
                float pulse = 1f + glint * (0.25f + 0.1f * Mth.sin(time * 0.8f + k * 1.4f));
                side.crystal[k].scale(pulse, pulse * (1f + 0.15f * glint), pulse)
                        .rotate(0f, glint * 0.2f * Mth.sin(time * 0.3f + k), 0f);
            }
        }
    }

    private static void shake(Bone[] bones, float s, float time, float rattle, float seed) {
        for (int k = 0; k < bones.length; k++) {
            float j = rattle * Mth.sin(time * 5.7f + k * 1.9f + seed);
            bones[k].rotate(j * 0.5f, 0f, s * j);
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
