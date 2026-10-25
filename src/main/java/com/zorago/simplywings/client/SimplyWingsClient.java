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
import com.zorago.simplywings.render.rig.WingRig;
import com.zorago.simplywings.render.rig.WingRigs;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.TailModel;
import com.zorago.simplywings.render.WingModel;
import com.zorago.simplywings.render.WingsLayer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
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
    private static final WingMotion TICK_MOTION = new WingMotion();

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
        game.addListener(ItemLore::onTooltip);
        AssemblyTableClient.register(modBus, game);
        com.zorago.simplywings.client.studio.Studio.register(game);
    }

    private static void onLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (WingModel.Shape shape : WingModel.SHAPES) {
            event.registerLayerDefinition(shape.layer(), () -> WingModel.createLayer(shape));
        }
        for (TailModel.Spec spec : TailModel.SPECS) {
            event.registerLayerDefinition(spec.layer(), () -> TailModel.createLayer(spec));
        }
        WingRigs.registerLayers(event);
    }

    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        Map<String, WingModel> models = new HashMap<>();
        for (WingModel.Shape shape : WingModel.SHAPES) {
            models.put(shape.name(), new WingModel(event.getEntityModels().bakeLayer(shape.layer()), shape));
        }
        WingRigs.bake(event.getEntityModels());
        Map<ResourceLocation, TailModel> tails = new HashMap<>();
        for (TailModel.Spec spec : TailModel.SPECS) {
            tails.put(SimplyWings.id(spec.name()), new TailModel(event.getEntityModels().bakeLayer(spec.layer()), spec));
        }
        for (PlayerSkin.Model skin : event.getSkins()) {
            EntityRenderer<? extends Player> renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new WingsLayer(playerRenderer, models, tails));
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
        Optional<WingDefinition> look = visual.look().flatMap(id -> WingRegistries.get(player.registryAccess(), id));
        float hoverRate = look.map(WingDefinition::hoverRate).orElse(2.5f);
        ResourceLocation model = look.map(WingDefinition::model).orElse(SimplyWings.id("basic"));
        if (visual.has(FlightMode.FLAG_THRUST) && look.filter(d -> !d.continuous()).isPresent()) {
            visual.animator.beat(look.get().flapRate(), 2);
        }
        WingStyle style = WingStyle.of(model);
        if (player == Minecraft.getInstance().player) {
            visual.animator.setCharge(ClientFlight.takeoffProgress());
        }
        WingRig rig = WingRigs.get(model);
        visual.animator.setIdleLengths(rig != null ? rig.idleLengths() : new int[0]);
        visual.animator.tick(player, visual.mode, visual.flags, style, hoverRate);
        if (rig != null) {
            visual.rig.claim(rig.name());
            visual.rig.begin();
            rig.tick(visual.animator.motion(1f, style, TICK_MOTION), visual.rig);
        }
        if (visual.mode.flying()) {
            player.walkAnimation.setSpeed(0f);
        }
        if (visual.mode == FlightMode.TRAVEL && visual.has(FlightMode.FLAG_BRAKE)) {
            spawnTrails(player, visual);
        } else {
            visual.tipTrail[0] = null;
            visual.tipTrail[1] = null;
        }
        WingEffects.tick(player, style, visual.mode, visual.flags);
        if (TailModel.SPECS.stream().anyMatch(spec -> SimplyWings.id(spec.name()).equals(model))) {
            visual.tail.tick(player, visual.mode);
        }
        FuryEffects.tick(player, visual);
        FuryEffects.ghost(player);
        if (player == Minecraft.getInstance().player) {
            GhostView.tick(player);
        }
    }

    private static void spawnTrails(Player player, WingVisuals.Visual visual) {
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
        boolean fresh = player.tickCount - visual.tipTick <= 3;
        boolean slow = visual.has(FlightMode.FLAG_SLOW);
        double yaw = Math.toRadians(player.yBodyRot);
        Vec3 side = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw)).scale(1.6);
        Vec3 center = player.position().add(0, player.getBbHeight() * 0.9, 0);
        int count = slow ? 1 : 4;
        for (int i = 0; i < 2; i++) {
            Vec3 tip = fresh ? player.position().add(visual.tipOffset[i]) : center.add(side.scale(i == 0 ? 1 : -1));
            Vec3 from = visual.tipTrail[i] != null && visual.tipTrail[i].distanceToSqr(tip) < 36 ? visual.tipTrail[i] : tip;
            for (int k = 0; k < count; k++) {
                Vec3 p = from.lerp(tip, (k + 1) / (double) count);
                double jitter = 0.04;
                player.level().addParticle(ParticleTypes.CLOUD, p.x, p.y, p.z, (player.getRandom().nextDouble() - 0.5) * jitter,
                        (player.getRandom().nextDouble() - 0.5) * jitter, (player.getRandom().nextDouble() - 0.5) * jitter);
            }
            visual.tipTrail[i] = tip;
        }
    }

    private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientFlight.reset();
        ClientWings.reset();
        WingVisuals.clear();
        FuryEffects.clear();
        GhostView.clear();
    }
}
