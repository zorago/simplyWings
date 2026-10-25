package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class DragonflyRig extends WingRig {
    private static final int BUZZ = 0;
    private static final int SPREAD = 1;
    private static final int IDLE = 2;
    private static final int SETTLE = 3;
    private static final int[] IDLES = {30, 44, 40};
    private static final float VISUAL_RATE = 2.9f;
    private static final float FORE_REST = -18f * Mth.DEG_TO_RAD;
    private static final float HIND_REST = -40f * Mth.DEG_TO_RAD;
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_fore_ghost_a", "left_fore_ghost_a"};

    private final Bone[] fore = new Bone[2];
    private final Bone[] hind = new Bone[2];
    private final Bone[][] ghosts = new Bone[2][4];

    public DragonflyRig() {
        super("dragonfly");
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
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            fore[i] = bones.get(side[i] + "fore");
            hind[i] = bones.get(side[i] + "hind");
            ghosts[i][0] = bones.get(side[i] + "fore_ghost_a");
            ghosts[i][1] = bones.get(side[i] + "fore_ghost_b");
            ghosts[i][2] = bones.get(side[i] + "hind_ghost_a");
            ghosts[i][3] = bones.get(side[i] + "hind_ghost_b");
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean whir = m.rouse > 0f && m.rouseVariant % 3 == 0;
        float buzz = Math.max(m.buzz, whir ? m.rouse : 0f);
        memory.spring(BUZZ, buzz, 0.35f, 0.55f);
        float spread = 1f - m.w(WingMotion.GROUND) + (whir ? m.rouse : 0f) * m.w(WingMotion.GROUND);
        memory.spring(SPREAD, Mth.clamp(spread, 0f, 1f), 0.18f, 0.66f);
        memory.spring(IDLE, m.rouse, 0.3f, 0.6f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.4f, 0.5f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float pt = m.partialTick;
        float spread = memory.get(SPREAD, pt);
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float charge = m.w(WingMotion.CHARGE);
        float brake = m.w(WingMotion.BRAKE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake - chute;
        float dive = m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.4f, 1f);
        float tuck = m.w(WingMotion.TUCK);
        int variant = m.rouseVariant % 3;

        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            blast = p < 0.15f ? p / 0.15f : (1f - p) * (1f - p) / 0.7225f;
        }
        float shiver = charge * charge;
        float buzz = Math.max(Mth.clamp(memory.get(BUZZ, pt), 0f, 1.1f), Math.max(0.5f * shiver, blast)) * (1f - chute);

        float foreSweep = (0.06f - FORE_REST) * spread + 0.06f * hover + 0.2f * slowBrake - 0.42f * dive - 0.85f * tuck
                - 0.08f * cruise + 0.5f * charge;
        float hindSweep = (-0.22f - HIND_REST) * spread + 0.12f * hover + 0.2f * slowBrake - 0.55f * dive - 0.9f * tuck
                - 0.02f * cruise + 0.45f * charge - 0.12f * m.w(WingMotion.AIR);
        float lift = 0.08f * hover + 0.04f * cruise + 0.14f * charge + 0.1f * m.w(WingMotion.AIR) + 0.2f * slowBrake + 0.05f * dive;

        float breath = ground * (1f - idle) * 0.025f * Mth.sin(time * 0.07f);
        lift += breath;
        foreSweep += breath * 0.6f;
        hindSweep += breath * 0.8f;

        float glide = cruise * (1f - Mth.clamp(buzz, 0f, 1f));
        float shimmer = glide * 0.035f * (Mth.sin(time * 1.7f) + 0.5f * Mth.sin(time * 3.1f + 1f));
        float dihedral = glide * 0.06f;
        float settleWave = settle * 0.22f * Mth.sin(time * 1.25f);

        float[] sideSweep = {0f, 0f};
        float[] sideTwist = {0f, 0f};
        float[] sideLift = {0f, 0f};
        if (idle > 0.001f && ground > 0.2f) {
            float p = m.rouseProgress;
            switch (variant) {
                case 1 -> {
                    float bask = idle * Mth.sin((float) Math.PI * p);
                    for (int i = 0; i < 2; i++) {
                        sideSweep[i] -= 0.28f * bask;
                        sideLift[i] -= 0.18f * bask;
                        sideTwist[i] += 0.22f * bask;
                    }
                }
                case 2 -> {
                    float first = Mth.clamp(p * 2f, 0f, 1f);
                    float second = Mth.clamp(p * 2f - 1f, 0f, 1f);
                    float r = idle * Mth.sin((float) Math.PI * first) * (p < 0.5f ? 1f : 0f);
                    float l = idle * Mth.sin((float) Math.PI * second) * (p >= 0.5f ? 1f : 0f);
                    float scrub = 0.08f * Mth.sin(time * 2.6f);
                    sideSweep[0] += r * (0.55f + scrub);
                    sideTwist[0] += r * 0.5f;
                    sideLift[0] += r * 0.12f;
                    sideSweep[1] += l * (0.55f + scrub);
                    sideTwist[1] += l * 0.5f;
                    sideLift[1] += l * 0.12f;
                }
                default -> {
                }
            }
        }

        float amplitude = buzz * (0.38f * cruise + 0.62f * hover + 0.25f * slowBrake + 0.2f * charge
                + 0.4f * (1f - Mth.clamp(cruise + hover + brake + charge, 0f, 1f))) + 0.5f * blast;
        float center = FLOOR * Mth.clamp(amplitude * 10f, 0f, 1f) + amplitude * 1.15f;
        float ph = time * VISUAL_RATE;
        float foreSwing = Mth.sin(ph) + 0.12f * Mth.sin(ph * 2.3f + 0.4f);
        float hindSwing = Mth.sin(ph + 1.57f) + 0.12f * Mth.sin(ph * 2.3f + 2f);
        float foreFeather = buzz * 0.55f * Mth.cos(ph);
        float hindFeather = buzz * 0.55f * Mth.cos(ph + 1.57f);
        float twist = hover * FlightRules.HOVER_PLANE;
        float flail = m.stall * 0.35f * Mth.sin(time * 1.5f);
        float stallLift = m.stall * 0.25f;
        boolean ghostsOn = amplitude > 0.2f && spread > 0.6f;
        float bank = m.bank * 0.35f;
        float tremble = chute * 0.02f * Mth.sin(time * 4.7f);
        float chutePitch = CHUTE_PITCH + 0.03f * Mth.sin(time * 5.3f);

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float asym = -s * bank * 0.45f;
            float foreBase = lift + dihedral + shimmer + flail + stallLift + settleWave + sideLift[i] + center;
            float hindBase = lift * 0.9f + dihedral * 0.8f + shimmer * 0.8f - flail * 0.8f + stallLift + settleWave * 0.8f
                    + sideLift[i] * 0.8f + center;
            float foreStroke = Math.max(FLOOR, Mth.lerp(chute, foreBase + amplitude * foreSwing, CHUTE_STROKE) + asym);
            float hindStroke = Math.max(FLOOR, Mth.lerp(chute, hindBase + amplitude * 0.92f * hindSwing, CHUTE_STROKE * 0.9f) + asym);
            float foreRaise = Mth.lerp(chute, foreSweep + sideSweep[i], CHUTE_RAISE - FORE_REST) + tremble;
            float hindRaise = Mth.lerp(chute, hindSweep + sideSweep[i] * 0.85f, CHUTE_RAISE * 0.55f - HIND_REST) + tremble;
            float foreTwist = Mth.lerp(chute, twist + foreFeather + sideTwist[i], chutePitch);
            float hindTwist = Mth.lerp(chute, twist + hindFeather + sideTwist[i] * 0.8f, chutePitch);
            fore[i].rotate(foreTwist, s * foreStroke, s * foreRaise);
            hind[i].rotate(hindTwist, s * hindStroke, s * hindRaise);
            for (int g = 0; g < 4; g++) {
                float sign = g % 2 == 0 ? 1f : -1f;
                boolean front = g < 2;
                float base = front ? foreBase : hindBase;
                float actual = front ? foreStroke : hindStroke;
                float edge = Math.max(FLOOR, base + sign * amplitude * 1.05f + asym);
                Bone ghost = ghosts[i][g];
                ghost.visible(ghostsOn);
                ghost.rotate(sign * 0.35f * buzz * (1f - hover), s * (edge - actual), 0f);
            }
        }
    }
}
