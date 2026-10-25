package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class DaedalusRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int FOLD = 2;
    private static final int THRUST = 3;
    private static final int GEAR = 4;
    private static final int SWING = 5;
    private static final int[] IDLES = {64, 50, 44, 56, 84};
    private static final int PANELS = 4;
    private static final int FEATHERS = 3;
    private static final float FOLD_ROOT = -1.2f;
    private static final float FOLD_MID = 2.35f;
    private static final float FOLD_TIP = -2.25f;
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_spar_tip", "left_spar_tip"};
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/daedalus_glow.png");

    private final Side[] sides = new Side[2];
    private Bone pack;
    private Bone thrusterLeft;
    private Bone thrusterRight;
    private Bone flameLeft;
    private Bone flameRight;

    public DaedalusRig() {
        super("daedalus");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    private static final class Side {
        final float s;
        final Bone shoulder;
        final Bone root;
        final Bone mid;
        final Bone tip;
        final Bone gear;
        final Bone elbowGear;
        final Bone aileron;
        final Bone[] panel = new Bone[PANELS];
        final Bone[] feather = new Bone[FEATHERS];

        Side(Bones b, String p, float s) {
            this.s = s;
            shoulder = b.get(p + "shoulder");
            root = b.get(p + "spar_root");
            mid = b.get(p + "spar_mid");
            tip = b.get(p + "spar_tip");
            gear = b.get(p + "gear");
            elbowGear = b.get(p + "elbow_gear");
            aileron = b.get(p + "aileron");
            for (int k = 0; k < PANELS; k++) {
                panel[k] = b.get(p + "panel_" + (k + 1));
            }
            for (int k = 0; k < FEATHERS; k++) {
                feather[k] = b.get(p + "icarus_" + (k + 1));
            }
        }
    }

    @Override
    protected void onBind(Bones bones) {
        sides[0] = new Side(bones, "right_", 1f);
        sides[1] = new Side(bones, "left_", -1f);
        pack = bones.get("pack");
        thrusterLeft = bones.get("thruster_left");
        thrusterRight = bones.get("thruster_right");
        flameLeft = bones.get("flame_left");
        flameRight = bones.get("flame_right");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.2f, 0.65f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.4f, 0.5f);
        boolean unfold = m.rouse > 0f && (m.rouseVariant % 5 == 0 || m.rouseVariant % 5 == 4);
        float fold = m.w(WingMotion.GROUND) * (unfold ? 1f - m.rouse : 1f) + 0.85f * m.w(WingMotion.TUCK);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.16f, 0.62f);
        float thrust = Math.max(m.buzz, Math.max(0.7f * m.w(WingMotion.HOVER), m.w(WingMotion.CHARGE)));
        thrust = Math.max(thrust, 0.12f * (1f - m.w(WingMotion.GROUND)));
        memory.spring(THRUST, thrust, 0.3f, 0.6f);
        float gear = 0.02f + 0.25f * memory.now(THRUST) + (m.rouse > 0f && m.rouseVariant % 5 == 1 ? 0.5f * m.rouse : 0f);
        if (memory.now(GEAR) > Mth.TWO_PI) {
            memory.snap(GEAR, memory.now(GEAR) - Mth.TWO_PI);
        }
        memory.set(GEAR, memory.now(GEAR) + gear);
        float swing = Mth.clamp((m.speed - 12f) / 28f, 0f, 1f) * m.w(WingMotion.CRUISE) + m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.6f, 1f);
        memory.spring(SWING, swing, 0.08f, 0.75f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float fold = Mth.clamp(memory.get(FOLD, pt), 0f, 1f);
        float thrust = Mth.clamp(memory.get(THRUST, pt), 0f, 1.2f);
        float gearAngle = memory.get(GEAR, pt);
        float swing = memory.get(SWING, pt);
        float hover = m.w(WingMotion.HOVER);
        float brake = m.w(WingMotion.BRAKE);
        float cruise = m.w(WingMotion.CRUISE);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);

        float sweepTest = variant == 4 ? env * 0.6f * Mth.sin(p * Mth.TWO_PI * 2f) : 0f;
        float aileronTest = variant == 2 ? env : 0f;
        float nozzleTest = variant == 3 ? env : 0f;
        float sputter = variant == 1 ? env * Math.max(0f, Mth.sin(time * 1.7f)) : 0f;
        float charge = m.w(WingMotion.CHARGE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake - chute;
        float blast = 0f;
        if (m.launch >= 0f) {
            float lp = m.launch;
            blast = lp < 0.15f ? lp / 0.15f : (1f - lp) * (1f - lp) / 0.7225f;
        }
        float hum = thrust * 0.012f * Mth.sin(time * 9.1f) + settle * 0.06f * Mth.sin(time * 3.3f)
                + charge * charge * 0.035f * Mth.sin(time * 7.3f) + blast * 0.03f * Mth.sin(time * 11.1f);
        float lift = Mth.lerp(chute, 0.15f * hover + 0.05f * cruise + 0.25f * slowBrake + 0.35f * charge + 0.2f * m.w(WingMotion.AIR),
                CHUTE_STROKE);
        float sweep = Mth.lerp(chute, -0.75f * swing + 0.1f * hover + sweepTest + 0.6f * charge - 0.5f * blast, CHUTE_RAISE)
                + chute * 0.02f * Mth.sin(time * 4.7f);
        float twist = hover * FlightRules.HOVER_PLANE + chute * CHUTE_PITCH - 0.3f * slowBrake + m.stall * 0.3f * Mth.sin(time * 1.2f);
        float bank = m.bank;

        for (int i = 0; i < 2; i++) {
            Side sd = sides[i];
            float s = sd.s;
            float open = 1f - fold;
            sd.shoulder.rotate(twist * open, s * Math.max(FLOOR, lift * open + hum - s * bank * 0.15f), s * sweep * open);
            sd.root.rotate(0f, 0f, s * FOLD_ROOT * fold);
            sd.mid.rotate(0f, s * 0.04f * Mth.sin(time * 0.3f) * open, s * (FOLD_MID * fold - 0.1f * swing * open));
            sd.tip.rotate(0f, 0f, s * (FOLD_TIP * fold - 0.15f * swing * open));
            sd.gear.rotate(0f, 0f, s * gearAngle);
            sd.elbowGear.rotate(0f, 0f, -s * gearAngle * 1.6f);
            float roll = -s * bank * 0.9f;
            float ail = 0.5f * brake + roll + aileronTest * 0.7f * Mth.sin(time * 1.1f + i * Mth.PI) + settle * 0.3f * Mth.sin(time * 2.7f + i);
            sd.aileron.rotate(ail, 0f, 0f);
            for (int k = 0; k < PANELS; k++) {
                float seq = variant == 0 ? Mth.clamp(idle * (p * 5f - k), 0f, 1f) * Mth.clamp(idle * ((1f - p) * 5f - (3 - k) * 0.6f), 0f, 1f) : 0f;
                float extend = Math.max(open, seq);
                float air = k == 1 ? 0.9f * brake : k == 2 ? 0.5f * brake : 0f;
                float clatter = settle * 0.08f * Mth.sin(time * 5.1f + k * 1.7f);
                sd.panel[k].rotate(air + clatter, 0f, 0f).scale(1f, 0.35f + 0.65f * extend, 1f)
                        .move(s * 2f * (1f - extend) * (k + 1) * 0.5f, 0f, 0f);
            }
            for (int k = 0; k < FEATHERS; k++) {
                float flutter = (0.05f + 0.08f * cruise * Mth.clamp(m.speed / 30f, 0f, 1f)) * Mth.sin(time * 2.3f + k * 1.3f);
                sd.feather[k].rotate(flutter * 2f, 0f, s * (0.9f * fold + flutter));
            }
        }

        float pitchVector = -0.35f * Mth.clamp(m.climb / 15f, -1f, 1f) * (1f - hover) + 1.6f * brake - 0.7f * blast;
        float diff = bank * 0.5f;
        float circle = nozzleTest * 0.5f;
        thrusterRight.rotate(pitchVector + hum + circle * Mth.sin(time * 0.4f), 0f, diff + circle * Mth.cos(time * 0.4f));
        thrusterLeft.rotate(pitchVector + hum + circle * Mth.sin(time * 0.4f + 1f), 0f, -diff + circle * Mth.cos(time * 0.4f + 1f));
        float flicker = 1f + 0.12f * Mth.sin(time * 7.7f) + 0.08f * Mth.sin(time * 13.3f);
        float jet = Math.max(0f, thrust * flicker + 0.6f * sputter) * (1f + 1.6f * blast);
        float jetR = jet * (1f + 0.25f * Mth.clamp(-diff, 0f, 1f));
        float jetL = jet * (1f + 0.25f * Mth.clamp(diff, 0f, 1f));
        flameRight.visible(jetR > 0.04f).scale(0.7f + 0.3f * Math.min(1f, jetR), Math.max(0.05f, jetR), 0.7f + 0.3f * Math.min(1f, jetR));
        flameLeft.visible(jetL > 0.04f).scale(0.7f + 0.3f * Math.min(1f, jetL), Math.max(0.05f, jetL), 0.7f + 0.3f * Math.min(1f, jetL));
        pack.rotate(hum * 0.5f, 0f, 0f);
    }
}
