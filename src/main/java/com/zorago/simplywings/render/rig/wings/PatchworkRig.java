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

public final class PatchworkRig extends WingRig {
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.8f, 0.05f, 0.95f, 1f, -0.3f, -0.9f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.12f, -0.18f, -0.05f, 0.22f, 0.28f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.5f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.6f, 0.3f, -0.05f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.05f, 0.15f, 0.15f, 0.05f);
    private static final float LEFT_LAG = 0.035f;

    private final RigPose rightDrive = new RigPose();
    private final RigPose leftDrive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone flap;

    public PatchworkRig() {
        super("patchwork");
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
        flap = bones.get("left_flap");
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        build(m, rightDrive, 0f, 1f);
        build(m, leftDrive, LEFT_LAG, 0.92f);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.09f * k);
        finish(right, rightDrive, tips);
        finish(left, leftDrive, tips);
        float time = m.time;
        float flying = 1f - m.w(WingMotion.GROUND);
        float wind = flying * (0.15f + 0.25f * Mth.clamp(m.speed / 20f, 0f, 1f));
        flap.rotate(wind * Mth.sin(time * 2.3f) + 0.1f * flying, 0f, -0.12f * wind * Mth.sin(time * 1.7f + 0.6f));
    }

    private void build(WingMotion m, RigPose drive, float lag, float amplitude) {
        drive.zero();
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f).set(RigPose.STROKE, 0.12f));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.08f).set(RigPose.STROKE, 0.85f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 0.9f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.BEND_TIPS, 0.15f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.15f, 0.55f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.1f, -0.45f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.15f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.15f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.4f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase - lag * Mth.TWO_PI;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * amplitude * 0.8f * c);
            drive.add(RigPose.TWIST, hw * 0.5f * s);
            drive.add(RigPose.ELBOW, hw * 0.35f * back);
            drive.add(RigPose.WRIST, hw * 0.5f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.5f * (Mth.cos(ph - 0.5f) - c));
        }
        if (m.beating()) {
            float t = m.beat - lag;
            float a = m.beatWeight * amplitude;
            float hiccup = Math.max(0f, Mth.sin(time * 0.071f + lag * 40f) - 0.93f) * 8f;
            drive.add(RigPose.STROKE, a * STROKE.at(t) - m.beatWeight * 0.25f * hiccup);
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t) + m.beatWeight * 0.2f * hiccup);
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.FAN, a * FAN.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.2f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.4f * STROKE.lagged(t, 0.1f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.55f * Mth.sin(time * 1.2f + lag * 20f));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 0.9f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.025f * (1f + Mth.sin(time * 0.09f + lag * 30f)));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.06f * Mth.sin(time * 0.5f + lag * 25f));
    }

    private static void finish(FeatherWing wing, RigPose drive, FeatherWing.TipCurve tips) {
        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.2f));
        wing.apply(drive, tips);
        wing.stack(fold, 0.68f, 0.5f, 0.4f);
    }
}
