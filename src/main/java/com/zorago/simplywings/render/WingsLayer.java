package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingPose;
import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.client.WingVisuals;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class WingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation BASIC = SimplyWings.id("basic");

    private final Map<String, WingModel> models;
    private final Map<ResourceLocation, TailModel> tails;
    private final WingPose pose = new WingPose();

    public WingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
                      Map<String, WingModel> models, Map<ResourceLocation, TailModel> tails) {
        super(parent);
        this.models = models;
        this.tails = tails;
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
        ResourceLocation modelId = definition.map(WingDefinition::model).orElse(BASIC);
        WingStyle style = WingStyle.of(modelId);
        poseStack.pushPose();
        getParentModel().body.translateAndRotate(poseStack);
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            poseStack.translate(0, 0, 0.0625);
        }
        for (WingStyle.Slot slot : style.slots()) {
            WingModel model = models.get(slot.shape().name());
            if (model == null) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(slot.x() / 16f, slot.y() / 16f, slot.z() / 16f);
            model.setup(visual.animator.pose(partialTick, style, slot, pose));
            ResourceLocation texture = model.shape().texture();
            RenderType type = switch (style.render()) {
                case CUTOUT -> RenderType.entityCutoutNoCull(texture);
                case TRANSLUCENT -> RenderType.entityTranslucent(texture);
                case ETHEREAL -> RenderType.entityTranslucentEmissive(texture);
            };
            int packedLight = style.render() == WingStyle.Render.ETHEREAL ? LightTexture.FULL_BRIGHT : light;
            model.render(poseStack, buffer.getBuffer(type), packedLight, OverlayTexture.NO_OVERLAY, color);
            if (style.glow() != null) {
                model.render(poseStack, buffer.getBuffer(RenderType.eyes(style.glow())), LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY, -1);
            }
            poseStack.popPose();
        }
        TailModel tail = tails.get(modelId);
        if (tail != null) {
            tail.setup(visual.tail, partialTick);
            tail.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(tail.spec().texture())), light,
                    OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
    }
}
