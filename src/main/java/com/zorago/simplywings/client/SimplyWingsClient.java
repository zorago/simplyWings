package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.client.gui.InventoryButton;
import com.zorago.simplywings.config.ClientConfig;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.anim.WingStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.zorago.simplywings.render.BodyPose;
import com.zorago.simplywings.render.WingModel;
import com.zorago.simplywings.render.WingsLayer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(value = SimplyWings.MOD_ID, dist = Dist.CLIENT)
public class SimplyWingsClient {
    public SimplyWingsClient(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(ModKeys::register);
        modBus.addListener(SimplyWingsClient::onLayerDefinitions);
        modBus.addListener(SimplyWingsClient::onAddLayers);
        modBus.addListener(SimplyWingsClient::onGuiLayers);

        IEventBus game = NeoForge.EVENT_BUS;
        game.addListener(ClientFlight::onMovementInput);
        game.addListener(FlightCamera::onComputeAngles);
        game.addListener(FlightCamera::onDistance);
        game.addListener(FlightCamera::onTurn);
        game.addListener(BodyPose::onPre);
        game.addListener(BodyPose::onPost);
        game.addListener(SimplyWingsClient::onPlayerTickPre);
        game.addListener(SimplyWingsClient::onPlayerTickPost);
        game.addListener(SimplyWingsClient::onLogout);
        game.addListener(InventoryButton::onInit);
        game.addListener(InventoryButton::onRender);
    }

    private static void onLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (WingModel.Shape shape : WingModel.SHAPES) {
            event.registerLayerDefinition(shape.layer(), () -> WingModel.createLayer(shape));
        }
    }

    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        Map<String, WingModel> models = new HashMap<>();
        for (WingModel.Shape shape : WingModel.SHAPES) {
            models.put(shape.name(), new WingModel(event.getEntityModels().bakeLayer(shape.layer()), shape));
        }
        for (PlayerSkin.Model skin : event.getSkins()) {
            EntityRenderer<? extends Player> renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new WingsLayer(playerRenderer, models));
            }
        }
    }

    private static void onGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, SimplyWings.id("stamina"), StaminaHud::render);
    }

    private static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof LocalPlayer player && player == Minecraft.getInstance().player) {
            ClientFlight.preTick(player);
        }
    }

    private static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            return;
        }
        if (player instanceof LocalPlayer local && local == Minecraft.getInstance().player) {
            ClientFlight.postTick(local);
        }
        WingVisuals.Visual visual = WingVisuals.of(player);
        if (visual == null || visual.equipped.isEmpty()) {
            return;
        }
        Optional<WingDefinition> definition = visual.equipped.flatMap(id -> WingRegistries.get(player.registryAccess(), id));
        float hoverRate = definition.map(WingDefinition::hoverRate).orElse(2.5f);
        WingStyle style = WingStyle.of(definition.map(WingDefinition::model).orElse(SimplyWings.id("basic")));
        if (player == Minecraft.getInstance().player) {
            visual.animator.setCharge(ClientFlight.takeoffProgress());
        }
        visual.animator.tick(player, visual.mode, visual.flags, style, hoverRate);
        if (visual.mode.flying()) {
            player.walkAnimation.setSpeed(0f);
        }
        if (visual.mode == FlightMode.TRAVEL && visual.has(FlightMode.FLAG_BRAKE)) {
            spawnTrails(player);
        }
        if (style.thrusters() && (visual.mode == FlightMode.STABILIZED || visual.has(FlightMode.FLAG_THRUST))) {
            spawnThrusters(player, visual.mode == FlightMode.TRAVEL);
        }
    }

    private static void spawnThrusters(Player player, boolean travel) {
        Minecraft mc = Minecraft.getInstance();
        if (!ClientConfig.PARTICLES.get()
                || mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(player.position()) > 48 * 48) {
            return;
        }
        double yaw = Math.toRadians(player.yBodyRot);
        Vec3 forward = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
        Vec3 side = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw)).scale(0.35);
        Vec3 exhaust = travel ? player.getLookAngle().scale(-0.25) : new Vec3(0, -0.25, 0);
        Vec3 center = travel
                ? player.position().add(0, player.getBbHeight() * 0.55, 0).subtract(player.getLookAngle().scale(0.3))
                : player.position().add(0, player.getBbHeight() * 0.7, 0).subtract(forward.scale(0.3));
        for (int sign = -1; sign <= 1; sign += 2) {
            Vec3 nozzle = center.add(side.scale(sign));
            player.level().addParticle(ParticleTypes.SMALL_FLAME, nozzle.x, nozzle.y, nozzle.z, exhaust.x, exhaust.y, exhaust.z);
            if (player.getRandom().nextInt(3) == 0) {
                player.level().addParticle(ParticleTypes.SMOKE, nozzle.x, nozzle.y, nozzle.z,
                        exhaust.x * 0.5, exhaust.y * 0.5, exhaust.z * 0.5);
            }
        }
    }

    private static void spawnTrails(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (!ClientConfig.PARTICLES.get()) {
            return;
        }
        Camera camera = mc.gameRenderer.getMainCamera();
        if (camera.getPosition().distanceToSqr(player.position()) > 48 * 48) {
            return;
        }
        Vec3 motion = player.position().subtract(player.xo, player.yo, player.zo);
        if (motion.lengthSqr() < 0.15 * 0.15) {
            return;
        }
        double yaw = Math.toRadians(player.yBodyRot);
        Vec3 side = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw)).scale(1.4);
        Vec3 center = player.position().add(0, player.getBbHeight() * 0.5, 0);
        for (int sign = -1; sign <= 1; sign += 2) {
            Vec3 tip = center.add(side.scale(sign));
            player.level().addParticle(ParticleTypes.CLOUD, tip.x, tip.y, tip.z, 0, 0, 0);
        }
    }

    private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientFlight.reset();
        ClientWings.reset();
        WingVisuals.clear();
    }
}
