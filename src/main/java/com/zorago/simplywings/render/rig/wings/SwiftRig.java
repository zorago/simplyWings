package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class SwiftRig extends WingRig {
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.5f, 0.05f, 0.6f, 0.62f, -0.15f, -0.6f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.05f, -0.2f, -0.25f, -0.15f, 0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.05f, 0.2f, 0.1f, 0f, 0f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.2f, 0.05f, 0f, -0.15f, -0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.25f, -0.1f, 0f, 0.05f, 0f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;

    public SwiftRig() {
        super("swift");
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 3.55f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 3.55f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f).set(RigPose.STROKE, 0.1f));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.7f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, -0.1f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.TWIST, -0.3f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.2f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.FOLD, 0.08f).set(RigPose.STROKE, 0.06f)
                .set(RigPose.SWEEP, -0.35f).set(RigPose.WRIST, 0.15f).set(RigPose.FAN, 0.85f)
                .set(RigPose.ASYM, m.bank * 0.45f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.45f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.5f, -0.75f)).set(RigPose.FAN, Mth.lerp(d, 0.6f, 0.2f))
                .set(RigPose.STROKE, 0.12f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.4f).set(RigPose.TWIST, -0.65f).set(RigPose.FAN, 1.05f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.7f).set(RigPose.SWEEP, -0.75f)
                .set(RigPose.STROKE, 0.1f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.1f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.3f));
        }

        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            drive.add(RigPose.STROKE, hw * 0.5f * c);
            drive.add(RigPose.TWIST, hw * 0.45f * s);
            drive.add(RigPose.SWEEP, hw * 0.12f * Mth.sin(2f * ph));
            drive.add(RigPose.ASYM, hw * 0.12f * (Mth.sin(time * 0.71f) + Mth.sin(time * 1.37f)));
            drive.add(RigPose.WRIST, hw * 0.15f * Math.max(0f, -s));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.HAND_TWIST, a * HAND_TWIST.at(t));
            drive.add(RigPose.BEND_WRIST, a * 0.15f * STROKE.lagged(t, 0.05f));
            drive.add(RigPose.BEND_TIPS, a * 0.22f * STROKE.lagged(t, 0.08f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.45f * Mth.sin(time * 1.6f));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 1.2f));
            drive.add(RigPose.ASYM, sw * 0.45f * Mth.sin(time * 0.9f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.STROKE, ground * 0.015f * Mth.sin(time * 0.12f));
        float glide = m.w(WingMotion.CRUISE);
        float burst = Mth.sin(time * 0.083f) + 0.5f * Mth.sin(time * 0.031f);
        float flick = Math.max(0f, burst - 0.9f) * 1.6f;
        drive.add(RigPose.STROKE, glide * flick * 0.3f * Mth.sin(time * 2.6f));
        drive.add(RigPose.SWEEP, glide * flick * 0.15f);
        drive.add(RigPose.BEND_TIPS, glide * 0.04f * Mth.sin(time * 0.47f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.1f));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1f - 0.08f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.6f, 0.5f, 0.4f);
        left.stack(fold, 0.6f, 0.5f, 0.4f);
        right.shoulder.move(0f, 0f, 0.35f * fold);
    }
}
