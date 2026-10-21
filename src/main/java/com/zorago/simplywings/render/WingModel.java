package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingPose;
import com.zorago.simplywings.anim.WingStyle;
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

    public static final Shape PSYCHE_FORE = new Shape("psyche_fore", List.of(
            new Segment(15f, 0.4f, 3, 10f, 16f, 0)),
            64, 32);

    public static final Shape PSYCHE_HIND = new Shape("psyche_hind", List.of(
            new Segment(11f, 0.4f, 3, 13f, 9f, 0)),
            64, 32);

    public static final Shape DEMON = new Shape("demon", List.of(
            new Segment(12f, 1.6f, 3, 10f, 14f, 0),
            new Segment(14f, 1.3f, 4, 14f, 9f, 30),
            new Segment(13f, 1.1f, 4, 10f, 2f, 62)),
            128, 32);

    public static final Shape DAEDALUS = new Shape("daedalus", List.of(
            new Segment(11f, 1.8f, 3, 7f, 9f, 0),
            new Segment(11f, 1.5f, 3, 9f, 8f, 30),
            new Segment(9f, 1.2f, 3, 8f, 4f, 56)),
            128, 32);

    public static final Shape FIRE = new Shape("fire", List.of(
            new Segment(11f, 1f, 3, 9f, 13f, 0),
            new Segment(12f, 0.9f, 4, 13f, 10f, 26),
            new Segment(11f, 0.7f, 4, 10f, 4f, 54)),
            128, 32);

    public static final Shape WATER = new Shape("water", List.of(
            new Segment(11f, 1f, 3, 10f, 13f, 0),
            new Segment(11f, 0.9f, 3, 13f, 12f, 26),
            new Segment(10f, 0.8f, 4, 12f, 6f, 52)),
            128, 32);

    public static final Shape EARTH = new Shape("earth", List.of(
            new Segment(10f, 2.2f, 3, 8f, 11f, 0),
            new Segment(10f, 2f, 3, 11f, 9f, 28),
            new Segment(8f, 1.8f, 3, 9f, 5f, 56)),
            128, 32);

    public static final Shape AIR = new Shape("air", List.of(
            new Segment(12f, 0.6f, 3, 6f, 9f, 0),
            new Segment(13f, 0.5f, 4, 9f, 7f, 28),
            new Segment(13f, 0.4f, 4, 7f, 2f, 58)),
            128, 32);

    public static final Shape PTERO = new Shape("ptero", List.of(
            new Segment(8f, 0.9f, 2, 7f, 8f, 0),
            new Segment(10f, 0.8f, 3, 8f, 7f, 22),
            new Segment(16f, 0.6f, 4, 7f, 1f, 46)),
            128, 32);

    public static final Shape STORM = new Shape("storm", List.of(
            new Segment(12f, 0.6f, 4, 8f, 12f, 0),
            new Segment(12f, 0.5f, 4, 12f, 8f, 28),
            new Segment(12f, 0.4f, 4, 8f, 2f, 56)),
            128, 32);

    public static final Shape AURORA = new Shape("aurora", List.of(
            new Segment(13f, 0.4f, 3, 10f, 14f, 0),
            new Segment(13f, 0.4f, 3, 14f, 12f, 30),
            new Segment(12f, 0.3f, 4, 12f, 5f, 60)),
            128, 32);

    public static final Shape SHADOW = new Shape("shadow", List.of(
            new Segment(11f, 1f, 3, 9f, 12f, 0),
            new Segment(13f, 0.8f, 4, 12f, 9f, 28),
            new Segment(12f, 0.6f, 4, 9f, 2f, 58)),
            128, 32);

    public static final Shape NEBULA = new Shape("nebula", List.of(
            new Segment(12f, 0.8f, 3, 11f, 15f, 0),
            new Segment(12f, 0.7f, 3, 15f, 13f, 28),
            new Segment(11f, 0.6f, 4, 13f, 5f, 56)),
            128, 32);

    public static final Shape FOREST = new Shape("forest", List.of(
            new Segment(10f, 1f, 3, 9f, 12f, 0),
            new Segment(10f, 0.9f, 3, 12f, 10f, 24),
            new Segment(9f, 0.8f, 3, 10f, 4f, 48)),
            128, 32);

    public static final Shape CROW = new Shape("crow", List.of(
            new Segment(10f, 1f, 3, 9f, 12f, 0),
            new Segment(10f, 0.9f, 3, 12f, 14f, 24),
            new Segment(9f, 0.8f, 4, 14f, 10f, 48)),
            128, 32);

    public static final Shape OWL = new Shape("owl", List.of(
            new Segment(9f, 1.2f, 3, 11f, 14f, 0),
            new Segment(9f, 1f, 3, 14f, 15f, 23),
            new Segment(7f, 0.9f, 3, 15f, 11f, 45)),
            64, 32);

    public static final Shape GULL = new Shape("gull", List.of(
            new Segment(11f, 0.8f, 3, 7f, 9f, 0),
            new Segment(12f, 0.7f, 3, 9f, 8f, 26),
            new Segment(12f, 0.6f, 4, 8f, 4f, 54)),
            128, 32);

    public static final Shape HUMMINGBIRD = new Shape("hummingbird", List.of(
            new Segment(6f, 0.6f, 2, 4f, 5f, 0),
            new Segment(6f, 0.5f, 2, 5f, 5f, 16),
            new Segment(5f, 0.4f, 2, 5f, 3f, 31)),
            64, 32);

    public static final Shape EAGLE = new Shape("eagle", List.of(
            new Segment(11f, 1.3f, 3, 11f, 14f, 0),
            new Segment(11f, 1.1f, 3, 14f, 15f, 27),
            new Segment(9f, 1f, 4, 15f, 12f, 54)),
            128, 32);

    public static final Shape SWIFT = new Shape("swift", List.of(
            new Segment(6f, 0.7f, 2, 5f, 6f, 0),
            new Segment(9f, 0.6f, 3, 6f, 5f, 16),
            new Segment(12f, 0.5f, 4, 5f, 1f, 38)),
            128, 32);

    public static final Shape PIGEON = new Shape("pigeon", List.of(
            new Segment(9f, 1f, 3, 9f, 11f, 0),
            new Segment(9f, 0.9f, 3, 11f, 11f, 22),
            new Segment(8f, 0.8f, 3, 11f, 7f, 44)),
            64, 32);

    public static final Shape PEACOCK = new Shape("peacock", List.of(
            new Segment(8f, 1f, 3, 10f, 13f, 0),
            new Segment(8f, 0.9f, 3, 13f, 15f, 20),
            new Segment(7f, 0.8f, 3, 15f, 17f, 40)),
            64, 32);

    public static final Shape PARROT = new Shape("parrot", List.of(
            new Segment(8f, 0.9f, 3, 8f, 11f, 0),
            new Segment(9f, 0.8f, 3, 11f, 11f, 20),
            new Segment(8f, 0.7f, 3, 11f, 7f, 42)),
            64, 32);

    public static final Shape BEE = new Shape("bee", List.of(
            new Segment(9f, 0.4f, 1, 4f, 4f, 0)),
            64, 32);

    public static final Shape MOTH = new Shape("moth", List.of(
            new Segment(8f, 0.6f, 3, 10f, 14f, 0),
            new Segment(8f, 0.5f, 3, 14f, 10f, 20)),
            64, 32);

    public static final Shape BEETLE = new Shape("beetle", List.of(
            new Segment(12f, 0.4f, 3, 6f, 8f, 0)),
            64, 32);

    public static final Shape BEETLE_SHELL = new Shape("beetle_shell", List.of(
            new Segment(7f, 1.6f, 2, 6f, 6f, 0)),
            64, 32);

    public static final Shape SQUIRREL = new Shape("squirrel", List.of(
            new Segment(8f, 1f, 2, 12f, 14f, 0),
            new Segment(6f, 0.9f, 2, 14f, 10f, 20)),
            64, 32);

    public static final Shape FLYING_FISH = new Shape("flying_fish", List.of(
            new Segment(11f, 0.4f, 4, 5f, 7f, 0)),
            64, 32);

    public static final Shape PHANTOM = new Shape("phantom", List.of(
            new Segment(9f, 0.9f, 3, 8f, 10f, 0),
            new Segment(10f, 0.8f, 3, 10f, 7f, 22),
            new Segment(9f, 0.6f, 3, 7f, 2f, 46)),
            128, 32);

    public static final Shape AMETHYST = new Shape("amethyst", List.of(
            new Segment(8f, 1.2f, 2, 6f, 9f, 0),
            new Segment(8f, 1f, 3, 9f, 10f, 21),
            new Segment(7f, 0.8f, 3, 10f, 5f, 41)),
            64, 32);

    public static final Shape ORNITHOPTER = new Shape("ornithopter", List.of(
            new Segment(11f, 1.2f, 3, 10f, 12f, 0),
            new Segment(11f, 1f, 3, 12f, 11f, 27),
            new Segment(9f, 0.8f, 3, 11f, 6f, 53)),
            128, 32);

    public static final Shape KITE = new Shape("kite", List.of(
            new Segment(10f, 0.5f, 2, 12f, 14f, 0),
            new Segment(10f, 0.5f, 2, 14f, 8f, 23)),
            64, 32);

    public static final Shape COPPER = new Shape("copper", List.of(
            new Segment(10f, 1.6f, 3, 8f, 10f, 0),
            new Segment(10f, 1.4f, 3, 10f, 9f, 26),
            new Segment(8f, 1.2f, 3, 9f, 4f, 51)),
            128, 32);

    public static final Shape PATCHWORK = new Shape("patchwork", List.of(
            new Segment(9f, 1f, 3, 10f, 12f, 0),
            new Segment(9f, 0.9f, 3, 12f, 11f, 22),
            new Segment(8f, 0.8f, 3, 11f, 6f, 44)),
            64, 32);

    public static final Shape NIGHT_MAIN = new Shape("night_main", List.of(
            new Segment(10f, 1.4f, 3, 10f, 13f, 0),
            new Segment(11f, 1.2f, 4, 13f, 12f, 26),
            new Segment(10f, 1f, 4, 12f, 4f, 54)),
            128, 32);

    public static final Shape NIGHT_HIND = new Shape("night_hind", List.of(
            new Segment(7f, 1f, 2, 6f, 7f, 0),
            new Segment(6f, 0.8f, 2, 7f, 3f, 20)),
            64, 32);

    public static final Shape LIGHT_MAIN = new Shape("light_main", List.of(
            new Segment(10f, 1.4f, 3, 10f, 13f, 0),
            new Segment(11f, 1.2f, 4, 13f, 12f, 26),
            new Segment(10f, 1f, 4, 12f, 4f, 54)),
            128, 32);

    public static final Shape LIGHT_HIND = new Shape("light_hind", List.of(
            new Segment(7f, 1f, 2, 6f, 7f, 0),
            new Segment(6f, 0.8f, 2, 7f, 3f, 20)),
            64, 32);

    public static final List<Shape> SHAPES = List.of(BASIC, ANGEL, BAT, DRAGONFLY, PSYCHE_FORE, PSYCHE_HIND, DEMON, DAEDALUS,
            FIRE, WATER, EARTH, AIR, PTERO, STORM, AURORA, SHADOW, NEBULA, FOREST, NIGHT_MAIN, NIGHT_HIND, LIGHT_MAIN, LIGHT_HIND,
            CROW, OWL, GULL, HUMMINGBIRD, EAGLE, SWIFT, PIGEON, PEACOCK, PARROT, BEE, MOTH, BEETLE, BEETLE_SHELL, SQUIRREL, FLYING_FISH, PHANTOM, AMETHYST, ORNITHOPTER, KITE, COPPER, PATCHWORK);

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
        side.base.xScale = WingStyle.SCALE;
        side.base.yScale = WingStyle.SCALE;
        side.base.zScale = WingStyle.SCALE;
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
