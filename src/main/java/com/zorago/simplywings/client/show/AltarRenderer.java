package com.zorago.simplywings.client.show;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zorago.simplywings.gacha.AltarShows;
import com.zorago.simplywings.gacha.DraconicAltarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AltarRenderer implements BlockEntityRenderer<DraconicAltarBlockEntity> {
    public AltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(DraconicAltarBlockEntity altar, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (altar.getLevel() == null || ClientShows.active(altar.getBlockPos())) {
            return;
        }
        ShowModel model = ShowModel.get().orElse(null);
        ShowModel.Anim idle = model == null ? null : model.anims.get("idle");
        if (idle == null) {
            return;
        }
        int facing = AltarShows.facing(altar.getBlockState());
        ClientShows.markIdle(altar.getBlockPos(), facing);
        float clock = (altar.getLevel().getGameTime() + partialTick) / 20f;
        float time = clock % idle.length;
        poseStack.pushPose();
        poseStack.translate(0.5, -2, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90f * facing));
        poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        ShowRenderer.render(model, idle, time, clock, poseStack, buffers, i -> 0xFFFFFFFF);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(DraconicAltarBlockEntity altar) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 160;
    }

    @Override
    public boolean shouldRender(DraconicAltarBlockEntity altar, Vec3 camera) {
        return Vec3.atCenterOf(altar.getBlockPos()).closerThan(camera, getViewDistance());
    }

    @Override
    public AABB getRenderBoundingBox(DraconicAltarBlockEntity altar) {
        return new AABB(altar.getBlockPos()).inflate(9, 6, 9);
    }
}
