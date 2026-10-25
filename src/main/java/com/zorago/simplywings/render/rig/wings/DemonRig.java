package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class DemonRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int SNAP = 2;
    private static final int FINGERS = 4;
    private static final int[] IDLES = {64, 90, 84, 56, 48};
    private static final float FOLD_ARM = 1.26f;
    private static final float FOLD_ELBOW = 2.74f;
    private static final float FOLD_WRIST = 2.79f;
    private static final float FINGER_CLOSED = -0.06f;
    private static final float GLIDE_BILLOW = 0.6f;
    private static final String[] TIPS = {"right_finger_1_tip", "left_finger_1_tip"};
    private static final float UP = 0.55f;
    private static final float DOWN = 0.08f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.55f, 0.9f, 0.86f, 0.3f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.05f, -0.65f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.1f, -0.75f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.32f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.3f, 0.65f, 0.25f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.5f, 0.8f, 0.3f, -0.05f, -0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.35f, 0.12f, -0.08f, -0.3f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, -0.05f, -0.3f, -0.1f, 0f, 0.05f, 0f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.45f, -0.15f, 0f, 0.12f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/demon_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose side = new RigPose();
    private float billow;
    private float flutter;
    private Side right;
    private Side left;

    public DemonRig() {
        super("demon");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
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

        void apply(RigPose d, float flutter, float tap, float chute) {
            float fold = d.get(RigPose.FOLD);
            float fan = Mth.clamp(d.get(RigPose.FAN), 0f, 1.1f) * (1f - fold);
            shoulder.rotate(d.get(RigPose.TWIST), s * d.get(RigPose.STROKE) - d.get(RigPose.ASYM), s * d.get(RigPose.SWEEP));
            arm.rotate(0f, 0f, s * -FOLD_ARM * fold);
            forearm.rotate(0f, s * d.get(RigPose.BEND_ELBOW), s * (FOLD_ELBOW * fold - d.get(RigPose.ELBOW)));
            hand.rotate(d.get(RigPose.HAND_TWIST), s * d.get(RigPose.BEND_WRIST), s * (-FOLD_WRIST * fold - d.get(RigPose.WRIST)));
            thumb.rotate(0f, 0f, s * (0.45f * fold + tap));
            for (int k = 0; k < FINGERS; k++) {
                Bone f = finger[k];
                f.rotation(f.rest().xRot, f.rest().yRot, f.rest().zRot * fan + s * FINGER_CLOSED * (1f - fan));
                float reach = 1f - 0.15f * k;
                tip[k].rotate(0f, s * (d.get(RigPose.BEND_TIPS) + flutter * (0.6f + 0.3f * k)) * reach, s * -0.25f * fold);
            }
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
        memory.spring(IDLE, m.rouse, 0.14f, 0.72f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.6f);
        boolean strike = m.beating() && m.beat >= UP && m.beat < UP + DOWN + 0.06f || m.launch >= 0f && m.launch < 0.35f;
        memory.spring(SNAP, strike ? m.beatWeight : 0f, strike ? 0.7f : 0.35f, 0.4f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        Arrays.fill(drive.v, 0f);
        billow = 0f;
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float snap = memory.get(SNAP, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));

        float gFold = 1f, gStroke = 0.1f, gSweep = 0f, gFan = 0f, gTwist = 0f;
        float ember = 0f;
        float tap = 0f;
        switch (variant) {
            case 0 -> {
                gFold = Mth.lerp(hold, 1f, 0.05f);
                gStroke = Mth.lerp(hold, 0.1f, 0.7f);
                gSweep = 0.45f * hold;
                gFan = 1f * hold;
                gTwist = -0.2f * hold;
                billow += hold * 0.2f * Mth.sin(time * 2.1f);
            }
            case 2 -> {
                gFold = Mth.lerp(hold, 1f, 0.35f);
                gStroke = Mth.lerp(hold, 0.1f, -0.35f);
                gSweep = 0.15f * hold;
                gFan = 0.8f * hold;
                gTwist = 0.25f * hold;
            }
            case 3 -> ember = env;
            case 4 -> tap = env * 0.35f * Math.max(0f, Mth.sin(time * 0.9f));
            default -> {
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke).set(RigPose.SWEEP, gSweep)
                .set(RigPose.FAN, gFan).set(RigPose.TWIST, gTwist));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.32f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.2f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 0.95f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.02f)
                .set(RigPose.FAN, 1f).set(RigPose.BEND_TIPS, 0.1f).set(RigPose.ASYM, m.bank * 0.35f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.62f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(dd, 0.85f, 0.3f))
                .set(RigPose.STROKE, 0.05f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.1f).set(RigPose.BEND_TIPS, 0.1f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        float airborne = Mth.clamp(1f - ground - m.w(WingMotion.CHARGE), 0f, 1f);
        float lift = airborne * (1f - fold);
        billow += GLIDE_BILLOW * lift + 2f * (drive.get(RigPose.CAMBER) + 0.15f * lift);
        billow += snap * 0.35f * Mth.sin(time * 4.2f);
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.14f * Mth.sin(time * 1.5f));
            billow += settle * 0.4f * Mth.sin(time * 2.2f);
        }
        billow += ember * 0.25f * Mth.sin(time * 1.6f);
        float chute = FlightRules.chute(m);
        float glide = m.w(WingMotion.CRUISE);
        flutter = glide * 0.03f * (Mth.sin(time * 2.9f) + 0.6f * Mth.sin(time * 4.7f))
                + m.w(WingMotion.DIVE) * 0.04f * Mth.sin(time * 5.1f)
                + ember * 0.06f * Mth.sin(time * 6.3f);
        Membrane membrane = membrane();
        if (membrane != null) {
            membrane.billow(Mth.clamp(billow, -0.8f, 1.2f));
        }

        float stretch = variant == 1 ? idle : 0f;
        for (int i = 0; i < 2; i++) {
            side.copy(drive);
            if (stretch > 0.001f) {
                float local = Mth.clamp(i == 0 ? p * 2f : p * 2f - 1f, 0f, 1f);
                float w = stretch * Mth.sin((float) Math.PI * local) * ground;
                side.add(RigPose.FOLD, -0.85f * w);
                side.add(RigPose.STROKE, 0.35f * w);
                side.add(RigPose.SWEEP, 0.5f * w);
                side.add(RigPose.FAN, 1f * w);
                side.add(RigPose.BEND_TIPS, -0.2f * w);
            }
            side.set(RigPose.FOLD, Mth.clamp(side.get(RigPose.FOLD), 0f, 1f));
            (i == 0 ? right : left).apply(side, flutter, tap, chute);
        }
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
