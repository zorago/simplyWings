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

public final class PeacockRig extends WingRig {
    private static final int DISPLAY = 0;
    private static final int SWAY = 1;
    private static final int TRAIN = 4;
    private static final float[] REST = {-60f, -70f, -80f, -90f};
    private static final float[] OPEN = {80f, 60f, 39f, 18f};
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.2f, 0.6f, 0.3f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.35f, 0.75f, 0.35f, -0.05f, -0.1f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.3f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.25f, 0.05f, 0.2f, 0.15f, 0.1f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.55f, -0.2f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private final Bone[] train = new Bone[TRAIN * 2];
    private Bone rightTrain;
    private Bone leftTrain;

    public PeacockRig() {
        super("peacock");
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
        rightTrain = bones.get("right_train");
        leftTrain = bones.get("left_train");
        for (int k = 0; k < TRAIN; k++) {
            train[k] = bones.get("right_train_" + (k + 1));
            train[TRAIN + k] = bones.get("left_train_" + (k + 1));
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float show = Math.max(m.w(WingMotion.HOVER), Math.max(m.rouse > 0f ? m.w(WingMotion.GROUND) : 0f,
                0.4f * m.w(WingMotion.BRAKE)));
        memory.spring(DISPLAY, show, show > 0.5f ? 0.09f : 0.12f, show > 0.5f ? 0.76f : 0.7f);
        memory.spring(SWAY, Mth.clamp(m.bank * 1.5f - m.climb * 0.03f, -1f, 1f), 0.08f, 0.82f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float display = Mth.clamp(memory.get(DISPLAY, m.partialTick), 0f, 1.1f);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(display, 1f, 0.75f))
                .set(RigPose.STROKE, Mth.lerp(display, 0.1f, -0.2f)).set(RigPose.SWEEP, -0.15f * display)
                .set(RigPose.FAN, 0.3f * display));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.2f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.2f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.05f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, -0.08f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.FAN, 1f).set(RigPose.BEND_TIPS, -0.05f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.25f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.45f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.2f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.8f).set(RigPose.SWEEP, -0.5f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        float ground = m.w(WingMotion.GROUND);

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.2f));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.08f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        float chute = FlightRules.chute(m);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);

        float open = Mth.clamp(display, 0f, 1f);
        float rattle = open * open * 0.045f * Mth.sin(time * 4.1f) * (0.6f + 0.4f * Mth.sin(time * 0.37f));
        float sway = memory.get(SWAY, m.partialTick);
        float flying = 1f - ground;
        float length = Mth.lerp(Math.max(open, 0.6f * flying), 0.72f, 1f);
        rightTrain.rotate(-0.25f * open, 0f, flying * 0.12f * sway);
        leftTrain.rotate(-0.25f * open, 0f, flying * 0.12f * sway);
        for (int k = 0; k < TRAIN; k++) {
            float delta = (OPEN[k] - REST[k]) * Mth.DEG_TO_RAD * open;
            float trail = flying * (1f - open) * (REST[k] + 90f) * -0.6f * Mth.DEG_TO_RAD;
            float wave = flying * 0.05f * Mth.sin(time * 0.6f - k * 0.7f);
            train[k].rotate(0f, 0f, delta + trail + wave + rattle).scale(length, 1f, 1f);
            train[TRAIN + k].rotate(0f, 0f, -(delta + trail + wave + rattle)).scale(length, 1f, 1f);
        }
    }
}
