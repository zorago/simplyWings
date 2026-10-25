package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class OwlRig extends WingRig {
    private static final int STRETCH = 0;
    private static final int SIDE = 1;
    private static final int WAS_ROUSING = 2;
    private static final int ALULA = 3;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.08f, 0.48f, 0.82f, 0.8f, 0.34f, -0.12f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.3f, 0f, 0.2f, 0.18f, 0.04f, -0.42f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.42f, -0.12f, 0.16f, 0.2f, 0.08f, -0.5f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.22f, 0.04f, 0.26f, 0.25f, 0f, -0.26f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.12f, 0.35f, 0.15f, 0f, 0f, 0.03f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.18f, 0.45f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.25f, 0.1f, 0f, -0.2f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.15f, 0f, 0.1f, 0.2f, 0.2f, 0.15f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.35f, -0.15f, 0f, 0.1f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);

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
    public String[] tipBones() {
        return TIPS;
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
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.7f).set(RigPose.FAN, 1.25f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, -0.2f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.2f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 1.15f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.04f).set(RigPose.SWEEP, 0.06f)
                .set(RigPose.FAN, 1.18f).set(RigPose.BEND_TIPS, 0.08f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.15f, 0.5f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.1f, -0.4f)).set(RigPose.FAN, Mth.lerp(d, 0.8f, 0.25f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.05f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.8f).set(RigPose.SWEEP, -0.5f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);

        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.04f * (Mth.sin(time * 0.29f) + Mth.sin(time * 0.17f)));
        drive.add(RigPose.STROKE, glide * 0.015f * Mth.sin(time * 0.23f));
        float slow = m.w(WingMotion.BRAKE) - FlightRules.chute(m) + m.w(WingMotion.LANDING);
        drive.add(RigPose.BEND_TIPS, slow * 0.06f * Mth.sin(time * 2.1f));

        float ground = m.w(WingMotion.GROUND);
        float stretch = memory.get(STRETCH, m.partialTick) * ground;
        boolean rightSide = memory.now(SIDE) > 0.5f;
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.5f, 1.1f - 0.07f * k);
        float chute = FlightRules.chute(m);
        finish(right, rightSide ? stretch : 0f, tips);
        finish(left, rightSide ? 0f : stretch, tips);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);
        float alula = memory.get(ALULA, m.partialTick) * (1f - drive.get(RigPose.FOLD));
        rightAlula.rotate(0f, 0.3f * alula, 0.5f * alula);
        leftAlula.rotate(0f, -0.3f * alula, -0.5f * alula);
    }

    private void finish(FeatherWing wing, float stretch, FeatherWing.TipCurve tips) {
        side.copy(drive);
        side.add(RigPose.FOLD, -0.8f * stretch);
        side.add(RigPose.STROKE, 0.25f * stretch);
        side.add(RigPose.SWEEP, 0.2f * stretch);
        side.add(RigPose.FAN, 1.15f * stretch);
        side.add(RigPose.TWIST, 0.2f * stretch);
        side.set(RigPose.FOLD, Mth.clamp(side.get(RigPose.FOLD), 0f, 1f));
        side.set(RigPose.FAN, Mth.clamp(side.get(RigPose.FAN), 0f, 1.25f));
        wing.apply(side, tips);
    }
}
