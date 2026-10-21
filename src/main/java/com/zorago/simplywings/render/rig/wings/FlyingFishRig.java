package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;

public final class FlyingFishRig extends WingRig {
    private static final int FOLD = 0;
    private static final int FLOP = 1;
    private static final float[] KEYS = Cycle.beat(0.4f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.4f, 0.05f, 0.5f, 0.52f, -0.15f, -0.45f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.95f, 0.8f, 0.9f, 1f, 1f, 1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.25f, 0.1f, 0f, -0.2f, -0.05f);

    private final Bone[] shoulder = new Bone[2];
    private final Bone[] pectoral = new Bone[2];
    private final Bone[] pelvic = new Bone[2];
    private final List<List<Bone>> rays = new ArrayList<>();
    private final List<List<Bone>> pelvicRays = new ArrayList<>();

    public FlyingFishRig() {
        super("flying_fish");
    }

    @Override
    public Material material() {
        return Material.TRANSLUCENT;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        rays.clear();
        pelvicRays.clear();
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            pectoral[i] = bones.get(side[i] + "pectoral");
            pelvic[i] = bones.get(side[i] + "pelvic");
            List<Bone> r = new ArrayList<>();
            for (int k = 1; bones.has(side[i] + "ray_" + k); k++) {
                r.add(bones.get(side[i] + "ray_" + k));
            }
            rays.add(r);
            List<Bone> p = new ArrayList<>();
            for (int k = 1; bones.has(side[i] + "pelvic_" + k); k++) {
                p.add(bones.get(side[i] + "pelvic_" + k));
            }
            pelvicRays.add(p);
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) + 0.5f * m.w(WingMotion.TUCK) + 0.3f * m.w(WingMotion.AIR)
                + 0.4f * m.diveDepth * m.w(WingMotion.DIVE);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.22f, 0.64f);
        boolean flopping = m.rouse > 0f && m.w(WingMotion.GROUND) > 0.5f;
        memory.spring(FLOP, flopping ? 1f : 0f, 0.35f, 0.5f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float flop = Mth.clamp(memory.get(FLOP, m.partialTick), 0f, 1.2f);
        float open = 1f - fold;
        float stroke = 0.05f * m.w(WingMotion.CRUISE) + 0.3f * m.w(WingMotion.LANDING) + 0.3f * m.w(WingMotion.BRAKE)
                + 0.15f * m.w(WingMotion.HOVER) + 0.4f * m.w(WingMotion.CHARGE) + flop * 0.35f * Mth.sin(time * 1.9f);
        float sweep = -1.2f * fold + 0.25f * m.w(WingMotion.BRAKE) - 0.3f * m.w(WingMotion.CRUISE) * 0.3f + 0.9f * flop * fold;
        float twist = -0.6f * m.w(WingMotion.BRAKE) - 0.3f * m.w(WingMotion.LANDING);
        float fan = Mth.lerp(fold, 1f, 0.18f) + 0.5f * flop * fold;
        if (m.beating()) {
            float t = m.beat;
            stroke += m.beatWeight * STROKE.at(t);
            fan *= Mth.lerp(m.beatWeight, 1f, FAN.at(t));
            twist += m.beatWeight * TWIST.at(t);
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            stroke += hw * 0.5f * Mth.cos(ph);
            twist += hw * 0.45f * Mth.sin(ph);
            fan *= 1f - hw * 0.12f * Math.max(0f, -Mth.sin(ph));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            stroke += sw * 0.45f * Mth.sin(time * 1.7f);
            twist += sw * 0.5f * Mth.sin(time * 1.2f);
        }
        float glide = m.w(WingMotion.CRUISE) * open;
        float tremble = glide * 0.035f * Mth.sin(time * 5.3f) + glide * 0.02f * Mth.sin(time * 8.9f + 1f);
        twist += tremble;
        stroke += glide * 0.03f * Mth.sin(time * 0.7f);
        fan = Mth.clamp(fan, 0.1f, 1f);
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(twist, s * stroke - m.bank * 0.3f, s * sweep);
            List<Bone> r = rays.get(i);
            for (int k = 0; k < r.size(); k++) {
                Bone ray = r.get(k);
                float flutter = glide * 0.03f * Mth.sin(time * 6.1f - k * 0.9f);
                ray.rotation(ray.rest().xRot + flutter, ray.rest().yRot + s * flutter, ray.rest().zRot * fan);
            }
            pelvic[i].rotate(twist * 0.5f, s * stroke * 0.5f, s * (-0.9f * fold + 0.1f * m.w(WingMotion.BRAKE)));
            List<Bone> p = pelvicRays.get(i);
            for (Bone ray : p) {
                ray.rotation(ray.rest().xRot, ray.rest().yRot, ray.rest().zRot * Mth.lerp(fold, 1f, 0.25f));
            }
        }
    }
}
