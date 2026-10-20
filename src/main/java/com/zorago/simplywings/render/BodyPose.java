package com.zorago.simplywings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zorago.simplywings.client.WingVisuals;
import com.zorago.simplywings.player.FlightMode;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

public final class BodyPose {
    private static final Set<Player> PUSHED = Collections.newSetFromMap(new IdentityHashMap<>());

    public static void onPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        WingVisuals.Visual visual = WingVisuals.of(player);
        if (visual == null || visual.mode != FlightMode.TRAVEL) {
            return;
        }
        float pt = event.getPartialTick();
        float yaw = Mth.rotLerp(pt, player.yBodyRotO, player.yBodyRot);
        float pitch = Mth.lerp(pt, player.xRotO, player.getXRot());
        float roll = visual.animator.bank(pt);
        float pivot = player.getBbHeight() * 0.5f;
        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        stack.translate(0, pivot, 0);
        stack.mulPose(Axis.YP.rotationDegrees(-yaw));
        stack.mulPose(Axis.XP.rotationDegrees(90f + pitch));
        stack.mulPose(Axis.YP.rotationDegrees(roll));
        stack.mulPose(Axis.YP.rotationDegrees(yaw));
        stack.translate(0, -pivot, 0);
        PUSHED.add(player);
    }

    public static void onPost(RenderPlayerEvent.Post event) {
        if (PUSHED.remove(event.getEntity())) {
            event.getPoseStack().popPose();
        }
    }

    private BodyPose() {
    }
}
