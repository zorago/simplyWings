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

public final class OwlRig extends WingRig {
    private static final int STRETCH = 0;
    private static final int SIDE = 1;
    private static final int WAS_ROUSING = 2;
    private static final int ALULA = 3;
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.9f, -0.1f, 0.95f, 1f, -0.3f, -1.05f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.1f, -0.15f, 0f, 0.25f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.05f, 0.4f, 0.2f, 0f, 0f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.05f, 0.5f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.25f, 0.1f, 0f, -0.2f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.15f, 0f, 0.1f, 0.2f, 0.2f, 0.15f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.35f, -0.15f, 0f, 0.1f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose side = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone rightAlula;
    private Bone leftAlula;

    public OwlRig() {
        super("owl");
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
        if (rousing && memory.now(WAS_ROUSING) == 0f) {
            memory.snap(SIDE, memory.now(SIDE) > 0.5f ? 0f : 1f);
        }
        memory.set(WAS_ROUSING, rousing ? 1f : 0f);
        memory.spring(STRETCH, rousing ? 1f : 0f, rousing ? 0.08f : 0.07f, rousing ? 0.78f : 0.8f);
        float slow = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING) + m.stall;
        memory.spring(ALULA, Math.min(1f, slow), 0.2f, 0.65f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f).set(RigPose.STROKE, 0.14f));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.95f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.FAN, 1.2f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.7f)
                .set(RigPose.SWEEP, 0.45f).set(RigPose.TWIST, -0.4f).set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, -0.2f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.2f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 1.1f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, -0.03f).set(RigPose.SWEEP, 0.06f)
                .set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.08f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.15f, 0.5f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.1f, -0.4f)).set(RigPose.FAN, Mth.lerp(d, 0.8f, 0.25f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.55f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.8f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, -0.2f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.8f).set(RigPose.SWEEP, -0.5f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.1f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.45f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.2f));
        }

        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * 0.65f * c);
            drive.add(RigPose.SWEEP, hw * 0.15f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.45f * s);
            drive.add(RigPose.ELBOW, hw * 0.25f * back);
            drive.add(RigPose.WRIST, hw * 0.35f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.4f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.6f * (Mth.cos(ph - 0.6f) - c));
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
            drive.add(RigPose.BEND_ELBOW, a * 0.25f * STROKE.lagged(t, 0.05f));
            drive.add(RigPose.BEND_WRIST, a * 0.35f * STROKE.lagged(t, 0.09f));
            drive.add(RigPose.BEND_TIPS, a * 0.5f * STROKE.lagged(t, 0.13f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.5f * Mth.sin(time * 0.9f));
            drive.add(RigPose.ELBOW, sw * 0.35f * (0.5f + 0.5f * Mth.sin(time * 1.4f)));
            drive.add(RigPose.TWIST, sw * 0.45f * Mth.sin(time * 0.7f));
            drive.add(RigPose.ASYM, sw * 0.3f * Mth.sin(time * 0.5f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.025f * (1f + Mth.sin(time * 0.07f)));
        drive.add(RigPose.STROKE, ground * 0.02f * Mth.sin(time * 0.07f + 0.5f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.04f * (Mth.sin(time * 0.29f) + Mth.sin(time * 0.17f)));
        drive.add(RigPose.STROKE, glide * 0.015f * Mth.sin(time * 0.23f));
        float brake = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING);
        drive.add(RigPose.BEND_TIPS, brake * 0.08f * Mth.sin(time * 2.1f));

        float stretch = memory.get(STRETCH, m.partialTick) * ground;
        boolean rightSide = memory.now(SIDE) > 0.5f;
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.5f, 1.1f - 0.07f * k);
        finish(right, rightSide ? stretch : 0f, tips);
        finish(left, rightSide ? 0f : stretch, tips);
        float alula = memory.get(ALULA, m.partialTick) * (1f - drive.get(RigPose.FOLD));
        rightAlula.rotate(0f, 0.3f * alula, 0.5f * alula);
        leftAlula.rotate(0f, -0.3f * alula, -0.5f * alula);
    }

    private void finish(FeatherWing wing, float stretch, FeatherWing.TipCurve tips) {
        side.copy(drive);
        side.add(RigPose.FOLD, -0.8f * stretch);
        side.add(RigPose.STROKE, -0.75f * stretch);
        side.add(RigPose.SWEEP, -0.35f * stretch);
        side.add(RigPose.FAN, 1.15f * stretch);
        side.add(RigPose.TWIST, 0.2f * stretch);
        float fold = Mth.clamp(side.get(RigPose.FOLD), 0f, 1f);
        side.set(RigPose.FOLD, fold);
        side.set(RigPose.FAN, Mth.clamp(side.get(RigPose.FAN), 0f, 1.25f));
        wing.apply(side, tips);
        wing.stack(fold, 0.7f, 0.55f, 0.45f);
    }
}
