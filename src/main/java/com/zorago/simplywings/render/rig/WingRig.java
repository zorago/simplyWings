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
import org.joml.Vector3f;

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
    private Membrane membrane;

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

    public final boolean bind(ModelPart root, RigLoader.Rig loaded) {
        bones = new Bones(root, loaded.paths());
        if (bones.count() == 0) {
            return false;
        }
        membrane = loaded.membranes() == null ? null
                : Membrane.parse(loaded.membranes(), root, loaded.paths(), loaded.width(), loaded.height());
        onBind(bones);
        return true;
    }

    protected abstract void onBind(Bones bones);

    public void poseBody(net.minecraft.client.model.PlayerModel<?> model, WingMotion motion) {
    }

    public void playerArms(ModelPart body, ModelPart right, ModelPart left) {
    }

    public void playerHead(ModelPart body, ModelPart head) {
    }

    protected final ModelPart root() {
        return bones.root();
    }

    public String[] tipBones() {
        return new String[0];
    }

    public final boolean tips(PoseStack poseStack, Vector3f[] out) {
        String[] names = tipBones();
        if (names.length == 0 || bones == null) {
            return false;
        }
        Vector3f anchor = poseStack.last().pose().transformPosition(0f, 1.4f, 0f, new Vector3f());
        boolean[] found = new boolean[1];
        bones.root().visit(poseStack, (pose, path, index, cube) -> {
            for (int i = 0; i < names.length && i < out.length; i++) {
                if (path.endsWith("/" + names[i])) {
                    float y = (cube.minY + cube.maxY) / 32f;
                    float z = (cube.minZ + cube.maxZ) / 32f;
                    Vector3f a = pose.pose().transformPosition(cube.minX / 16f, y, z, new Vector3f());
                    Vector3f b = pose.pose().transformPosition(cube.maxX / 16f, y, z, new Vector3f());
                    out[i] = a.distanceSquared(anchor) > b.distanceSquared(anchor) ? a : b;
                    found[0] = true;
                }
            }
        });
        return found[0];
    }

    public int[] idleLengths() {
        return DEFAULT_IDLE;
    }

    public void tick(WingMotion motion, RigMemory memory) {
    }

    protected abstract void pose(WingMotion motion, RigMemory memory);

    public final void setup(WingMotion motion, RigMemory memory) {
        bones.reset();
        pose(motion, memory);
        if (membrane != null) {
            membrane.time(motion.time);
        }
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

    protected final Membrane membrane() {
        return membrane;
    }

    public final float membraneBillow() {
        return membrane == null ? 0f : membrane.billow();
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        bones.root().render(poseStack, consumer, light, overlay, color);
        if (membrane != null) {
            membrane.render(poseStack, consumer, light, overlay, color);
        }
    }
}
