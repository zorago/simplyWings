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

public final class CrowRig extends WingRig {
    private static final int ROUSE = 0;
    private static final int ALULA = 1;
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.85f, 0f, 1f, 1.05f, -0.35f, -1f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.15f, -0.2f, -0.05f, 0.25f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.6f, 0.3f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.15f, 0.85f, 0.4f, -0.1f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.15f, -0.35f, 0.05f, 0.25f, 0.25f, 0.15f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.6f, -0.25f, 0f, 0.15f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone rightAlula;
    private Bone leftAlula;

    public CrowRig() {
        super("crow");
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
        rightAlula = bones.get("right_alula");
        leftAlula = bones.get("left_alula");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.22f : 0.14f, rousing ? 0.6f : 0.66f);
        float slow = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING) + m.stall + 0.5f * m.w(WingMotion.CHARGE);
        memory.spring(ALULA, Math.min(1f, slow), 0.25f, 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float rouse = memory.get(ROUSE, m.partialTick);
        float flick = m.rouse > 0f ? Math.max(0f, Mth.sin(m.rouseProgress * Mth.TWO_PI * 2f)) : 0f;
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f - 0.35f * flick * rouse)
                .set(RigPose.STROKE, 0.1f + 0.25f * flick * rouse).set(RigPose.FAN, 0.2f * flick)
                .set(RigPose.SWEEP, 0.1f * flick));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.08f).set(RigPose.STROKE, 0.9f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.2f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.5f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.05f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.02f)
                .set(RigPose.FAN, 1.12f).set(RigPose.BEND_TIPS, 0.3f).set(RigPose.ASYM, m.bank * 0.35f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(d, 0.6f, 0.1f))
                .set(RigPose.STROKE, 0.05f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.22f).set(RigPose.BEND_TIPS, -0.25f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.4f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.2f));
        }

        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * 0.7f * c);
            drive.add(RigPose.SWEEP, hw * 0.2f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.5f * s);
            drive.add(RigPose.ELBOW, hw * 0.4f * back);
            drive.add(RigPose.WRIST, hw * 0.65f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.6f * back);
            drive.add(RigPose.FAN, hw * (0.25f * Math.max(0f, s) - 0.3f * back));
            drive.add(RigPose.BEND_TIPS, hw * 0.5f * (Mth.cos(ph - 0.5f) - c));
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
            drive.add(RigPose.BEND_TIPS, a * 0.4f * STROKE.lagged(t, 0.11f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.55f * Mth.sin(time * 1.2f));
            drive.add(RigPose.ELBOW, sw * 0.45f * (0.5f + 0.5f * Mth.sin(time * 1.8f)));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 0.9f));
            drive.add(RigPose.ASYM, sw * 0.4f * Mth.sin(time * 0.6f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.02f * (1f + Mth.sin(time * 0.1f)));
        drive.add(RigPose.STROKE, ground * 0.02f * Mth.sin(time * 0.1f + 0.5f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.06f * (Mth.sin(time * 0.41f) + Mth.sin(time * 0.27f)));
        drive.add(RigPose.STROKE, glide * 0.02f * Mth.sin(time * 0.33f));
        float brake = m.w(WingMotion.BRAKE);
        drive.add(RigPose.BEND_TIPS, brake * 0.14f * Mth.sin(time * 2.8f));
        drive.add(RigPose.FAN, brake * 0.06f * Mth.sin(time * 3.3f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));
        FeatherWing.TipCurve tips = (k, n) -> k < 6 ? 1.2f - 0.08f * k : 0.5f;
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.68f, 0.5f, 0.4f);
        left.stack(fold, 0.68f, 0.5f, 0.4f);
        float alula = memory.get(ALULA, m.partialTick) * (1f - fold);
        rightAlula.rotate(0f, 0.35f * alula, 0.55f * alula);
        leftAlula.rotate(0f, -0.35f * alula, -0.55f * alula);
    }
}
