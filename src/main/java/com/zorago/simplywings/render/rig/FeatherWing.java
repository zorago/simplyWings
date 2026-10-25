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
    public final List<Bone> tertial = new ArrayList<>();
    public final List<Bone> secondary = new ArrayList<>();
    public final List<Bone> handFeather = new ArrayList<>();
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
        for (int k = 1; bones.has(prefix + "tertial_feather_" + k); k++) {
            tertial.add(bones.get(prefix + "tertial_feather_" + k));
        }
        for (int k = 1; bones.has(prefix + "secondary_feather_" + k); k++) {
            secondary.add(bones.get(prefix + "secondary_feather_" + k));
        }
        for (int k = 1; bones.has(prefix + "hand_feather_" + k); k++) {
            handFeather.add(bones.get(prefix + "hand_feather_" + k));
        }
    }

    public void stack(float fold, float secondary, float tertial, float hand) {
        secondaries.scale(1f - secondary * fold, 1f, 1f);
        tertials.scale(1f - tertial * fold, 1f, 1f);
        handFeathers.scale(1f - hand * fold, 1f, 1f);
        handCoverts.scale(1f - hand * fold, 1f, 1f);
    }

    public interface TipCurve {
        float weight(int index, int count);
    }

    private static void row(Bone group, List<Bone> feathers, float turn, float fold, float twistIn, float twistOut, float camber) {
        if (feathers.isEmpty()) {
            group.rotate(camber, 0f, turn);
            return;
        }
        int n = feathers.size();
        for (int k = 0; k < n; k++) {
            Bone feather = feathers.get(k);
            float t = n == 1 ? 0.5f : k / (float) (n - 1);
            feather.rotation(feather.rest().xRot + camber, feather.rest().yRot + twistIn + (twistOut - twistIn) * t,
                    feather.rest().zRot * (1f - fold) + turn);
        }
    }

    public void apply(RigPose p, TipCurve tips) {
        float s = sign;
        float fold = p.get(RigPose.FOLD);
        float fan = p.get(RigPose.FAN) * (1f - fold);
        shoulder.rotate(p.get(RigPose.TWIST), s * p.get(RigPose.STROKE) - p.get(RigPose.ASYM), s * p.get(RigPose.SWEEP));
        arm.rotate(0f, 0f, s * -foldArm * fold);
        float bendElbow = s * p.get(RigPose.BEND_ELBOW);
        float bendWrist = s * p.get(RigPose.BEND_WRIST);
        float camber = p.get(RigPose.CAMBER);
        row(tertials, tertial, s * foldArm * fold * 0.85f, fold, 0f, 0.5f * bendElbow, camber);
        forearm.rotate(0f, s * p.get(RigPose.BEND_ELBOW), s * (foldElbow * fold - p.get(RigPose.ELBOW)));
        row(secondaries, secondary, s * (-(foldElbow - foldArm) * fold * 0.95f + 0.4f * p.get(RigPose.ELBOW)), fold,
                -0.5f * bendElbow, 0.5f * bendWrist, camber);
        hand.rotate(p.get(RigPose.HAND_TWIST), s * p.get(RigPose.BEND_WRIST), s * (-foldWrist * fold - p.get(RigPose.WRIST)));
        float handTurn = s * (foldArm - foldElbow + foldWrist) * 0.9f * fold;
        if (handFeather.isEmpty()) {
            handFeathers.rotate(camber, -0.3f * bendWrist, handTurn);
        } else {
            row(handFeathers, handFeather, handTurn, fold, -0.3f * bendWrist, 0f, camber);
        }
        handCoverts.rotate(0.5f * camber, -0.3f * bendWrist, s * ((foldElbow - foldArm - foldWrist) * -0.6f * fold));
        int count = primary.size();
        for (int k = 0; k < count; k++) {
            Bone feather = primary.get(k);
            feather.rotation(feather.rest().xRot, feather.rest().yRot + s * p.get(RigPose.BEND_TIPS) * tips.weight(k, count),
                    feather.rest().zRot * fan);
        }
    }
}
