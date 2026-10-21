package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class GullRig extends WingRig {
    private static final int ROUSE = 0;
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.6f, 0.05f, 0.75f, 0.8f, -0.2f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.1f, -0.1f, -0.15f, 0f, 0.15f, 0.2f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0f, 0.35f, 0.15f, -0.05f, -0.1f, -0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.15f, 0.55f, 0.3f, 0.1f, 0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.2f, 0.05f, 0f, -0.15f, -0.05f);
    private static final Cycle FAN = Cycle.of(KEYS, 0f, -0.15f, 0f, 0.05f, 0.05f, 0f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.35f, -0.1f, 0f, 0.05f, 0f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;

    public GullRig() {
        super("gull");
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.12f : 0.09f, rousing ? 0.72f : 0.75f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float rouse = memory.get(ROUSE, m.partialTick);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(rouse, 1f, 0.05f))
                .set(RigPose.STROKE, Mth.lerp(rouse, 0.12f, 0.85f)).set(RigPose.SWEEP, 0.2f * rouse)
                .set(RigPose.WRIST, 0.35f * rouse).set(RigPose.FAN, rouse));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.8f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.WRIST, 0.2f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.12f).set(RigPose.STROKE, 0.55f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.ELBOW, -0.15f).set(RigPose.WRIST, 0.3f).set(RigPose.TWIST, -0.3f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.ELBOW, -0.2f).set(RigPose.WRIST, 0.4f).set(RigPose.BEND_WRIST, -0.35f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.22f).set(RigPose.ELBOW, -0.25f)
                .set(RigPose.WRIST, 0.45f).set(RigPose.BEND_WRIST, -0.6f).set(RigPose.BEND_TIPS, -0.1f)
                .set(RigPose.ASYM, m.bank * 0.4f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.55f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.2f, -0.5f)).set(RigPose.WRIST, Mth.lerp(d, 0.4f, 0.2f))
                .set(RigPose.FAN, Mth.lerp(d, 0.8f, 0.3f)).set(RigPose.STROKE, 0.08f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.5f).set(RigPose.TWIST, -0.7f).set(RigPose.FAN, 1.1f).set(RigPose.BEND_TIPS, -0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.45f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.1f));
        }

        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            drive.add(RigPose.STROKE, hw * 0.25f * c);
            drive.add(RigPose.TWIST, hw * 0.2f * s);
            drive.add(RigPose.WRIST, hw * 0.25f * Math.max(0f, -s));
            drive.add(RigPose.BEND_TIPS, hw * 0.35f * (Mth.cos(ph - 0.5f) - c));
            drive.add(RigPose.ASYM, hw * 0.06f * Mth.sin(time * 0.21f));
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
            drive.add(RigPose.HAND_TWIST, a * HAND_TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.15f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.25f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.3f * STROKE.lagged(t, 0.1f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.5f * Mth.sin(time * 1.1f));
            drive.add(RigPose.WRIST, sw * 0.4f * (0.5f + 0.5f * Mth.sin(time * 1.6f)));
            drive.add(RigPose.TWIST, sw * 0.45f * Mth.sin(time * 0.8f));
            drive.add(RigPose.ASYM, sw * 0.35f * Mth.sin(time * 0.55f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.02f * (1f + Mth.sin(time * 0.08f)));
        drive.add(RigPose.STROKE, ground * 0.02f * Mth.sin(time * 0.08f + 0.5f));
        float glide = m.w(WingMotion.CRUISE);
        float gust = Mth.sin(time * 0.19f) + 0.6f * Mth.sin(time * 0.43f + 1f);
        drive.add(RigPose.WRIST, glide * 0.08f * gust);
        drive.add(RigPose.BEND_WRIST, glide * -0.06f * gust);
        drive.add(RigPose.STROKE, glide * 0.03f * Mth.sin(time * 0.27f));
        drive.add(RigPose.BEND_TIPS, glide * 0.04f * Mth.sin(time * 0.61f));
        float brake = m.w(WingMotion.BRAKE);
        drive.add(RigPose.BEND_TIPS, brake * 0.1f * Mth.sin(time * 2.4f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.1f));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1f - 0.08f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.7f, 0.55f, 0.45f);
        left.stack(fold, 0.7f, 0.55f, 0.45f);
    }
}
