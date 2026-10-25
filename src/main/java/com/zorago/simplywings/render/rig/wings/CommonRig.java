package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class CommonRig extends WingRig {
    private static final int ROUSE = 0;
    private static final float BRAKE_BEAT = Mth.TWO_PI * (4f / 3f) / 20f;
    private static final float[] KEYS = Cycle.beat(0.45f, 0.25f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.75f, 0f, 1f, 1.05f, -0.3f, -0.9f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.25f, -0.15f, -0.25f, -0.1f, 0.25f, 0.35f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.55f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.7f, 0.35f, -0.1f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.1f, 0.25f, 0.2f, 0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;

    public CommonRig() {
        super("basic");
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.16f : 0.1f, rousing ? 0.68f : 0.72f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        java.util.Arrays.fill(drive.v, 0f);
        float rouse = memory.get(ROUSE, m.partialTick);
        add(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(rouse, 1f, 0.72f))
                .set(RigPose.STROKE, Mth.lerp(rouse, 0.12f, 0.3f)).set(RigPose.FAN, 0.3f * rouse));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.85f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.FAN, 1.25f).set(RigPose.TWIST, 0.1f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 0.9f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.BEND_TIPS, 0.25f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.15f, 0.55f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.1f, -0.45f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.15f))
                .set(RigPose.STROKE, 0.05f).set(RigPose.BEND_TIPS, 0.1f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.1f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.4f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.2f));
        }

        float time = m.time;
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float a = 0.8f;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float forward = Math.max(0f, s);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * a * 0.85f * c);
            drive.add(RigPose.SWEEP, hw * 0.18f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.5f * s);
            drive.add(RigPose.ELBOW, hw * 0.35f * back);
            drive.add(RigPose.WRIST, hw * 0.5f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.55f * back);
            drive.add(RigPose.FAN, hw * (0.2f * forward - 0.2f * back));
            drive.add(RigPose.BEND_WRIST, hw * a * 0.4f * (Mth.cos(ph - 0.3f) - c));
            drive.add(RigPose.BEND_TIPS, hw * a * 0.6f * (Mth.cos(ph - 0.5f) - c));
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
            drive.add(RigPose.BEND_ELBOW, a * 0.2f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.35f * STROKE.lagged(t, 0.1f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.55f * Mth.sin(time * 1.1f));
            drive.add(RigPose.ELBOW, sw * 0.45f * (0.5f + 0.5f * Mth.sin(time * 1.7f)));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 0.9f));
            drive.add(RigPose.ASYM, sw * 0.35f * Mth.sin(time * 0.6f));
            drive.add(RigPose.FAN, sw * 0.1f);
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.025f * (1f + Mth.sin(time * 0.08f)));
        drive.add(RigPose.STROKE, ground * 0.02f * Mth.sin(time * 0.08f + 0.5f));
        if (m.rouse > 0f) {
            float shake = Mth.sin(time * 2.2f) * 0.06f * m.rouse;
            drive.add(RigPose.SWEEP, shake);
            drive.add(RigPose.BEND_TIPS, shake * 1.5f);
            drive.add(RigPose.FAN, 0.2f * m.rouse);
        }
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.05f * (Mth.sin(time * 0.37f) + Mth.sin(time * 0.23f)));
        drive.add(RigPose.STROKE, glide * 0.02f * Mth.sin(time * 0.31f));
        float brake = m.w(WingMotion.BRAKE);
        float push = time * BRAKE_BEAT;
        float down = Mth.sin(push);
        float recover = Math.max(0f, -down);
        drive.add(RigPose.STROKE, brake * 0.35f * Mth.cos(push));
        drive.add(RigPose.SWEEP, brake * -0.12f * down);
        drive.add(RigPose.TWIST, brake * -0.2f * Math.max(0f, down));
        drive.add(RigPose.ELBOW, brake * 0.3f * recover);
        drive.add(RigPose.WRIST, brake * 0.4f * recover);
        drive.add(RigPose.BEND_TIPS, brake * (0.25f * Mth.sin(push - 0.6f) + 0.05f * Mth.sin(time * 2.6f)));

        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Math.max(0f, drive.get(RigPose.FAN)));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.1f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
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
