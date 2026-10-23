package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.CloudCompressorBlockEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

public class CloudCompressorRenderer implements BlockEntityRenderer<CloudCompressorBlockEntity> {
    private static final ResourceLocation TEXTURE = SimplyWings.id("textures/block/cloud_compressor.png");
    private static final String[] SPINNING = {"gear_left", "gear_right", "gear_small", "flywheel"};

    private ModelPart root;
    private final Map<String, ModelPart> parts = new HashMap<>();

    public CloudCompressorRenderer(BlockEntityRendererProvider.Context context) {
    }

    private boolean ensure() {
        if (root == null) {
            RigLoader.cached("cloud_compressor").ifPresent(rig -> {
                root = rig.layer().bakeRoot();
                for (Map.Entry<String, List<String>> entry : rig.paths().entrySet()) {
                    ModelPart part = root;
                    for (String name : entry.getValue()) {
                        part = part.hasChild(name) ? part.getChild(name) : null;
                        if (part == null) {
                            break;
                        }
                    }
                    if (part != null) {
                        parts.put(entry.getKey(), part);
                    }
                }
            });
        }
        return root != null;
    }

    private ModelPart part(String name) {
        return parts.get(name);
    }

    @Override
    public void render(CloudCompressorBlockEntity compressor, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        if (!ensure()) {
            return;
        }
        root.getAllParts().forEach(ModelPart::resetPose);
        float spin = compressor.spin(partialTick) * Mth.DEG_TO_RAD;
        float speed = compressor.speed();
        rotate("gear_left", spin, 0, 0);
        rotate("gear_right", -spin * 1.5F, 0, 0);
        rotate("gear_small", spin * 2.2F, 0, 0);
        rotate("flywheel", 0, 0, spin * 0.8F);
        move("piston_a", -Math.abs(Mth.sin(spin)) * 3.0F * speed);
        move("piston_b", -Math.abs(Mth.cos(spin)) * 3.0F * speed);
        rotate("needle", 0, 0, -0.9F + speed * (1.6F + 0.08F * Mth.sin(spin * 3)));
        rotate("valve", 0, 0, spin * 0.25F);
        ModelPart liquid = part("liquid");
        ModelPart glass = part("glass");
        if (liquid != null) {
            liquid.yScale = 0.92F + 0.04F * Mth.sin(spin * 0.7F) * speed;
            liquid.visible = false;
        }
        if (glass != null) {
            glass.visible = false;
        }
        pose.pushPose();
        pose.translate(0.5, 1.5, 0.5);
        pose.scale(-1.0F, -1.0F, 1.0F);
        root.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay);
        if (liquid != null) {
            liquid.visible = true;
            liquid.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), LightTexture.FULL_BRIGHT, overlay);
        }
        if (glass != null) {
            glass.visible = true;
            glass.render(pose, buffers.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, overlay);
        }
        pose.popPose();
    }

    private void rotate(String name, float x, float y, float z) {
        ModelPart part = part(name);
        if (part != null) {
            part.xRot += x;
            part.yRot += y;
            part.zRot += z;
        }
    }

    private void move(String name, float dy) {
        ModelPart part = part(name);
        if (part != null) {
            part.y += dy;
        }
    }

    @Override
    public boolean shouldRenderOffScreen(CloudCompressorBlockEntity compressor) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public AABB getRenderBoundingBox(CloudCompressorBlockEntity compressor) {
        return compressor.renderBox();
    }
}
