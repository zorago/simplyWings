package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingPose;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public final class WingModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(SimplyWings.id("basic_wings"), "main");

    private static final float SHOULDER_LENGTH = 9f;
    private static final float ELBOW_LENGTH = 8f;
    private static final float TIP_LENGTH = 6f;

    private final ModelPart root;
    private final Side left;
    private final Side right;

    private record Side(ModelPart shoulder, ModelPart elbow, ModelPart tip, float sign) {
    }

    public WingModel(ModelPart root) {
        this.root = root;
        this.left = side(root, "left", 1f);
        this.right = side(root, "right", -1f);
    }

    private static Side side(ModelPart root, String name, float sign) {
        ModelPart shoulder = root.getChild(name + "_shoulder");
        ModelPart elbow = shoulder.getChild(name + "_elbow");
        ModelPart tip = elbow.getChild(name + "_tip");
        return new Side(shoulder, elbow, tip, sign);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        addSide(root, "left", 1f);
        addSide(root, "right", -1f);
        return LayerDefinition.create(mesh, 64, 32);
    }

    private static void addSide(PartDefinition root, String name, float s) {
        PartDefinition shoulder = root.addOrReplaceChild(name + "_shoulder", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(s > 0 ? 0 : -SHOULDER_LENGTH, -0.5f, -0.5f, SHOULDER_LENGTH, 1f, 1f)
                        .texOffs(0, 4).addBox(s > 0 ? 0 : -SHOULDER_LENGTH, 0.5f, -0.25f, SHOULDER_LENGTH, 11f, 0.5f),
                PartPose.offset(1.5f * s, 1.5f, 2.6f));
        PartDefinition elbow = shoulder.addOrReplaceChild(name + "_elbow", CubeListBuilder.create()
                        .texOffs(20, 0).addBox(s > 0 ? 0 : -ELBOW_LENGTH, -0.5f, -0.5f, ELBOW_LENGTH, 1f, 1f)
                        .texOffs(20, 4).addBox(s > 0 ? 0 : -ELBOW_LENGTH, 0.5f, 0.05f, ELBOW_LENGTH, 9f, 0.5f),
                PartPose.offset(SHOULDER_LENGTH * s, 0f, 0f));
        elbow.addOrReplaceChild(name + "_tip", CubeListBuilder.create()
                        .texOffs(40, 0).addBox(s > 0 ? 0 : -TIP_LENGTH, -0.4f, -0.4f, TIP_LENGTH, 0.8f, 0.8f)
                        .texOffs(40, 4).addBox(s > 0 ? 0 : -TIP_LENGTH, 0.4f, 0.3f, TIP_LENGTH, 6f, 0.5f),
                PartPose.offset(ELBOW_LENGTH * s, 0f, 0f));
    }

    public void setup(WingPose pose) {
        apply(left, pose);
        apply(right, pose);
    }

    private static void apply(Side side, WingPose p) {
        float s = side.sign;
        float folded = 1f - Math.min(1f, p.spread);
        side.shoulder.yRot = s * (-1.35f * folded + p.sweep);
        side.shoulder.zRot = s * (-p.stroke + 0.45f * folded);
        side.shoulder.xRot = p.twist;
        side.elbow.yRot = s * (-2.5f * folded);
        side.elbow.zRot = s * -p.elbow;
        side.elbow.xRot = 0f;
        side.tip.yRot = s * (2.2f * folded);
        side.tip.zRot = s * -p.tip;
        side.tip.xRot = 0f;
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        root.render(poseStack, consumer, light, overlay, color);
    }
}
