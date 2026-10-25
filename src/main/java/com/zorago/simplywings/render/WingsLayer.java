package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.anim.WingPose;
import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.client.WingVisuals;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.render.rig.WingRig;
import com.zorago.simplywings.render.rig.WingRigs;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.phys.Vec3;
import com.zorago.simplywings.player.FlightMode;
import org.joml.Vector3f;

public class WingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation BASIC = SimplyWings.id("basic");
    private static final int GHOST_ALPHA = 0x40;
    private static final int GHOST_COLOR = GHOST_ALPHA << 24 | 0xFFFFFF;

    private final Map<String, WingModel> models;
    private final Map<ResourceLocation, TailModel> tails;
    private final WingPose pose = new WingPose();
    private final WingMotion motion = new WingMotion();
    private final Vector3f[] tips = new Vector3f[2];

    public WingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
                      Map<String, WingModel> models,
                      Map<ResourceLocation, TailModel> tails) {
        super(parent);
        this.models = models;
        this.tails = tails;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
                       float headPitch) {
        WingVisuals.Visual visual = WingVisuals.of(player);
        if (visual == null || visual.equipped.isEmpty()) {
            return;
        }
        boolean ghost = player.isInvisible();
        if (ghost && player != Minecraft.getInstance().player) {
            return;
        }
        Optional<WingDefinition> definition = visual.look().flatMap(id -> WingRegistries.get(player.registryAccess(), id));
        int color = definition.map(WingDefinition::color).orElse(0xFFA0A0A0);
        ResourceLocation modelId = definition.map(WingDefinition::model).orElse(BASIC);
        WingStyle style = WingStyle.of(modelId);
        poseStack.pushPose();
        getParentModel().body.translateAndRotate(poseStack);
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            poseStack.translate(0, 0, 0.0625);
        }
        WingRig rig = WingRigs.get(modelId);
        if (rig != null) {
            visual.rig.claim(rig.name());
            rig.playerArms(getParentModel().body, getParentModel().rightArm, getParentModel().leftArm);
            rig.setup(visual.animator.motion(partialTick, style, motion), visual.rig);
            rig.render(poseStack, buffer.getBuffer(rig.renderType(ghost)), rig.light(light), OverlayTexture.NO_OVERLAY,
                    ghost ? GHOST_COLOR : -1);
            if (visual.mode == FlightMode.TRAVEL && visual.has(FlightMode.FLAG_BRAKE) && rig.tips(poseStack, tips)) {
                Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                Vec3 body = player.getPosition(partialTick);
                boolean inWorld = true;
                Vec3[] offsets = new Vec3[2];
                for (int i = 0; i < 2; i++) {
                    if (tips[i] == null) {
                        inWorld = false;
                        break;
                    }
                    offsets[i] = camera.add(tips[i].x(), tips[i].y(), tips[i].z()).subtract(body);
                    inWorld &= offsets[i].lengthSqr() < 16.0;
                }
                if (inWorld) {
                    System.arraycopy(offsets, 0, visual.tipOffset, 0, 2);
                    visual.tipTick = player.tickCount;
                }
                tips[0] = null;
                tips[1] = null;
            }
            if (rig.glow() != null && !ghost) {
                rig.render(poseStack, buffer.getBuffer(RenderType.eyes(rig.glow())), LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY, -1);
            }
        }
        for (WingStyle.Slot slot : rig != null ? java.util.List.<WingStyle.Slot>of() : style.slots()) {
            WingModel model = models.get(slot.shape().name());
            if (model == null) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(slot.x() / 16f, slot.y() / 16f, slot.z() / 16f);
            model.setup(visual.animator.pose(partialTick, style, slot, pose));
            ResourceLocation texture = model.shape().texture();
            RenderType type = ghost ? RenderType.entityTranslucent(texture) : switch (style.render()) {
                case CUTOUT -> RenderType.entityCutoutNoCull(texture);
                case TRANSLUCENT -> RenderType.entityTranslucent(texture);
                case ETHEREAL -> RenderType.entityTranslucent(texture);
            };
            int packedLight = style.render() == WingStyle.Render.ETHEREAL ? LightTexture.FULL_BRIGHT : light;
            model.render(poseStack, buffer.getBuffer(type), packedLight, OverlayTexture.NO_OVERLAY, ghost ? ghostColor(color) : color);
            if (style.glow() != null && !ghost) {
                model.render(poseStack, buffer.getBuffer(RenderType.eyes(style.glow())), LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY, -1);
            }
            poseStack.popPose();
        }
        TailModel tail = rig == null ? tails.get(modelId) : null;
        if (tail != null) {
            tail.setup(visual.tail, partialTick);
            RenderType type = ghost ? RenderType.entityTranslucent(tail.spec().texture())
                    : RenderType.entityCutoutNoCull(tail.spec().texture());
            tail.render(poseStack, buffer.getBuffer(type), light, OverlayTexture.NO_OVERLAY, ghost ? GHOST_COLOR : -1);
        }
        poseStack.popPose();
    }

    private static int ghostColor(int color) {
        return GHOST_ALPHA << 24 | (color & 0xFFFFFF);
    }
}
