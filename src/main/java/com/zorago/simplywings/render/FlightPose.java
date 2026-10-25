package com.zorago.simplywings.render;

import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.client.WingVisuals;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.player.FlightMode;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class FlightPose {
    private static final ResourceLocation BASIC = ResourceLocation.fromNamespaceAndPath("simplywings", "basic");
    private static final WingMotion MOTION = new WingMotion();

    public static void apply(PlayerModel<?> model, LivingEntity entity, float ageInTicks) {
        if (!(entity instanceof Player player)) {
            return;
        }
        WingVisuals.Visual visual = WingVisuals.of(player);
        if (visual == null || visual.equipped.isEmpty() || visual.mode == FlightMode.NONE) {
            return;
        }
        float partialTick = ageInTicks - Mth.floor(ageInTicks);
        ResourceLocation modelId = visual.look().flatMap(id -> WingRegistries.get(player.registryAccess(), id))
                .map(WingDefinition::model).orElse(BASIC);
        WingMotion motion = visual.animator.motion(partialTick, WingStyle.of(modelId), MOTION);
        pose(model, visual.mode, motion, visual.animator.bank(partialTick), ageInTicks);
    }

    public static void pose(PlayerModel<?> model, FlightMode mode, WingMotion motion, float bankDegrees, float ageInTicks) {
        float t = ageInTicks * 0.05f;
        if (mode == FlightMode.TRAVEL) {
            travel(model, motion, bankDegrees, t);
        } else {
            hover(model, motion, t);
        }
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.hat.copyFrom(model.head);
    }

    public static float brake(Player player, WingVisuals.Visual visual, float partialTick) {
        ResourceLocation modelId = visual.look().flatMap(id -> WingRegistries.get(player.registryAccess(), id))
                .map(WingDefinition::model).orElse(BASIC);
        return Mth.clamp(visual.animator.motion(partialTick, WingStyle.of(modelId), MOTION).w(WingMotion.BRAKE), 0, 1);
    }

    private static void travel(PlayerModel<?> model, WingMotion motion, float bankDegrees, float t) {
        float brake = Mth.clamp(motion.w(WingMotion.BRAKE), 0, 1);
        float dive = Mth.clamp(motion.w(WingMotion.DIVE) + motion.w(WingMotion.TUCK), 0, 1);
        float bank = Mth.clamp(bankDegrees / 45f, -1, 1);
        float beat = motion.beating() ? Mth.sin(motion.beat * Mth.TWO_PI) * motion.beatWeight : 0;
        float sway = Mth.sin(t * 6.0f);

        float slow = brake * Mth.clamp(motion.brakeBeat, 0, 1);
        float balance = Mth.sin(t * 5.0f) * (brake - slow);
        float armX = Mth.lerp(slow, Mth.lerp(brake, 0.05f + 0.06f * beat, 0.42f), -0.08f);
        float armY = 0.05f * (brake - slow);
        float armZ = Mth.lerp(slow, Mth.lerp(brake, 0.14f + 0.08f * beat + 0.03f * sway, 1.35f), 0.12f) * (1 - 0.8f * dive);
        set(model.rightArm, armX, armY, armZ - bank * 0.25f + 0.06f * balance);
        set(model.leftArm, armX, -armY, -armZ - bank * 0.25f + 0.06f * balance);

        float kick = 0.08f * Mth.sin(t * 9.0f) * (1 - brake) * (1 - dive);
        float legX = Mth.lerp(brake, 0.04f, -0.15f);
        float legZ = Mth.lerp(brake, 0.04f, 0.12f) * (1 - dive);
        set(model.rightLeg, legX + kick + 0.04f * balance, 0, legZ - bank * 0.12f);
        set(model.leftLeg, legX - kick + Mth.lerp(brake, 0, 0.35f) - 0.04f * balance, 0, -legZ - bank * 0.12f);

        model.head.xRot = Mth.lerp(dive, Mth.lerp(brake, -1.25f, 0.3f), -0.95f);
        model.head.zRot = -bank * 0.2f;
    }

    private static void hover(PlayerModel<?> model, WingMotion motion, float t) {
        float bob = Mth.sin(motion.hoverPhase);
        float drift = Mth.sin(t * 4.0f);
        set(model.rightArm, -0.12f + 0.05f * bob, 0, 0.32f + 0.06f * bob);
        set(model.leftArm, -0.12f + 0.05f * bob, 0, -0.32f - 0.06f * bob);
        set(model.rightLeg, 0.12f + 0.08f * drift, 0, 0.06f);
        set(model.leftLeg, 0.05f - 0.08f * drift, 0, -0.06f);
    }

    private static void set(ModelPart part, float x, float y, float z) {
        part.xRot = x;
        part.yRot = y;
        part.zRot = z;
    }

    private FlightPose() {
    }
}
