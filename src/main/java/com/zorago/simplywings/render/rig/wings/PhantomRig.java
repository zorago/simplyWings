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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class PhantomRig extends WingRig {
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/phantom_glow.png");
    private static final int ROUSE = 0;
    private static final float FOLD_ARM = 86.6f * Mth.DEG_TO_RAD;
    private static final float FOLD_ELBOW = 21f * Mth.DEG_TO_RAD;
    private static final float FOLD_WRIST = 22.1f * Mth.DEG_TO_RAD;
    private static final float FOLD_PITCH = 23.5f * Mth.DEG_TO_RAD;
    private static final float FOLD_YAW = 3.4f * Mth.DEG_TO_RAD;
    private static final float FOLD_HAND_YAW = 6.5f * Mth.DEG_TO_RAD;
    private static final float FOLD_HAND_PITCH = 0.9f * Mth.DEG_TO_RAD;
    private static final float REST_FOLD = 1f;
    private static final float REST_SWEEP = 35.3f * Mth.DEG_TO_RAD;
    private static final float GLIDE_BILLOW = 0.45f;
    private static final String[] TIPS = {"right_hand", "left_hand"};
    private static final float[] KEYS = Cycle.beat(0.5f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.3f, 0.6f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.5f, 0.75f, 0.25f, -0.05f, -0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.25f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0f, 0f, 0f, 0f, 0f, 0f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.35f, -0.15f, 0f, 0.1f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final Bone[] shoulder = new Bone[2];
    private final Bone[] arm = new Bone[2];
    private final Bone[] forearm = new Bone[2];
    private final Bone[] hand = new Bone[2];

    public PhantomRig() {
        super("phantom");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            arm[i] = bones.get(side[i] + "arm");
            forearm[i] = bones.get(side[i] + "forearm");
            hand[i] = bones.get(side[i] + "hand");
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.12f : 0.08f, rousing ? 0.72f : 0.76f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float rouse = memory.get(ROUSE, m.partialTick);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(rouse, REST_FOLD, 0.5f))
                .set(RigPose.STROKE, 0.3f).set(RigPose.SWEEP, Mth.lerp(rouse, REST_SWEEP, 0.15f)).set(RigPose.TWIST, 0.082f * (1f - rouse)));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, -0.05f)
                .set(RigPose.BEND_TIPS, -0.12f).set(RigPose.ASYM, m.bank * 0.35f));
        float dd = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.15f, 0.5f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.35f, -0.75f)).set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.55f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.06f));
        FlightRules.apply(m, drive, BEAT);

        float glide = m.w(WingMotion.CRUISE) + 0.6f * m.w(WingMotion.HOVER);
        float wave = glide * 0.08f;
        drive.add(RigPose.STROKE, wave * Mth.sin(time * 0.35f));
        drive.add(RigPose.BEND_ELBOW, wave * (Mth.sin(time * 0.35f - 0.8f) - Mth.sin(time * 0.35f)));
        drive.add(RigPose.BEND_WRIST, wave * 1.6f * (Mth.sin(time * 0.35f - 1.6f) - Mth.sin(time * 0.35f)));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        float airborne = Mth.clamp(1f - m.w(WingMotion.GROUND) - m.w(WingMotion.CHARGE), 0f, 1f);
        float lift = airborne * (1f - fold);
        Membrane skin = membrane();
        if (skin != null) {
            skin.billow(Mth.clamp(GLIDE_BILLOW * lift + 2f * (drive.get(RigPose.CAMBER) + 0.15f * lift), -0.8f, 1.2f));
        }
        float chute = FlightRules.chute(m);
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(drive.get(RigPose.TWIST), s * drive.get(RigPose.STROKE) - drive.get(RigPose.ASYM),
                    s * drive.get(RigPose.SWEEP));
            arm[i].rotate(FOLD_PITCH * fold, s * -FOLD_YAW * fold, s * -FOLD_ARM * fold);
            forearm[i].rotate(0f, s * drive.get(RigPose.BEND_ELBOW), s * (FOLD_ELBOW * fold - drive.get(RigPose.ELBOW)));
            hand[i].rotate(drive.get(RigPose.HAND_TWIST) - FOLD_HAND_PITCH * fold,
                    s * (drive.get(RigPose.BEND_WRIST) + drive.get(RigPose.BEND_TIPS) - FOLD_HAND_YAW * fold),
                    s * (-FOLD_WRIST * fold - drive.get(RigPose.WRIST)));
            FlightRules.chuteArm(arm[i], s, chute);
        }
    }
}
