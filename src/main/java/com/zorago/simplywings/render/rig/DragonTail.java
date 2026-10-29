package com.zorago.simplywings.render.rig;

import com.zorago.simplywings.anim.RigMemory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;

public final class DragonTail {
    public final List<Bone> segment = new ArrayList<>();
    public final Bone midRight;
    public final Bone midLeft;
    public final Bone tipRight;
    public final Bone tipLeft;
    private static final float SWING_LAG = 0.45f;
    private final int slot;

    public DragonTail(Bones b, int slot, int mid, int tip) {
        this.slot = slot;
        for (int k = 1; b.has("tail_" + k); k++) {
            segment.add(b.get("tail_" + k));
        }
        midRight = b.get("tail_" + (mid + 1) + "_fin_right");
        midLeft = b.get("tail_" + (mid + 1) + "_fin_left");
        tipRight = b.get("tail_" + (tip + 1) + "_fin_right");
        tipLeft = b.get("tail_" + (tip + 1) + "_fin_left");
    }

    public int slots() {
        return segment.size() * 2 + 2;
    }

    private int phaseSlot() {
        return slot + segment.size() * 2;
    }

    public void swing(RigMemory memory, float gait, float run) {
        int phase = phaseSlot();
        float next = memory.now(phase) + 0.17f + 0.09f * run;
        if (next > Mth.TWO_PI) {
            memory.snap(phase, memory.now(phase) - Mth.TWO_PI);
            next -= Mth.TWO_PI;
        }
        memory.set(phase, next);
        memory.ease(phase + 1, gait * (1f + 0.6f * run), 0.08f);
    }

    public void follow(RigMemory memory, float[] pitch, float[] yaw, float stiffness, float damping) {
        for (int k = 0; k < segment.size(); k++) {
            float lag = 1f - 0.06f * k;
            memory.spring(slot + 2 * k, pitch[k], stiffness * lag, damping);
            memory.spring(slot + 2 * k + 1, yaw[k], stiffness * lag, damping);
        }
    }

    public void apply(RigMemory memory, float partialTick, float[] extraPitch, float[] extraYaw) {
        float phase = memory.get(phaseSlot(), partialTick);
        float amplitude = memory.get(phaseSlot() + 1, partialTick);
        for (int k = 0; k < segment.size(); k++) {
            float p = memory.get(slot + 2 * k, partialTick) + (extraPitch == null ? 0f : extraPitch[k]);
            float y = memory.get(slot + 2 * k + 1, partialTick) + (extraYaw == null ? 0f : extraYaw[k])
                    + amplitude * (0.055f + 0.02f * k) * Mth.sin(phase - k * SWING_LAG);
            segment.get(k).rotate(p, y * Mth.sin(p), -y * Mth.cos(p));
        }
    }

    public void fins(float midOpen, float tipRightOpen, float tipLeftOpen, float flutter) {
        midRight.rotate(0f, 0f, -midOpen - flutter * 0.5f);
        midLeft.rotate(0f, 0f, midOpen + flutter * 0.5f);
        tipRight.rotate(flutter, 0f, -tipRightOpen);
        tipLeft.rotate(-flutter, 0f, tipLeftOpen);
    }
}
