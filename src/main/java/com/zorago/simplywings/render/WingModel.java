package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingPose;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public final class WingModel {
    private static final String[] SEGMENT_NAMES = {"shoulder", "elbow", "tip"};
    private static final float[] SEGMENT_DEPTHS = {-0.25f, 0.05f, 0.3f};
    private static final float HALF_PI = (float) (Math.PI / 2);

    public record Segment(float length, float bone, int strips, float plateFrom, float plateTo, int u) {
    }

    public record Shape(String name, List<Segment> segments, int textureWidth, int textureHeight) {
        public ModelLayerLocation layer() {
            return new ModelLayerLocation(SimplyWings.id(name + "_wings"), "main");
        }

        public ResourceLocation texture() {
            return SimplyWings.id("textures/entity/" + name + "_wings.png");
        }
    }

    public static final Shape BASIC = new Shape("basic", List.of(
            new Segment(9f, 1f, 1, 11f, 11f, 0),
            new Segment(8f, 1f, 1, 9f, 9f, 20),
            new Segment(6f, 0.8f, 1, 6f, 6f, 40)),
            64, 32);

    public static final Shape ANGEL = new Shape("angel", List.of(
            new Segment(13f, 1.4f, 3, 13f, 17f, 0),
            new Segment(12f, 1.2f, 3, 17f, 21f, 30),
            new Segment(11f, 1f, 4, 21f, 26f, 58)),
            128, 32);

    public static final Shape BAT = new Shape("bat", List.of(
            new Segment(9f, 0.8f, 2, 8f, 9f, 0),
            new Segment(10f, 0.7f, 3, 10f, 6f, 24),
            new Segment(10f, 0.6f, 4, 8f, 3f, 48)),
            128, 32);

    public static final Shape DRAGONFLY = new Shape("dragonfly", List.of(
            new Segment(17f, 0.5f, 1, 5f, 5f, 0)),
            64, 32);

    public static final List<Shape> SHAPES = List.of(BASIC, ANGEL, BAT, DRAGONFLY);

    private final ModelPart root;
    private final Side left;
    private final Side right;
    private final Shape shape;

    private record Side(ModelPart base, List<ModelPart> segments, float sign) {
    }

    public WingModel(ModelPart root, Shape shape) {
        this.root = root;
        this.shape = shape;
        this.left = side(root, shape, "left", 1f);
        this.right = side(root, shape, "right", -1f);
    }

    public Shape shape() {
        return shape;
    }

    private static Side side(ModelPart root, Shape shape, String name, float sign) {
        ModelPart base = root.getChild(name + "_root");
        List<ModelPart> parts = new ArrayList<>();
        ModelPart parent = base;
        for (int i = 0; i < shape.segments().size(); i++) {
            parent = parent.getChild(name + "_" + SEGMENT_NAMES[i]);
            parts.add(parent);
        }
        return new Side(base, parts, sign);
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
        PartDefinition parent = root.addOrReplaceChild(name + "_root", CubeListBuilder.create(),
                PartPose.offset(3f * s, 1.5f, 2.4f));
        float offset = 0f;
        for (int i = 0; i < shape.segments().size(); i++) {
            Segment seg = shape.segments().get(i);
            parent = parent.addOrReplaceChild(name + "_" + SEGMENT_NAMES[i], segment(seg, s, SEGMENT_DEPTHS[i]),
                    PartPose.offset(offset * s, 0f, 0f));
            offset = seg.length();
        }
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
        side.base.yRot = s * HALF_PI * dorsal * folded;
        List<ModelPart> parts = side.segments;
        ModelPart shoulder = parts.get(0);
        shoulder.yRot = s * (-1.25f * folded + vertical * forward - dorsal * p.stroke);
        shoulder.zRot = s * (-0.1f * folded - vertical * p.stroke - dorsal * forward);
        shoulder.xRot = p.twist;
        if (parts.size() > 1) {
            ModelPart elbow = parts.get(1);
            elbow.yRot = s * (2.95f * folded - dorsal * p.elbow);
            elbow.zRot = s * -(vertical * p.elbow);
            elbow.xRot = 0f;
        }
        if (parts.size() > 2) {
            ModelPart tip = parts.get(2);
            tip.yRot = s * (-2.85f * folded - dorsal * p.tip);
            tip.zRot = s * -(vertical * p.tip);
            tip.xRot = 0f;
        }
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        root.render(poseStack, consumer, light, overlay, color);
    }
}
