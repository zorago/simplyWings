package com.zorago.simplywings.flight;

import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.data.FurySet;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.ModAttachments;
import com.zorago.simplywings.player.WingsData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.neoforged.neoforge.network.PacketDistributor;

public final class FuryServer {
    private static final int LIGHTNING_INTERVAL = 9;
    private static final int DASH_COOLDOWN_TICKS = 100;
    private static final int LATENCY_TICKS = 6;

    public static void onCharge(ServerPlayer player, Payloads.FuryCharge request) {
        WingsData data = ModAttachments.get(player);
        long now = player.level().getGameTime();
        if (request.charging()) {
            if (!FurySet.isFury(data.equipped()) || !data.mode().flying() || now < data.furyReadyTick()) {
                return;
            }
            data.setFuryChargeStart(now);
            broadcast(player, Payloads.FuryEvent.CHARGE_START);
        } else if (data.furyChargeStart() >= 0) {
            data.setFuryChargeStart(-1);
            broadcast(player, Payloads.FuryEvent.CHARGE_STOP);
        }
    }

    public static void onDash(ServerPlayer player) {
        WingsData data = ModAttachments.get(player);
        long start = data.furyChargeStart();
        data.setFuryChargeStart(-1);
        if (start < 0 || !FurySet.isFury(data.equipped()) || !data.mode().flying()) {
            broadcast(player, Payloads.FuryEvent.CHARGE_STOP);
            return;
        }
        long elapsed = player.level().getGameTime() - start;
        if (elapsed < FurySet.CHARGE_TICKS - LATENCY_TICKS
                || elapsed > FurySet.CHARGE_TICKS + FurySet.DASH_WINDOW_TICKS + LATENCY_TICKS) {
            broadcast(player, Payloads.FuryEvent.CHARGE_STOP);
            return;
        }
        data.setFuryReadyTick(player.level().getGameTime() + DASH_COOLDOWN_TICKS);
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, ServerConfig.FURY_INVISIBILITY_TICKS.get(), 0, false, false, true));
        broadcast(player, Payloads.FuryEvent.DASH);
    }

    static void tick(ServerPlayer player, WingsData data) {
        long start = data.furyChargeStart();
        if (start < 0) {
            return;
        }
        long now = player.level().getGameTime();
        if (!FurySet.isFury(data.equipped()) || !data.mode().flying()
                || now - start > FurySet.CHARGE_TICKS + FurySet.DASH_WINDOW_TICKS + 40) {
            data.setFuryChargeStart(-1);
            broadcast(player, Payloads.FuryEvent.CHARGE_STOP);
            return;
        }
        long elapsed = now - start;
        if (FurySet.isNight(data.equipped()) && elapsed > 4 && elapsed <= FurySet.CHARGE_TICKS
                && elapsed % LIGHTNING_INTERVAL == 0 && player.level() instanceof ServerLevel level) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                double angle = player.getRandom().nextDouble() * Math.PI * 2;
                double distance = 4 + player.getRandom().nextDouble() * 4;
                bolt.moveTo(player.getX() + Math.cos(angle) * distance, player.getY() - 1,
                        player.getZ() + Math.sin(angle) * distance);
                bolt.setVisualOnly(true);
                level.addFreshEntity(bolt);
            }
        }
    }

    private static void broadcast(ServerPlayer player, int kind) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new Payloads.FuryEvent(player.getId(), kind));
    }

    private FuryServer() {
    }
}
