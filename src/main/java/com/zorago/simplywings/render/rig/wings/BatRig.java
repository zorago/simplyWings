package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import java.util.Arrays;
import net.minecraft.util.Mth;

public final class BatRig extends WingRig {
    private static final int ROUSE = 0;
    private static final int FINGERS = 4;
    private static final float FOLD_ARM = 1.26f;
    private static final float FOLD_ELBOW = 2.74f;
    private static final float FOLD_WRIST = 2.79f;
    private static final float FINGER_CLOSED = -0.06f;
    private static final float GLIDE_BILLOW = 0.5f;
    private static final String[] TIPS = {"right_finger_1_tip", "left_finger_1_tip"};
    private static final float[] KEYS = Cycle.beat(0.4f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.3f, 0.6f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.5f, 0.75f, 0.25f, -0.05f, -0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, -0.15f, -0.35f, -0.05f, 0.05f, 0.05f, 0f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.4f, -0.15f, 0f, 0.1f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private Side right;
    private Side left;

    public BatRig() {
        super("bat");
    }

    private static final class Side {
        final float s;
        final Bone shoulder;
        final Bone arm;
        final Bone forearm;
        final Bone hand;
        final Bone thumb;
        final Bone[] finger = new Bone[FINGERS];
        final Bone[] tip = new Bone[FINGERS];

        Side(Bones b, String p, float s) {
            this.s = s;
            shoulder = b.get(p + "shoulder");
            arm = b.get(p + "arm");
            forearm = b.get(p + "forearm");
            hand = b.get(p + "hand");
            thumb = b.get(p + "thumb");
            for (int k = 0; k < FINGERS; k++) {
                String f = p + "finger_" + (k + 1);
                finger[k] = b.get(f);
                tip[k] = b.get(f + "_tip");
            }
        }

        void apply(RigPose d, float chute) {
            float fold = d.get(RigPose.FOLD);
            float fan = Mth.clamp(d.get(RigPose.FAN), 0f, 1.1f) * (1f - fold);
            shoulder.rotate(d.get(RigPose.TWIST), s * d.get(RigPose.STROKE) - d.get(RigPose.ASYM), s * d.get(RigPose.SWEEP));
            arm.rotate(0f, 0f, s * -FOLD_ARM * fold);
            forearm.rotate(0f, s * d.get(RigPose.BEND_ELBOW), s * (FOLD_ELBOW * fold - d.get(RigPose.ELBOW)));
            hand.rotate(d.get(RigPose.HAND_TWIST), s * d.get(RigPose.BEND_WRIST), s * (-FOLD_WRIST * fold - d.get(RigPose.WRIST)));
            thumb.rotate(0f, 0f, s * 0.5f * fold);
            for (int k = 0; k < FINGERS; k++) {
                Bone f = finger[k];
                f.rotation(f.rest().xRot, f.rest().yRot, f.rest().zRot * fan + s * FINGER_CLOSED * (1f - fan));
                float reach = 1f - 0.18f * k;
                tip[k].rotate(0f, s * d.get(RigPose.BEND_TIPS) * reach, s * -0.25f * fold);
            }
            FlightRules.chuteArm(arm, s, chute);
        }
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new Side(bones, "right_", 1f);
        left = new Side(bones, "left_", -1f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.14f : 0.09f, rousing ? 0.7f : 0.74f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        Arrays.fill(drive.v, 0f);
        float rouse = memory.get(ROUSE, m.partialTick);
        add(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(rouse, 1f, 0.45f))
                .set(RigPose.STROKE, Mth.lerp(rouse, 0.08f, 0.35f)).set(RigPose.SWEEP, 0.2f * rouse)
                .set(RigPose.FAN, 0.9f * rouse));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.25f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 0.9f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.FAN, 1f).set(RigPose.BEND_TIPS, 0.15f).set(RigPose.ASYM, m.bank * 0.35f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(dd, 0.85f, 0.3f))
                .set(RigPose.STROKE, 0.05f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.1f).set(RigPose.BEND_TIPS, 0.1f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);

        float fold = drive.get(RigPose.FOLD);
        float airborne = Mth.clamp(1f - m.w(WingMotion.GROUND) - m.w(WingMotion.CHARGE), 0f, 1f);
        float lift = airborne * (1f - fold);
        float billow = GLIDE_BILLOW * lift + 2f * (drive.get(RigPose.CAMBER) + 0.15f * lift);
        Membrane membrane = membrane();
        if (membrane != null) {
            membrane.billow(Mth.clamp(billow, -0.8f, 1.2f));
        }
        float chute = FlightRules.chute(m);
        right.apply(drive, chute);
        left.apply(drive, chute);
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
