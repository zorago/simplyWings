package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.TailAnimator;
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

public final class TailModel {
    private static final float[] LENGTHS = {5f, 5f, 4.5f, 4f, 4f, 3.5f};
    private static final float[] WIDTHS = {3f, 2.6f, 2.2f, 1.8f, 1.5f, 1.2f};
    private static final float[] DEPTHS = {2.4f, 2.1f, 1.8f, 1.5f, 1.2f, 1f};
    private static final int MID_FINS = 3;
    private static final int TIP_FINS = 5;

    public record Spec(String name, boolean redLeftFin) {
        public ModelLayerLocation layer() {
            return new ModelLayerLocation(SimplyWings.id(name + "_tail"), "main");
        }

        public ResourceLocation texture() {
            return SimplyWings.id("textures/entity/" + name + "_tail.png");
        }
    }

    public static final Spec NIGHT = new Spec("night", true);
    public static final Spec LIGHT = new Spec("light", false);
    public static final List<Spec> SPECS = List.of(NIGHT, LIGHT);

    private final ModelPart root;
    private final List<ModelPart> segments = new ArrayList<>();
    private final Spec spec;

    public TailModel(ModelPart root, Spec spec) {
        this.root = root;
        this.spec = spec;
        ModelPart parent = root;
        for (int i = 0; i < TailAnimator.SEGMENTS; i++) {
            parent = parent.getChild("tail" + i);
            segments.add(parent);
        }
    }

    public Spec spec() {
        return spec;
    }

    public static LayerDefinition createLayer(Spec spec) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parent = mesh.getRoot();
        float offset = 0f;
        for (int i = 0; i < TailAnimator.SEGMENTS; i++) {
            float w = WIDTHS[i];
            float d = DEPTHS[i];
            CubeListBuilder cubes = CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-w / 2f, 0f, -d / 2f, w, LENGTHS[i], d);
            if (i == MID_FINS) {
                cubes.texOffs(40, 0).addBox(w / 2f, 0.5f, -0.25f, 3f, 3f, 0.5f)
                        .texOffs(40, 0).addBox(-w / 2f - 3f, 0.5f, -0.25f, 3f, 3f, 0.5f);
            }
            if (i == TIP_FINS) {
                cubes.texOffs(40, 8).addBox(-w / 2f - 5f, 0f, -0.25f, 5f, 4.5f, 0.5f)
                        .texOffs(40, spec.redLeftFin() ? 18 : 8)
                        .addBox(w / 2f, 0f, -0.25f, 5f, 4.5f, 0.5f);
            }
            PartPose pose = i == 0 ? PartPose.offset(0f, 11f, 1.5f) : PartPose.offset(0f, offset, 0f);
            parent = parent.addOrReplaceChild("tail" + i, cubes, pose);
            offset = LENGTHS[i];
        }
        return LayerDefinition.create(mesh, 64, 32);
    }

    public void setup(TailAnimator animator, float partialTick) {
        for (int i = 0; i < segments.size(); i++) {
            ModelPart part = segments.get(i);
            part.xRot = animator.pitch(i, partialTick);
            part.yRot = 0f;
            part.zRot = animator.yaw(i, partialTick);
        }
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        root.render(poseStack, consumer, light, overlay, color);
    }
}
