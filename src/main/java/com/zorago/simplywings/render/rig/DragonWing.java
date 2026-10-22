package com.zorago.simplywings.render.rig;

import com.zorago.simplywings.anim.RigPose;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;

public final class DragonWing {
    public final float sign;
    public final Bone shoulder;
    public final Bone arm;
    public final Bone armWeb;
    public final Bone forearm;
    public final Bone forearmWeb;
    public final Bone hand;
    public final Bone thumb;
    public final List<Bone> finger = new ArrayList<>();
    public final List<Bone> tip = new ArrayList<>();
    public final List<Bone> web = new ArrayList<>();
    public final List<Bone> tipWeb = new ArrayList<>();
    private final float foldArm;
    private final float foldElbow;
    private final float foldWrist;

    public DragonWing(Bones b, String prefix, float sign, float foldArm, float foldElbow, float foldWrist) {
        this.sign = sign;
        this.foldArm = foldArm;
        this.foldElbow = foldElbow;
        this.foldWrist = foldWrist;
        shoulder = b.get(prefix + "shoulder");
        arm = b.get(prefix + "arm");
        armWeb = b.get(prefix + "arm_membrane");
        forearm = b.get(prefix + "forearm");
        forearmWeb = b.get(prefix + "forearm_membrane");
        hand = b.get(prefix + "hand");
        thumb = b.get(prefix + "thumb");
        for (int k = 1; b.has(prefix + "finger_" + k); k++) {
            String f = prefix + "finger_" + k;
            finger.add(b.get(f));
            tip.add(b.get(f + "_tip"));
            web.add(b.get(f + "_web"));
            tipWeb.add(b.get(f + "_tip_web"));
        }
    }

    public void apply(RigPose d, float billow, float flutter, float thumbFlex) {
        float s = sign;
        float fold = Mth.clamp(d.get(RigPose.FOLD), 0f, 1f);
        float fan = Mth.clamp(d.get(RigPose.FAN), 0f, 1.15f) * (1f - fold);
        float spread = 1f - 0.62f * fold;
        shoulder.rotate(d.get(RigPose.TWIST), s * d.get(RigPose.STROKE) - d.get(RigPose.ASYM), s * d.get(RigPose.SWEEP));
        arm.rotate(0f, 0f, s * -foldArm * fold);
        forearm.rotate(0f, s * d.get(RigPose.BEND_ELBOW), s * (foldElbow * fold - d.get(RigPose.ELBOW)));
        hand.rotate(d.get(RigPose.HAND_TWIST), s * d.get(RigPose.BEND_WRIST), s * (-foldWrist * fold - d.get(RigPose.WRIST)));
        thumb.rotate(0f, 0f, s * (0.45f * fold + thumbFlex));
        armWeb.rotate(billow * 0.3f, 0f, s * 0.35f * fold).scale(1f, spread, 1f);
        forearmWeb.rotate(billow * 0.3f, 0f, s * -0.2f * fold).scale(1f, spread, 1f);
        int n = finger.size();
        for (int k = 0; k < n; k++) {
            Bone f = finger.get(k);
            f.rotation(f.rest().xRot, f.rest().yRot, f.rest().zRot * fan + s * -0.06f * (1f - fan));
            float reach = 1f - 0.15f * k;
            tip.get(k).rotate(0f, s * d.get(RigPose.BEND_TIPS) * reach, s * -0.25f * fold);
            web.get(k).rotate(billow * 0.2f, 0f, 0f).scale(1f, spread, 1f);
            tipWeb.get(k).rotate(billow * 0.25f + flutter * (0.6f + 0.3f * k), 0f, 0f).scale(1f, spread, 1f);
        }
    }
}
