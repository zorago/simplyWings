package com.zorago.simplywings.mixin;

import com.zorago.simplywings.render.FlightPose;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 1500)
public abstract class PlayerModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void simplywings$flightPose(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                        float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (ageInTicks == 0f && limbSwing == 0f && limbSwingAmount == 0f && netHeadYaw == 0f && headPitch == 0f) {
            return;
        }
        FlightPose.apply((PlayerModel<?>) (Object) this, entity, ageInTicks);
    }
}
