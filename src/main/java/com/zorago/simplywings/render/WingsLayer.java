package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingPose;
import com.zorago.simplywings.client.WingVisuals;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class WingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation BASIC = SimplyWings.id("basic");

    private final Map<ResourceLocation, WingModel> models;
    private final WingPose pose = new WingPose();

    public WingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
                      Map<ResourceLocation, WingModel> models) {
        super(parent);
        this.models = models;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
                       float headPitch) {
        WingVisuals.Visual visual = WingVisuals.of(player);
        if (visual == null || visual.equipped.isEmpty() || player.isInvisible()) {
            return;
        }
        Optional<WingDefinition> definition = visual.equipped.flatMap(id -> WingRegistries.get(player.registryAccess(), id));
        int color = definition.map(WingDefinition::color).orElse(0xFFA0A0A0);
        WingModel model = models.getOrDefault(definition.map(WingDefinition::model).orElse(BASIC), models.get(BASIC));
        poseStack.pushPose();
        getParentModel().body.translateAndRotate(poseStack);
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            poseStack.translate(0, 0, 0.0625);
        }
        model.setup(visual.animator.pose(partialTick, pose));
        model.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(model.shape().texture())), light,
                OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
    }
}
