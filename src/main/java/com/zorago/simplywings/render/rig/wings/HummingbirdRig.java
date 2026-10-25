package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class HummingbirdRig extends WingRig {
    private static final int ROUSE = 0;
    private static final float VISUAL_RATE = 3.2f;
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private final Bone[] ghosts = new Bone[4];

    public HummingbirdRig() {
        super("hummingbird");
    }

    @Override
    public Material material() {
        return Material.TRANSLUCENT;
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.3f, 2.8f, 2.9f);
        left = new FeatherWing(bones, "left_", -1f, 1.3f, 2.8f, 2.9f);
        ghosts[0] = bones.get("right_ghost_a");
        ghosts[1] = bones.get("right_ghost_b");
        ghosts[2] = bones.get("left_ghost_a");
        ghosts[3] = bones.get("left_ghost_b");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.3f : 0.2f, rousing ? 0.55f : 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float rouse = memory.get(ROUSE, m.partialTick);
        float ground = m.w(WingMotion.GROUND);
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float charge = m.w(WingMotion.CHARGE);
        float brake = m.w(WingMotion.BRAKE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake - chute;

        drive.blendIn(ground, state.clear().set(RigPose.FOLD, 1f - 0.75f * rouse).set(RigPose.STROKE, 0.1f + 0.2f * rouse));
        drive.blendIn(charge, state.clear().set(RigPose.STROKE, 0.25f).set(RigPose.SWEEP, 0.55f).set(RigPose.FAN, 1f)
                .set(RigPose.BEND_TIPS, -0.12f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.25f).set(RigPose.STROKE, 0.2f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.15f));
        drive.blendIn(hover, state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.12f)
                .set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(cruise, state.clear().set(RigPose.STROKE, 0.12f).set(RigPose.SWEEP, -0.1f)
                .set(RigPose.ASYM, m.bank * 0.25f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.3f, 0.75f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.3f, -0.6f)).set(RigPose.FAN, Mth.lerp(d, 0.6f, 0f)));
        drive.blendIn(chute, state.clear().set(RigPose.STROKE, -0.525f).set(RigPose.SWEEP, 0.704f).set(RigPose.FAN, 1.05f)
                .set(RigPose.TWIST, -0.2f).set(RigPose.ELBOW, 0.26f).set(RigPose.WRIST, 0.346f)
                .set(RigPose.BEND_ELBOW, -0.349f).set(RigPose.BEND_WRIST, -0.218f).set(RigPose.CAMBER, 0.35f));
        drive.blendIn(slowBrake, state.clear().set(RigPose.STROKE, 0.3f).set(RigPose.SWEEP, 0.2f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.9f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.05f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.2f));
        }

        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            blast = p < 0.15f ? p / 0.15f : (1f - p) * (1f - p) / 0.7225f;
        }
        float shiver = charge * charge;
        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        float buzz = Math.max(Math.max(m.buzz, 0.9f * rouse * ground), Math.max(0.6f * shiver, blast)) * (1f - fold) * (1f - chute);
        float amplitude = buzz * (0.45f * cruise + 0.6f * hover + 0.3f * slowBrake + 0.25f * charge
                + 0.45f * (1f - Mth.clamp(cruise + hover + brake + charge, 0f, 1f))) + 0.5f * blast;
        float center = FLOOR * Mth.clamp(amplitude * 10f, 0f, 1f) + amplitude * 1.15f;
        float ph = time * VISUAL_RATE;
        float swing = Mth.sin(ph) + 0.18f * Mth.sin(ph * 2.7f + 1f);
        float baseStroke = drive.get(RigPose.STROKE) + center;
        float baseSweep = drive.get(RigPose.SWEEP);
        float baseTwist = drive.get(RigPose.TWIST);
        drive.set(RigPose.STROKE, baseStroke + amplitude * swing);
        drive.add(RigPose.TWIST, buzz * Mth.lerp(hover, 0.6f, 0.55f) * Mth.cos(ph));
        drive.add(RigPose.SWEEP, buzz * (0.06f + 0.1f * hover) * Mth.sin(2f * ph));
        drive.add(RigPose.BEND_TIPS, buzz * 0.15f * Mth.cos(ph));
        drive.add(RigPose.STROKE, shiver * 0.03f * Mth.sin(time * 2.9f));
        drive.add(RigPose.BEND_TIPS, chute * (-0.1f + 0.05f * Mth.sin(time * 4.7f) + 0.03f * Mth.sin(time * 7.3f)));
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.TWIST, sw * 0.4f * Mth.sin(time * 1.3f));
            drive.add(RigPose.ASYM, sw * 0.3f * Mth.sin(time * 0.8f));
        }
        drive.add(RigPose.FOLD, ground * -0.015f * (1f + Mth.sin(time * 0.15f)));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.05f));
        FeatherWing.TipCurve tips = (k, n) -> 1f - 0.05f * k;
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.6f, 0.5f, 0.4f);
        left.stack(fold, 0.6f, 0.5f, 0.4f);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);

        boolean show = buzz > 0.35f && fold < 0.15f;
        float reach = amplitude * 1.05f;
        for (int i = 0; i < 4; i++) {
            float s = i < 2 ? 1f : -1f;
            float sign = i % 2 == 0 ? 1f : -1f;
            ghosts[i].visible(show);
            ghosts[i].rotate(baseTwist + sign * 0.5f * buzz * (1f - hover), s * Math.max(FLOOR, baseStroke + sign * reach), s * baseSweep);
        }
    }
}
