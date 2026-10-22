package com.zorago.simplywings.render.rig;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.render.RigLoader;
import com.zorago.simplywings.render.rig.wings.AirRig;
import com.zorago.simplywings.render.rig.wings.AmethystRig;
import com.zorago.simplywings.render.rig.wings.AngelRig;
import com.zorago.simplywings.render.rig.wings.AuroraRig;
import com.zorago.simplywings.render.rig.wings.BatRig;
import com.zorago.simplywings.render.rig.wings.BeeRig;
import com.zorago.simplywings.render.rig.wings.BeetleRig;
import com.zorago.simplywings.render.rig.wings.CommonRig;
import com.zorago.simplywings.render.rig.wings.CopperRig;
import com.zorago.simplywings.render.rig.wings.CrowRig;
import com.zorago.simplywings.render.rig.wings.DaedalusRig;
import com.zorago.simplywings.render.rig.wings.DemonRig;
import com.zorago.simplywings.render.rig.wings.DragonflyRig;
import com.zorago.simplywings.render.rig.wings.EagleRig;
import com.zorago.simplywings.render.rig.wings.EarthRig;
import com.zorago.simplywings.render.rig.wings.FireRig;
import com.zorago.simplywings.render.rig.wings.ForestRig;
import com.zorago.simplywings.render.rig.wings.FlyingFishRig;
import com.zorago.simplywings.render.rig.wings.GullRig;
import com.zorago.simplywings.render.rig.wings.HummingbirdRig;
import com.zorago.simplywings.render.rig.wings.KiteRig;
import com.zorago.simplywings.render.rig.wings.MothRig;
import com.zorago.simplywings.render.rig.wings.NebulaRig;
import com.zorago.simplywings.render.rig.wings.OrnithopterRig;
import com.zorago.simplywings.render.rig.wings.OwlRig;
import com.zorago.simplywings.render.rig.wings.ParrotRig;
import com.zorago.simplywings.render.rig.wings.PatchworkRig;
import com.zorago.simplywings.render.rig.wings.PeacockRig;
import com.zorago.simplywings.render.rig.wings.PhantomRig;
import com.zorago.simplywings.render.rig.wings.PigeonRig;
import com.zorago.simplywings.render.rig.wings.PsycheRig;
import com.zorago.simplywings.render.rig.wings.PteroRig;
import com.zorago.simplywings.render.rig.wings.SeraphRig;
import com.zorago.simplywings.render.rig.wings.ShadowRig;
import com.zorago.simplywings.render.rig.wings.SquirrelRig;
import com.zorago.simplywings.render.rig.wings.StormRig;
import com.zorago.simplywings.render.rig.wings.SwiftRig;
import com.zorago.simplywings.render.rig.wings.WaterRig;
import com.zorago.simplywings.render.rig.wings.YinYangRig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class WingRigs {
    private static final List<WingRig> ALL = List.of(
            new CommonRig(),
            new BatRig(),
            new CrowRig(),
            new OwlRig(),
            new GullRig(),
            new HummingbirdRig(),
            new EagleRig(),
            new SwiftRig(),
            new PigeonRig(),
            new PeacockRig(),
            new ParrotRig(),
            new BeeRig(),
            new MothRig(),
            new BeetleRig(),
            new SquirrelRig(),
            new FlyingFishRig(),
            new PhantomRig(),
            new AmethystRig(),
            new OrnithopterRig(),
            new KiteRig(),
            new CopperRig(),
            new PatchworkRig(),
            new DragonflyRig(),
            new PteroRig(),
            new FireRig(),
            new WaterRig(),
            new EarthRig(),
            new AirRig(),
            new StormRig(),
            new AuroraRig(),
            new ShadowRig(),
            new NebulaRig(),
            new ForestRig(),
            new AngelRig(),
            new DemonRig(),
            new SeraphRig(),
            new PsycheRig(),
            new DaedalusRig(),
            new YinYangRig());
    private static final Map<ResourceLocation, WingRig> BOUND = new HashMap<>();

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (WingRig rig : ALL) {
            event.registerLayerDefinition(rig.layer(), () -> RigLoader.load(rig.name())
                    .map(RigLoader.Rig::layer)
                    .orElseGet(() -> LayerDefinition.create(new MeshDefinition(), 64, 32)));
        }
    }

    public static void bake(EntityModelSet models) {
        BOUND.clear();
        for (WingRig rig : ALL) {
            ModelPart baked = models.bakeLayer(rig.layer());
            RigLoader.cached(rig.name()).ifPresent(loaded -> {
                if (rig.bind(baked, loaded.paths())) {
                    BOUND.put(SimplyWings.id(rig.name()), rig);
                } else {
                    SimplyWings.LOGGER.warn("Wing rig {} has no bones, falling back to the legacy model", rig.file());
                }
            });
        }
    }

    public static WingRig get(ResourceLocation model) {
        return BOUND.get(model);
    }

    private WingRigs() {
    }
}
