package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.resources.ResourceLocation;

public final class RiggedWingModel {
    public static final List<String> RIGS = List.of("basic");
    public static final float ATTACH_X = 2.5f;
    public static final float ATTACH_Y = 1.5f;
    public static final float ATTACH_Z = 2.4f;
    private static final float FOLD_ARM = 1.25f;
    private static final float FOLD_ELBOW = 2.75f;
    private static final float FOLD_WRIST = 2.6f;
    private static final int PRIMARIES = 5;

    private final ModelPart root;
    private final ResourceLocation texture;
    private final Side right;
    private final Side left;

    private record Bone(ModelPart part, PartPose rest) {
    }

    private record Side(Map<String, Bone> bones, float sign) {
        Bone get(String name) {
            return bones.get(name);
        }
    }

    public static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(SimplyWings.id(name + "_rig"), "main");
    }

    public RiggedWingModel(ModelPart root, String name, Map<String, List<String>> paths) {
        this.root = root;
        this.texture = SimplyWings.id("textures/entity/" + name + "_wings.png");
        this.right = side(root.getChild(RigLoader.RIGHT), paths, 1f);
        this.left = side(root.getChild(RigLoader.LEFT), paths, -1f);
    }

    public ResourceLocation texture() {
        return texture;
    }

    private static Side side(ModelPart sideRoot, Map<String, List<String>> paths, float sign) {
        Map<String, Bone> bones = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : paths.entrySet()) {
            ModelPart part = sideRoot;
            boolean found = true;
            for (String name : entry.getValue()) {
                if (!part.hasChild(name)) {
                    found = false;
                    break;
                }
                part = part.getChild(name);
            }
            if (found) {
                bones.put(entry.getKey(), new Bone(part, part.storePose()));
            }
        }
        return new Side(bones, sign);
    }

    public void setup(RigPose pose) {
        apply(right, pose);
        apply(left, pose);
    }

    private static void apply(Side side, RigPose p) {
        float s = side.sign();
        float fold = p.get(RigPose.FOLD);
        float fan = p.get(RigPose.FAN) * (1f - fold);
        side.bones().values().forEach(b -> b.part().loadPose(b.rest()));
        rotate(side.get("shoulder"), p.get(RigPose.TWIST),
                s * (p.get(RigPose.STROKE) - s * p.get(RigPose.ASYM)), s * p.get(RigPose.SWEEP));
        Bone shoulder = side.get("shoulder");
        if (shoulder != null) {
            shoulder.part().xScale = WingStyle.SCALE;
            shoulder.part().yScale = WingStyle.SCALE;
            shoulder.part().zScale = WingStyle.SCALE;
        }
        rotate(side.get("arm"), 0f, 0f, s * -FOLD_ARM * fold);
        rotate(side.get("tertials"), 0f, 0f, s * FOLD_ARM * fold * 0.85f);
        rotate(side.get("forearm"), 0f, s * p.get(RigPose.BEND_ELBOW), s * (FOLD_ELBOW * fold - p.get(RigPose.ELBOW)));
        rotate(side.get("secondaries"), 0f, 0f, s * (-(FOLD_ELBOW - FOLD_ARM) * fold * 0.95f + 0.4f * p.get(RigPose.ELBOW)));
        rotate(side.get("hand"), p.get(RigPose.HAND_TWIST), s * p.get(RigPose.BEND_WRIST), s * (-FOLD_WRIST * fold - p.get(RigPose.WRIST)));
        rotate(side.get("hand_coverts"), 0f, 0f, s * ((FOLD_ELBOW - FOLD_ARM - FOLD_WRIST) * -0.6f * fold));
        for (int k = 1; k <= PRIMARIES; k++) {
            Bone feather = side.get("primary_" + k);
            if (feather != null) {
                feather.part().zRot = feather.rest().zRot * fan;
                feather.part().yRot = feather.rest().yRot + s * p.get(RigPose.BEND_TIPS) * (1.2f - 0.15f * k);
            }
        }
    }

    private static void rotate(Bone bone, float x, float y, float z) {
        if (bone == null) {
            return;
        }
        bone.part().xRot = bone.rest().xRot + x;
        bone.part().yRot = bone.rest().yRot + y;
        bone.part().zRot = bone.rest().zRot + z;
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        root.render(poseStack, consumer, light, overlay, color);
    }
}
