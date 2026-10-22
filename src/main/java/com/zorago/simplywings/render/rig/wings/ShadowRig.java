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

public final class ShadowRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int[] IDLES = {60, 50, 46};
    private static final int TERTIALS = 2;
    private static final int SECONDARIES = 3;
    private static final int EYES = 5;
    private static final float UP = 0.45f;
    private static final float DOWN = 0.14f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.7f, 0.1f, 1.05f, 1.1f, -0.35f, -0.9f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.15f, -0.25f, -0.08f, 0.28f, 0.32f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.55f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.65f, 0.3f, -0.1f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/shadow_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private Side right;
    private Side left;

    public ShadowRig() {
        super("shadow");
    }

    @Override
    public Material material() {
        return Material.TRANSLUCENT;
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
        final Bone[] eye = new Bone[EYES];

        Side(Bones b, String p, float s) {
            this.s = s;
            wing = new FeatherWing(b, p, s, 1.25f, 2.75f, 2.85f);
            for (int k = 0; k < TERTIALS; k++) {
                tertial[k] = b.get(p + "tertial_" + (k + 1));
            }
            for (int k = 0; k < SECONDARIES; k++) {
                secondary[k] = b.get(p + "secondary_" + (k + 1));
            }
            for (int k = 0; k < EYES; k++) {
                eye[k] = b.get(p + "eye_" + (k + 1));
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
        memory.spring(IDLE, m.rouse, 0.15f, 0.75f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.25f, 0.65f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        java.util.Arrays.fill(drive.v, 0f);
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float stare = 0f;
        float dissolve = 0f;
        float coil = 0f;
        if (idle > 0.001f) {
            float env = idle * Mth.sin((float) Math.PI * p);
            switch (variant) {
                case 0 -> stare = env;
                case 1 -> dissolve = env;
                default -> coil = env;
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, 1f - 0.4f * dissolve).set(RigPose.STROKE, 0.12f + 0.2f * dissolve)
                .set(RigPose.FAN, 0.8f * dissolve));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.85f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.2f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.2f).set(RigPose.FAN, 1f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.07f).set(RigPose.SWEEP, -0.05f)
                .set(RigPose.FAN, 0.85f).set(RigPose.BEND_TIPS, 0.12f).set(RigPose.ASYM, m.bank * 0.4f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(dd, 0.6f, 0.1f)));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.38f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.25f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));

        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.add(RigPose.STROKE, m.stall * 0.55f * Mth.sin(time * 1.1f));
            drive.add(RigPose.TWIST, m.stall * 0.45f * Mth.sin(time * 0.9f));
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
            drive.add(RigPose.BEND_TIPS, hw * 0.5f * (Mth.cos(ph - 0.5f) - cs));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t));
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.2f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.45f * STROKE.lagged(t, 0.11f));
        }
        if (settle > 0.001f) {
            drive.add(RigPose.FAN, settle * 0.3f);
            drive.add(RigPose.STROKE, settle * 0.08f * Mth.sin(time * 1.3f));
        }
        drive.add(RigPose.FOLD, ground * (1f - idle) * -0.025f * (1f + Mth.sin(time * 0.07f)));
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.02f * Mth.sin(time * 0.07f + 0.5f));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));

        float fold = drive.get(RigPose.FOLD);
        float writhe = 0.08f + 0.06f * (1f - ground) + 0.18f * coil + 0.12f * dissolve + 0.1f * settle;
        float spread = 1f + 0.35f * dissolve + 0.15f * settle;
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.1f * k);
        for (Side side : new Side[]{right, left}) {
            side.wing.apply(drive, tips);
            side.wing.stack(fold, 0.55f, 0.5f, 0.4f);
            float s = side.s;
            for (int k = 0; k < side.wing.primary.size(); k++) {
                float w = writhe * Mth.sin(time * 0.55f - k * 0.7f + (s > 0 ? 0f : 1.7f));
                float c = coil * 0.6f * (0.5f + 0.1f * k);
                side.wing.primary.get(k).rotate(0f, s * w * 0.6f, s * (w - c)).scale(spread * (1f + 0.06f * Mth.sin(time * 0.4f + k)), 1f, 1f);
            }
            writhe(side.secondary, s, time, writhe, coil, spread, 0.4f);
            writhe(side.tertial, s, time, writhe, coil, spread, 2.2f);
            for (int k = 0; k < EYES; k++) {
                float blink = blink(time, k, s);
                float open = Mth.lerp(stare, blink, 1.3f) * (1f - 0.8f * fold * (1f - ground)) * (1f - 0.7f * dissolve);
                float lookX = 0.5f * Mth.sin(time * 0.05f + k * 2.1f) + stare * 0.6f * Mth.sin(time * 0.17f + k);
                float lookY = 0.3f * Mth.sin(time * 0.037f + k * 1.3f);
                side.eye[k].move(s * lookX, lookY, 0f).scale(1f, Math.max(0.05f, open), 1f);
            }
        }
    }

    private static float blink(float time, int k, float s) {
        float period = 70f + 23f * k + (s > 0 ? 0f : 11f);
        float t = (time + k * 37f) % period;
        if (t > 6f) {
            return 1f;
        }
        return Math.abs(t - 3f) / 3f;
    }

    private static void writhe(Bone[] bones, float s, float time, float writhe, float coil, float spread, float seed) {
        for (int k = 0; k < bones.length; k++) {
            float w = writhe * 1.3f * Mth.sin(time * 0.6f - k * 0.9f + seed);
            bones[k].rotate(w * 0.5f, 0f, s * (w + coil * 0.5f)).scale(1f, spread * (1f + 0.07f * Mth.sin(time * 0.45f + k + seed)), 1f);
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
}
