package com.zorago.simplywings.render.rig;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.RigLoader;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class WingRig {
    public enum Material {
        CUTOUT,
        TRANSLUCENT,
        EMISSIVE
    }

    private static final int[] DEFAULT_IDLE = {26};

    private final String name;
    private final ResourceLocation texture;
    private Bones bones;

    protected WingRig(String name) {
        this.name = name;
        this.texture = SimplyWings.id("textures/entity/" + name + "_wings.png");
    }

    public final String name() {
        return name;
    }

    public final ModelLayerLocation layer() {
        return new ModelLayerLocation(SimplyWings.id(name + "_rig"), "main");
    }

    public final ResourceLocation file() {
        return RigLoader.file(name);
    }

    public ResourceLocation texture() {
        return texture;
    }

    public ResourceLocation glow() {
        return null;
    }

    public Material material() {
        return Material.CUTOUT;
    }

    public final boolean bind(ModelPart root, Map<String, List<String>> paths) {
        bones = new Bones(root, paths);
        if (bones.count() == 0) {
            return false;
        }
        onBind(bones);
        return true;
    }

    protected abstract void onBind(Bones bones);

    public int[] idleLengths() {
        return DEFAULT_IDLE;
    }

    public void tick(WingMotion motion, RigMemory memory) {
    }

    protected abstract void pose(WingMotion motion, RigMemory memory);

    public final void setup(WingMotion motion, RigMemory memory) {
        bones.reset();
        pose(motion, memory);
    }

    public RenderType renderType(boolean ghost) {
        if (ghost) {
            return RenderType.entityTranslucent(texture());
        }
        return switch (material()) {
            case CUTOUT -> RenderType.entityCutoutNoCull(texture());
            case TRANSLUCENT -> RenderType.entityTranslucent(texture());
            case EMISSIVE -> RenderType.entityTranslucent(texture());
        };
    }

    public int light(int light) {
        return material() == Material.EMISSIVE ? LightTexture.FULL_BRIGHT : light;
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        bones.root().render(poseStack, consumer, light, overlay, color);
    }
}
