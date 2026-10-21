package com.zorago.simplywings.render.rig;

import com.zorago.simplywings.anim.RigPose;
import java.util.ArrayList;
import java.util.List;

public final class FeatherWing {
    public final float sign;
    public final Bone shoulder;
    public final Bone arm;
    public final Bone tertials;
    public final Bone forearm;
    public final Bone secondaries;
    public final Bone hand;
    public final Bone handFeathers;
    public final Bone handCoverts;
    public final Bone primaries;
    public final List<Bone> primary = new ArrayList<>();
    private final float foldArm;
    private final float foldElbow;
    private final float foldWrist;

    public FeatherWing(Bones bones, String prefix, float sign, float foldArm, float foldElbow, float foldWrist) {
        this.sign = sign;
        this.foldArm = foldArm;
        this.foldElbow = foldElbow;
        this.foldWrist = foldWrist;
        shoulder = bones.get(prefix + "shoulder");
        arm = bones.get(prefix + "arm");
        tertials = bones.get(prefix + "tertials");
        forearm = bones.get(prefix + "forearm");
        secondaries = bones.get(prefix + "secondaries");
        hand = bones.get(prefix + "hand");
        handFeathers = bones.get(prefix + "hand_feathers");
        handCoverts = bones.get(prefix + "hand_coverts");
        primaries = bones.get(prefix + "primaries");
        for (int k = 1; bones.has(prefix + "primary_" + k); k++) {
            primary.add(bones.get(prefix + "primary_" + k));
        }
    }

    public interface TipCurve {
        float weight(int index, int count);
    }

    public void apply(RigPose p, TipCurve tips) {
        float s = sign;
        float fold = p.get(RigPose.FOLD);
        float fan = p.get(RigPose.FAN) * (1f - fold);
        shoulder.rotate(p.get(RigPose.TWIST), s * p.get(RigPose.STROKE) - p.get(RigPose.ASYM), s * p.get(RigPose.SWEEP));
        arm.rotate(0f, 0f, s * -foldArm * fold);
        tertials.rotate(0f, 0f, s * foldArm * fold * 0.85f);
        forearm.rotate(0f, s * p.get(RigPose.BEND_ELBOW), s * (foldElbow * fold - p.get(RigPose.ELBOW)));
        secondaries.rotate(0f, 0f, s * (-(foldElbow - foldArm) * fold * 0.95f + 0.4f * p.get(RigPose.ELBOW)));
        hand.rotate(p.get(RigPose.HAND_TWIST), s * p.get(RigPose.BEND_WRIST), s * (-foldWrist * fold - p.get(RigPose.WRIST)));
        handFeathers.rotate(0f, 0f, s * (foldArm - foldElbow + foldWrist) * 0.9f * fold);
        handCoverts.rotate(0f, 0f, s * ((foldElbow - foldArm - foldWrist) * -0.6f * fold));
        int count = primary.size();
        for (int k = 0; k < count; k++) {
            Bone feather = primary.get(k);
            feather.rotation(feather.rest().xRot, feather.rest().yRot + s * p.get(RigPose.BEND_TIPS) * tips.weight(k, count),
                    feather.rest().zRot * fan);
        }
    }
}
