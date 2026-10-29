package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.gacha.OfferingFountainBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

public class FountainRenderer implements BlockEntityRenderer<OfferingFountainBlockEntity> {
    private static final ResourceLocation TEXTURE = SimplyWings.id("textures/block/offering_fountain.png");

    private ModelPart root;
    private ModelPart water;
    private ModelPart crystal;

    public FountainRenderer(BlockEntityRendererProvider.Context context) {
    }

    private boolean ensure() {
        if (root == null) {
            RigLoader.cached("offering_fountain").ifPresent(rig -> {
                root = rig.layer().bakeRoot();
                water = root.hasChild("water") ? root.getChild("water") : null;
                crystal = root.hasChild("crystal") ? root.getChild("crystal") : null;
            });
        }
        return root != null;
    }

    @Override
    public void render(OfferingFountainBlockEntity fountain, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        if (!ensure()) {
            return;
        }
        root.getAllParts().forEach(ModelPart::resetPose);
        float time = (fountain.getLevel() == null ? 0 : fountain.getLevel().getGameTime()) + partialTick;
        if (crystal != null) {
            crystal.yRot = time * 0.02F;
            crystal.y -= 0.6F * Mth.sin(time * 0.05F);
        }
        pose.pushPose();
        pose.translate(0.5, 1.5, 0.5);
        pose.scale(-1.0F, -1.0F, 1.0F);
        if (water != null) {
            water.visible = false;
        }
        if (crystal != null) {
            crystal.visible = false;
        }
        root.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay);
        if (crystal != null) {
            crystal.visible = true;
            crystal.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), LightTexture.FULL_BRIGHT, overlay);
        }
        if (water != null) {
            water.visible = true;
            water.render(pose, buffers.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, overlay);
        }
        pose.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(OfferingFountainBlockEntity fountain) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public AABB getRenderBoundingBox(OfferingFountainBlockEntity fountain) {
        return fountain.renderBounds();
    }
}
