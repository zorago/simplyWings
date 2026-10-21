package com.zorago.simplywings.render.rig;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

public final class Bone {
    public static final Bone NONE = new Bone(null);

    private final ModelPart part;
    private final PartPose rest;

    Bone(ModelPart part) {
        this.part = part;
        this.rest = part == null ? PartPose.ZERO : part.storePose();
    }

    public boolean present() {
        return part != null;
    }

    public PartPose rest() {
        return rest;
    }

    public void reset() {
        if (part != null) {
            part.loadPose(rest);
            part.xScale = 1f;
            part.yScale = 1f;
            part.zScale = 1f;
            part.visible = true;
        }
    }

    public Bone rotate(float x, float y, float z) {
        if (part != null) {
            part.xRot += x;
            part.yRot += y;
            part.zRot += z;
        }
        return this;
    }

    public Bone rotation(float x, float y, float z) {
        if (part != null) {
            part.xRot = x;
            part.yRot = y;
            part.zRot = z;
        }
        return this;
    }

    public Bone move(float x, float y, float z) {
        if (part != null) {
            part.x += x;
            part.y += y;
            part.z += z;
        }
        return this;
    }

    public Bone scale(float x, float y, float z) {
        if (part != null) {
            part.xScale *= x;
            part.yScale *= y;
            part.zScale *= z;
        }
        return this;
    }

    public Bone visible(boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
        return this;
    }

    public float xRot() {
        return part == null ? 0f : part.xRot;
    }

    public float yRot() {
        return part == null ? 0f : part.yRot;
    }

    public float zRot() {
        return part == null ? 0f : part.zRot;
    }
}
