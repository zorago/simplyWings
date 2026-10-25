package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;

public final class FlyingFishRig extends WingRig {
    private static final int FOLD = 0;
    private static final int FLOP = 1;
    private static final float[] KEYS = Cycle.beat(0.4f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.04f, 0.35f, 0.62f, 0.6f, 0.2f, -0.06f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.15f, 0.05f, 0.18f, 0.16f, 0f, -0.18f);
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_ray_1", "left_ray_1"};
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
    public String[] tipBones() {
        return TIPS;
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
        float charge = m.w(WingMotion.CHARGE);
        float hover = m.w(WingMotion.HOVER);
        float chute = FlightRules.chute(m);
        float slowBrake = m.w(WingMotion.BRAKE) - chute;
        float warm = charge * charge;
        float stroke = 0.05f * m.w(WingMotion.CRUISE) + 0.3f * m.w(WingMotion.LANDING) + 0.25f * slowBrake
                + 0.15f * hover + 0.3f * charge + flop * 0.35f * Mth.sin(time * 1.9f) + warm * 0.035f * Mth.sin(time * 2.9f);
        float sweep = -1.2f * fold - 0.09f * m.w(WingMotion.CRUISE) + 0.15f * slowBrake + 0.6f * charge + 0.12f * hover
                + 0.9f * flop * fold - 0.35f * m.w(WingMotion.DIVE) * m.diveDepth + warm * 0.03f * Mth.sin(time * 3.7f);
        float twist = -0.3f * m.w(WingMotion.LANDING) + hover * FlightRules.HOVER_PLANE;
        float fan = Mth.lerp(fold, 1f, 0.18f) + 0.5f * flop * fold;
        float camber = 0f;
        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            float strike = Mth.clamp(p / 0.3f, 0f, 1f);
            strike = strike * strike * (3f - 2f * strike);
            blast = p < 0.3f ? 1f : 1f - (p - 0.3f) / 0.7f;
            stroke += blast * Mth.lerp(strike, 0.7f, -0.05f - stroke);
            sweep += blast * Mth.lerp(strike, 0.3f, -0.2f);
            camber += blast * 0.4f * Mth.sin((float) Math.PI * strike);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight * (1f - blast);
            stroke += a * STROKE.at(t);
            sweep += a * SWEEP.at(t);
            fan *= Mth.lerp(a, 1f, FAN.at(t));
            twist += a * TWIST.at(t);
            camber += a * 0.6f * STROKE.lagged(t, 0.06f);
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            sweep += hw * (0.2f + 0.42f * Mth.cos(ph));
            stroke += hw * 0.1f * Mth.sin(2f * ph);
            twist += hw * 0.45f * Mth.sin(ph);
            fan *= 1f - hw * 0.12f * Math.max(0f, -Mth.sin(ph));
            camber += hw * 0.3f * Mth.sin(ph);
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            stroke += sw * 0.25f * (1f + Mth.sin(time * 1.7f));
            twist += sw * 0.5f * Mth.sin(time * 1.2f);
        }
        float glide = m.w(WingMotion.CRUISE) * open;
        twist += glide * 0.035f * Mth.sin(time * 5.3f) + glide * 0.02f * Mth.sin(time * 8.9f + 1f);
        stroke += glide * 0.03f * Mth.sin(time * 0.7f);
        float tremble = chute * 0.02f * Mth.sin(time * 4.7f);
        stroke = Mth.lerp(chute, stroke, CHUTE_STROKE);
        sweep = Mth.lerp(chute, sweep, CHUTE_RAISE + tremble);
        twist = Mth.lerp(chute, twist, CHUTE_PITCH + 0.03f * Mth.sin(time * 5.3f));
        fan = Mth.lerp(chute, Mth.clamp(fan, 0.1f, 1f), 1f);
        float airborne = Mth.clamp(1f - m.w(WingMotion.GROUND) - charge, 0f, 1f);
        Membrane skin = membrane();
        if (skin != null) {
            skin.billow(Mth.clamp(open * (0.35f * airborne + 0.9f * chute + 0.25f * warm + camber), -0.6f, 1.1f));
        }
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(twist, s * Math.max(FLOOR, stroke - s * m.bank * 0.3f), s * sweep);
            List<Bone> r = rays.get(i);
            for (int k = 0; k < r.size(); k++) {
                Bone ray = r.get(k);
                float flutter = glide * 0.03f * Mth.sin(time * 6.1f - k * 0.9f) + chute * 0.02f * Mth.sin(time * 7.3f - k);
                ray.rotation(ray.rest().xRot + flutter, ray.rest().yRot + s * flutter, ray.rest().zRot * fan);
            }
            pelvic[i].rotate(twist * 0.5f, s * Math.max(FLOOR, stroke * 0.5f), s * (-0.9f * fold + 0.1f * m.w(WingMotion.BRAKE)));
            List<Bone> p = pelvicRays.get(i);
            for (Bone ray : p) {
                ray.rotation(ray.rest().xRot, ray.rest().yRot, ray.rest().zRot * Mth.lerp(fold, 1f, 0.25f));
            }
        }
    }
}
