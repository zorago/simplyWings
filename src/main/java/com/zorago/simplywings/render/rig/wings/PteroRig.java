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

public final class PteroRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int GUST = 2;
    private static final int PHALANGES = 4;
    private static final int[] IDLES = {54, 64, 30};
    private static final float FOLD_ARM = 1.08f;
    private static final float FOLD_ELBOW = 2.62f;
    private static final float FOLD_WRIST = 0.07f;
    private static final float FOLD_KNUCKLE = 2.9f;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.18f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.52f, 0.86f, 0.82f, 0.32f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.05f, -0.6f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.1f, -0.72f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.32f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.3f, 0.55f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.2f, 0.35f, 0.12f, -0.03f, -0.05f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.28f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.3f, 0.6f, 0.2f, 0f, 0f, 0.1f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.4f, -0.15f, 0f, 0.12f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final float GLIDE_BILLOW = 0.55f;
    private static final String[] TIPS = {"right_finger_4", "left_finger_4"};

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private float billow;
    private float flutter;
    private Side right;
    private Side left;

    public PteroRig() {
        super("ptero");
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    private static final class Side {
        final float s;
        final Bone shoulder;
        final Bone arm;
        final Bone forearm;
        final Bone front;
        final Bone hand;
        final Bone claws;
        final Bone[] finger = new Bone[PHALANGES];

        Side(Bones b, String p, float s) {
            this.s = s;
            shoulder = b.get(p + "shoulder");
            arm = b.get(p + "arm");
            forearm = b.get(p + "forearm");
            front = b.get(p + "propatagium");
            hand = b.get(p + "hand");
            claws = b.get(p + "claws");
            for (int k = 0; k < PHALANGES; k++) {
                finger[k] = b.get(p + "finger_" + (k + 1));
            }
        }

        void apply(RigPose d, float billow, float flutter, float chute) {
            float fold = Mth.clamp(d.get(RigPose.FOLD), 0f, 1f);
            float tips = d.get(RigPose.BEND_TIPS);
            shoulder.rotate(d.get(RigPose.TWIST), s * d.get(RigPose.STROKE) - d.get(RigPose.ASYM), s * d.get(RigPose.SWEEP));
            arm.rotate(0f, 0f, s * -FOLD_ARM * fold);
            forearm.rotate(0f, s * d.get(RigPose.BEND_ELBOW), s * (FOLD_ELBOW * fold - d.get(RigPose.ELBOW)));
            hand.rotate(d.get(RigPose.HAND_TWIST), s * d.get(RigPose.BEND_WRIST), s * (FOLD_WRIST * fold - d.get(RigPose.WRIST)));
            claws.rotate(0f, 0f, s * 0.6f * fold);
            float curl = d.get(RigPose.FAN);
            for (int k = 0; k < PHALANGES; k++) {
                float reach = 0.55f + 0.25f * k;
                float bend = k == 0 ? -FOLD_KNUCKLE * fold : 0.07f * fold;
                finger[k].rotate(0f, s * (tips + flutter * (0.5f + 0.3f * k)) * reach, s * (bend - curl * (0.04f + 0.03f * k)));
            }
            front.rotate(0.7f * fold - billow * 0.15f, 0f, 0f);
            FlightRules.chuteArm(arm, s, chute);
        }
    }

    @Override
    protected void onBind(Bones bones) {
        right = new Side(bones, "right_", 1f);
        left = new Side(bones, "left_", -1f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.2f, 0.7f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.35f, 0.55f);
        float gust = Mth.clamp(m.speed / 30f, 0f, 1f) * m.w(WingMotion.CRUISE);
        memory.spring(GUST, gust, 0.1f, 0.8f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        Arrays.fill(drive.v, 0f);
        billow = 0f;
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float gust = memory.get(GUST, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float groundFold = 1f;
        float groundStroke = 0.06f;
        float groundSweep = 0f;
        float groundTwist = 0f;
        float groundBillow = 0f;
        if (idle > 0.001f) {
            switch (variant) {
                case 0 -> {
                    float open = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3.5f, 0f, 1f));
                    groundFold = Mth.lerp(open, 1f, 0.12f);
                    groundStroke = Mth.lerp(open, 0.06f, 0.55f);
                    groundSweep = open * (0.2f + 0.08f * Mth.sin(time * 0.9f));
                    groundBillow = open * 0.25f * Mth.sin(time * 1.4f);
                }
                case 1 -> {
                    float open = idle * smooth(Mth.clamp(p * 4f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 4f, 0f, 1f));
                    groundFold = Mth.lerp(open, 1f, 0.48f);
                    groundStroke = Mth.lerp(open, 0.06f, 0.12f);
                    groundTwist = -0.25f * open;
                    groundBillow = open * 0.35f * (0.5f + 0.5f * Mth.sin(time * 0.35f));
                }
                default -> {
                    float shake = idle * Mth.sin((float) Math.PI * p);
                    groundFold = 1f - 0.12f * shake;
                    groundStroke = 0.06f + 0.18f * shake * Mth.sin(time * 2.2f);
                    groundBillow = shake * 0.5f * Mth.sin(time * 3.1f);
                }
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, groundFold).set(RigPose.STROKE, groundStroke)
                .set(RigPose.SWEEP, groundSweep).set(RigPose.TWIST, groundTwist));
        billow += ground * groundBillow;
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.12f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.5f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.2f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.04f)
                .set(RigPose.BEND_TIPS, -0.14f).set(RigPose.FAN, 1f).set(RigPose.ASYM, m.bank * 0.4f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.22f, 0.62f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.18f, -0.55f)).set(RigPose.STROKE, 0.05f)
                .set(RigPose.BEND_TIPS, -0.1f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.BEND_TIPS, 0.1f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.06f));
        FlightRules.apply(m, drive, BEAT);
        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        float airborne = Mth.clamp(1f - ground - m.w(WingMotion.CHARGE), 0f, 1f);
        float lift = airborne * (1f - fold);
        billow += GLIDE_BILLOW * lift + 2f * (drive.get(RigPose.CAMBER) + 0.15f * lift);
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.18f * Mth.sin(time * 1.6f));
            drive.add(RigPose.FOLD, settle * 0.08f * Mth.sin(time * 1.6f + 1f));
            billow += settle * 0.5f * Mth.sin(time * 2.4f);
        }
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.03f * Mth.sin(time * 0.21f));
        drive.add(RigPose.ASYM, glide * 0.04f * Mth.sin(time * 0.13f + 1f));
        flutter = (glide * (0.02f + 0.05f * gust)) * (Mth.sin(time * 3.3f) + 0.5f * Mth.sin(time * 5.9f))
                + m.w(WingMotion.DIVE) * 0.05f * Mth.sin(time * 5.3f);
        billow += glide * gust * 0.15f * Mth.sin(time * 2.7f);
        Membrane membrane = membrane();
        if (membrane != null) {
            membrane.billow(Mth.clamp(billow, -0.8f, 1.2f));
        }
        float chute = FlightRules.chute(m);
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        right.apply(drive, billow, flutter, chute);
        left.apply(drive, billow, flutter, chute);
    }

    private void add(float weight, RigPose pose) {
        if (weight == 0f) {
            return;
        }
        for (int i = 0; i < RigPose.SIZE; i++) {
            drive.v[i] += weight * pose.v[i];
        }
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
