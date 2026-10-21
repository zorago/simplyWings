package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class PhantomRig extends WingRig {
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/phantom_glow.png");
    private static final int FOLD = 0;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.75f, 0.1f, 0.95f, 1f, -0.3f, -0.9f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.1f, -0.15f, -0.2f, -0.05f, 0.2f, 0.25f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.25f, 0.1f, -0.05f, -0.25f, -0.1f);

    private final Bone[] shoulder = new Bone[2];
    private final Bone[] arm = new Bone[2];
    private final Bone[] forearm = new Bone[2];
    private final Bone[] hand = new Bone[2];

    public PhantomRig() {
        super("phantom");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            arm[i] = bones.get(side[i] + "arm");
            forearm[i] = bones.get(side[i] + "forearm");
            hand[i] = bones.get(side[i] + "hand");
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) + 0.55f * m.w(WingMotion.TUCK) + 0.3f * m.w(WingMotion.AIR);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.16f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float dive = m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.5f, 1f);
        float stroke = 0.08f * m.w(WingMotion.CRUISE) + 0.5f * m.w(WingMotion.LANDING) + 0.4f * m.w(WingMotion.BRAKE)
                + 0.2f * m.w(WingMotion.HOVER) + 0.85f * m.w(WingMotion.CHARGE) + 0.08f * fold + 0.05f * dive;
        float sweep = 0.3f * m.w(WingMotion.LANDING) + 0.5f * m.w(WingMotion.BRAKE) - 0.75f * dive
                + 0.25f * m.w(WingMotion.CHARGE) - 0.1f * m.w(WingMotion.CRUISE);
        float twist = -0.7f * m.w(WingMotion.BRAKE) - 0.35f * m.w(WingMotion.LANDING);
        float wave = 0f;
        float lag = 0f;
        float lag2 = 0f;
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            stroke += a * STROKE.at(t);
            sweep += a * SWEEP.at(t);
            twist += a * TWIST.at(t);
            lag += a * 0.5f * STROKE.lagged(t, 0.07f);
            lag2 += a * 0.8f * STROKE.lagged(t, 0.15f);
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            stroke += hw * 0.6f * Mth.cos(ph);
            twist += hw * 0.4f * Mth.sin(ph);
            lag += hw * 0.35f * (Mth.cos(ph - 0.5f) - Mth.cos(ph));
            lag2 += hw * 0.55f * (Mth.cos(ph - 1f) - Mth.cos(ph));
        }
        float glide = m.w(WingMotion.CRUISE) + 0.6f * m.w(WingMotion.HOVER);
        wave = glide * 0.08f;
        lag += wave * Mth.sin(time * 0.35f - 0.8f) - wave * Mth.sin(time * 0.35f);
        lag2 += wave * 1.6f * (Mth.sin(time * 0.35f - 1.6f) - Mth.sin(time * 0.35f));
        stroke += wave * Mth.sin(time * 0.35f);
        float sw = m.stall;
        if (sw > 0.001f) {
            stroke += sw * 0.5f * Mth.sin(time * 1.1f);
            twist += sw * 0.45f * Mth.sin(time * 0.8f);
            lag2 += sw * 0.3f * Mth.sin(time * 1.6f);
        }
        float droop = -0.12f * m.w(WingMotion.CRUISE);
        float open = 1f - fold;
        float spread = Mth.lerp(fold, 1f, 0.45f);
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(twist * open, s * stroke - m.bank * 0.35f, s * sweep * open);
            arm[i].rotate(0f, 0f, s * -1.2f * fold).scale(1f, spread, 1f);
            forearm[i].rotate(0f, s * lag, s * (2.55f * fold - 0.2f * dive)).scale(1f, Mth.lerp(fold, 1f, 0.6f), 1f);
            hand[i].rotate(0f, s * (lag2 + droop), s * (-2.35f * fold - 0.25f * dive));
        }
    }
}
