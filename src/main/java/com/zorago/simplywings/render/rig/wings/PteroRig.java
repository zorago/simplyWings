package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
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
    private static final Cycle STROKE = Cycle.of(KEYS, -0.85f, 0.15f, 1.05f, 1.12f, -0.3f, -1.0f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.18f, -0.12f, -0.22f, -0.05f, 0.25f, 0.32f);
    private static final Cycle FLEX = Cycle.of(KEYS, 0.2f, 0.45f, 0.18f, 0f, 0f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.28f, -0.1f);
    private static final Cycle BILLOW = Cycle.of(KEYS, 0f, -0.2f, -0.15f, 0.4f, 0.6f, 0.2f);

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

    private static final class Side {
        final float s;
        final Bone shoulder;
        final Bone arm;
        final Bone armWeb;
        final Bone forearm;
        final Bone forearmWeb;
        final Bone front;
        final Bone hand;
        final Bone handWeb;
        final Bone claws;
        final Bone[] finger = new Bone[PHALANGES];
        final Bone[] web = new Bone[PHALANGES];

        Side(Bones b, String p, float s) {
            this.s = s;
            shoulder = b.get(p + "shoulder");
            arm = b.get(p + "arm");
            armWeb = b.get(p + "arm_membrane");
            forearm = b.get(p + "forearm");
            forearmWeb = b.get(p + "forearm_membrane");
            front = b.get(p + "propatagium");
            hand = b.get(p + "hand");
            handWeb = b.get(p + "hand_membrane");
            claws = b.get(p + "claws");
            for (int k = 0; k < PHALANGES; k++) {
                finger[k] = b.get(p + "finger_" + (k + 1));
                web[k] = b.get(p + "finger_" + (k + 1) + "_membrane");
            }
        }

        void apply(RigPose d, float billow, float flutter) {
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
                finger[k].rotate(0f, s * tips * reach, s * (bend - curl * (0.04f + 0.03f * k)));
                float spread = Mth.lerp(fold, 1f, 0.5f + 0.03f * k);
                web[k].rotate(0.26f * fold + billow * 0.28f + flutter * (0.5f + 0.3f * k), 0f, 0f).scale(1f, spread, 1f);
            }
            armWeb.rotate(billow * 0.2f, 0f, s * 0.45f * fold).scale(1f, Mth.lerp(fold, 1f, 0.55f), 1f);
            forearmWeb.rotate(0.44f * fold + billow * 0.3f, 0f, 0f).scale(1f, Mth.lerp(fold, 1f, 0.32f), 1f);
            handWeb.rotate(0.44f * fold + billow * 0.3f, 0f, 0f).scale(1f, Mth.lerp(fold, 1f, 0.4f), 1f);
            front.rotate(0.7f * fold - billow * 0.15f, 0f, 0f).scale(1f, Mth.lerp(fold, 1f, 0.5f), 1f);
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
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.95f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.TWIST, 0.15f).set(RigPose.BEND_TIPS, 0.25f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.32f)
                .set(RigPose.SWEEP, 0.12f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.7f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.2f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.22f)
                .set(RigPose.SWEEP, 0.12f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.04f)
                .set(RigPose.BEND_TIPS, -0.14f).set(RigPose.FAN, 1f).set(RigPose.ASYM, m.bank * 0.4f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.22f, 0.62f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.18f, -0.55f)).set(RigPose.STROKE, 0.05f)
                .set(RigPose.BEND_TIPS, -0.1f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.STROKE, 0.45f).set(RigPose.SWEEP, 0.55f)
                .set(RigPose.TWIST, -0.8f).set(RigPose.BEND_TIPS, 0.3f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.06f));
        billow += 0.35f * m.w(WingMotion.LANDING) + 0.2f * m.w(WingMotion.CRUISE) + 0.7f * m.w(WingMotion.BRAKE);

        if (settle > 0.001f) {
            float wobble = Mth.sin(time * 1.6f);
            drive.add(RigPose.STROKE, settle * 0.18f * wobble);
            drive.add(RigPose.FOLD, settle * 0.08f * Mth.sin(time * 1.6f + 1f));
            billow += settle * 0.5f * Mth.sin(time * 2.4f);
        }
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.15f));
            drive.add(RigPose.STROKE, m.stall * 0.55f * Mth.sin(time * 1.2f));
            drive.add(RigPose.TWIST, m.stall * 0.45f * Mth.sin(time * 0.9f));
            drive.add(RigPose.ASYM, m.stall * 0.35f * Mth.sin(time * 0.7f));
            billow += m.stall * 0.4f * Mth.sin(time * 2.1f);
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float sn = Mth.sin(ph);
            float cs = Mth.cos(ph);
            float back = Math.max(0f, -sn);
            drive.add(RigPose.STROKE, hw * 0.7f * cs);
            drive.add(RigPose.SWEEP, hw * 0.2f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.5f * sn);
            drive.add(RigPose.FOLD, hw * 0.32f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.4f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.55f * (Mth.cos(ph - 0.6f) - cs));
            billow += hw * 0.45f * Math.max(0f, sn);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.FOLD, a * FLEX.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.12f * STROKE.lagged(t, 0.05f));
            drive.add(RigPose.BEND_WRIST, a * 0.28f * STROKE.lagged(t, 0.09f));
            drive.add(RigPose.BEND_TIPS, a * 0.6f * STROKE.lagged(t, 0.14f));
            billow += a * BILLOW.at(t);
        }
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.02f * Mth.sin(time * 0.06f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.03f * Mth.sin(time * 0.21f));
        drive.add(RigPose.ASYM, glide * 0.04f * Mth.sin(time * 0.13f + 1f));
        flutter = (glide * (0.03f + 0.07f * gust)) * (Mth.sin(time * 3.3f) + 0.5f * Mth.sin(time * 5.9f))
                + m.w(WingMotion.BRAKE) * 0.1f * Mth.sin(time * 3.7f)
                + m.w(WingMotion.DIVE) * 0.09f * Mth.sin(time * 5.3f);

        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        right.apply(drive, billow, flutter);
        left.apply(drive, billow, flutter);
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
