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
import net.minecraft.resources.ResourceLocation;

public final class WingModel {
    public record Segment(float length, float bone, int strips, float plateFrom, float plateTo, int u) {
    }

    public record Shape(String name, Segment shoulder, Segment elbow, Segment tip, int textureWidth,
                        int textureHeight) {
        public ModelLayerLocation layer() {
            return new ModelLayerLocation(SimplyWings.id(name + "_wings"), "main");
        }

        public ResourceLocation texture() {
            return SimplyWings.id("textures/entity/" + name + "_wings.png");
        }
    }

    public static final Shape BASIC = new Shape("basic",
            new Segment(9f, 1f, 1, 11f, 11f, 0),
            new Segment(8f, 1f, 1, 9f, 9f, 20),
            new Segment(6f, 0.8f, 1, 6f, 6f, 40),
            64, 32);

    public static final Shape ANGEL = new Shape("angel",
            new Segment(13f, 1.4f, 3, 13f, 17f, 0),
            new Segment(12f, 1.2f, 3, 17f, 21f, 30),
            new Segment(11f, 1f, 4, 21f, 26f, 58),
            128, 32);

    public static final Shape BAT = new Shape("bat",
            new Segment(9f, 0.8f, 2, 8f, 9f, 0),
            new Segment(10f, 0.7f, 3, 10f, 6f, 24),
            new Segment(10f, 0.6f, 4, 8f, 3f, 48),
            128, 32);

    public static final Shape ANCIENT = new Shape("ancient",
            new Segment(12f, 1.6f, 3, 12f, 15f, 0),
            new Segment(12f, 1.4f, 3, 15f, 18f, 30),
            new Segment(10f, 1.2f, 4, 18f, 22f, 58),
            128, 32);

    private static final float HALF_PI = (float) (Math.PI / 2);

    private final ModelPart root;
    private final Side left;
    private final Side right;
    private final Shape shape;

    private record Side(ModelPart root, ModelPart shoulder, ModelPart elbow, ModelPart tip, float sign) {
    }

    public WingModel(ModelPart root, Shape shape) {
        this.root = root;
        this.shape = shape;
        this.left = side(root, "left", 1f);
        this.right = side(root, "right", -1f);
    }

    public Shape shape() {
        return shape;
    }

    private static Side side(ModelPart root, String name, float sign) {
        ModelPart base = root.getChild(name + "_root");
        ModelPart shoulder = base.getChild(name + "_shoulder");
        ModelPart elbow = shoulder.getChild(name + "_elbow");
        ModelPart tip = elbow.getChild(name + "_tip");
        return new Side(base, shoulder, elbow, tip, sign);
    }

    public static LayerDefinition createLayer(Shape shape) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        addSide(root, shape, "left", 1f);
        addSide(root, shape, "right", -1f);
        return LayerDefinition.create(mesh, shape.textureWidth(), shape.textureHeight());
    }

    private static CubeListBuilder segment(Segment seg, float s, float depth) {
        float half = seg.bone() / 2f;
        float x0 = s > 0 ? 0 : -seg.length();
        CubeListBuilder builder = CubeListBuilder.create()
                .texOffs(seg.u(), 0).addBox(x0, -half, -half, seg.length(), seg.bone(), seg.bone());
        float stripWidth = seg.length() / seg.strips();
        for (int i = 0; i < seg.strips(); i++) {
            float t = seg.strips() == 1 ? 1f : i / (float) (seg.strips() - 1);
            float along = s > 0 ? t : 1f - t;
            float height = seg.plateFrom() + (seg.plateTo() - seg.plateFrom()) * along;
            float x = x0 + i * stripWidth;
            builder.texOffs(seg.u(), 4).addBox(x, half, depth + i * 0.02f, stripWidth, height, 0.5f);
        }
        return builder;
    }

    private static void addSide(PartDefinition root, Shape shape, String name, float s) {
        PartDefinition base = root.addOrReplaceChild(name + "_root", CubeListBuilder.create(),
                PartPose.offset(3f * s, 1.5f, 2.4f));
        PartDefinition shoulder = base.addOrReplaceChild(name + "_shoulder",
                segment(shape.shoulder(), s, -0.25f), PartPose.ZERO);
        PartDefinition elbow = shoulder.addOrReplaceChild(name + "_elbow",
                segment(shape.elbow(), s, 0.05f), PartPose.offset(shape.shoulder().length() * s, 0f, 0f));
        elbow.addOrReplaceChild(name + "_tip",
                segment(shape.tip(), s, 0.3f), PartPose.offset(shape.elbow().length() * s, 0f, 0f));
    }

    public void setup(WingPose pose) {
        apply(left, pose);
        apply(right, pose);
    }

    private static void apply(Side side, WingPose p) {
        float s = side.sign;
        float folded = 1f - Math.min(1f, p.spread);
        float dorsal = p.axis;
        float vertical = 1f - dorsal;
        float forward = p.sweep - p.curl;
        side.root.yRot = s * HALF_PI * dorsal * folded;
        side.shoulder.yRot = s * (-1.25f * folded + vertical * forward - dorsal * p.stroke);
        side.shoulder.zRot = s * (-0.1f * folded - vertical * p.stroke - dorsal * forward);
        side.shoulder.xRot = p.twist;
        side.elbow.yRot = s * (2.95f * folded - dorsal * p.elbow);
        side.elbow.zRot = s * -(vertical * p.elbow);
        side.elbow.xRot = 0f;
        side.tip.yRot = s * (-2.85f * folded - dorsal * p.tip);
        side.tip.zRot = s * -(vertical * p.tip);
        side.tip.xRot = 0f;
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        root.render(poseStack, consumer, light, overlay, color);
    }
}
