package com.zorago.simplywings.anim;

import com.zorago.simplywings.player.FlightMode;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class WingAnimator {
    private static final float TWO_PI = (float) (Math.PI * 2);
    private static final float HALF_PI = (float) (Math.PI / 2);

    private final WingPose base = new WingPose();
    private final WingPose prevBase = new WingPose();
    private final WingPose baseTarget = new WingPose();
    private final WingPose scratch = new WingPose();

    private double hoverPhase;
    private double prevHoverPhase;
    private float hoverWeight;
    private float prevHoverWeight;
    private float stallWeight;
    private float prevStallWeight;
    private float vibrationWeight;
    private float prevVibrationWeight;
    private float strokeProgress = -1;
    private float prevStrokeProgress = -1;
    private float strokeDuration = 10;
    private int age;
    private boolean initialized;
    private float bank;
    private float prevBank;
    private float charge;
    private boolean idle;
    private boolean glide;
    private boolean brake;

    public void flap(float flapRate) {
        strokeDuration = Math.max(4f, 20f / Math.max(0.1f, flapRate));
        strokeProgress = 0;
        prevStrokeProgress = 0;
    }

    public void strike(float flapRate, float strikeAt) {
        flap(flapRate);
        strokeProgress = strikeAt;
        prevStrokeProgress = strikeAt;
    }

    public void setCharge(float charge) {
        this.charge = Mth.clamp(charge, 0f, 1f);
    }

    public float bank(float partialTick) {
        return Mth.lerp(partialTick, prevBank, bank);
    }

    public void tick(Player player, FlightMode mode, int flags, WingStyle style, float hoverRate) {
        age++;
        prevBank = bank;
        float turnRate = Mth.wrapDegrees(player.yBodyRot - player.yBodyRotO) * 20f;
        float targetBank = mode == FlightMode.TRAVEL ? Mth.clamp(turnRate * 0.5f, -45f, 45f) : 0f;
        bank += (targetBank - bank) * 0.25f;

        brake = (flags & FlightMode.FLAG_BRAKE) != 0;
        boolean stall = (flags & FlightMode.FLAG_STALL) != 0;
        boolean tuck = (flags & FlightMode.FLAG_TUCK) != 0;
        boolean thrust = (flags & FlightMode.FLAG_THRUST) != 0;
        boolean grounded = player.onGround() || player.isPassenger();
        float pitch = player.getXRot();
        float rate = 0.3f;
        boolean hovering = false;
        idle = mode == FlightMode.NONE && grounded;
        glide = mode == FlightMode.TRAVEL && !brake && !tuck;

        switch (mode) {
            case STABILIZED -> {
                baseTarget.set(0.9f, 0.15f, 0.25f, 0f, 0f, 0.05f);
                hovering = true;
            }
            case TRAVEL -> {
                if (brake) {
                    baseTarget.set(1f, 0.55f, 0.45f, -0.25f, -0.35f, 1.1f);
                    rate = 0.35f;
                } else if (tuck) {
                    baseTarget.set(0.05f, 0f, 0f, 0f, 0f, 0f);
                    rate = 0.25f;
                } else if (pitch > 35f) {
                    float dive = Mth.clamp((pitch - 35f) / 40f, 0f, 1f);
                    baseTarget.set(1f, Mth.lerp(dive, -0.05f, -0.25f), 0.06f, 0f, -0.05f, 0f);
                } else {
                    baseTarget.set(1f, -0.05f, 0.06f, 0f, -0.05f, 0f);
                }
                baseTarget.withAxis(1f);
            }
            case NONE -> {
                if (grounded) {
                    baseTarget.set(charge * 0.95f, charge * -0.2f, charge * 0.7f, 0f, 0f, 0f);
                    rate = charge > 0 ? 0.3f : 0.2f;
                } else {
                    baseTarget.set(0.55f, 0.1f, 0.5f, 0.1f, 0f, 0.2f);
                }
            }
        }
        if (!style.folds() && baseTarget.spread < 1f) {
            float fold = 1f - baseTarget.spread;
            baseTarget.spread = 1f;
            baseTarget.sweep -= 0.55f * fold;
            baseTarget.stroke -= 0.1f * fold;
        }

        if (!initialized) {
            base.copy(baseTarget);
            initialized = true;
        }
        prevBase.copy(base);
        base.approach(baseTarget, rate);

        prevHoverWeight = hoverWeight;
        prevStallWeight = stallWeight;
        prevVibrationWeight = vibrationWeight;
        boolean hoverBeats = hovering && style.hoverAmplitude() > 0;
        boolean vibrating = style.vibration() > 0 && (hovering || thrust || (mode == FlightMode.TRAVEL && !tuck));
        float vibrationTarget = vibrating ? (hovering || thrust ? 1f : 0.3f) : 0f;
        hoverWeight += ((hoverBeats ? 1f : 0f) - hoverWeight) * 0.2f;
        stallWeight += ((stall ? 1f : 0f) - stallWeight) * 0.25f;
        vibrationWeight += (vibrationTarget - vibrationWeight) * 0.3f;

        prevHoverPhase = hoverPhase;
        hoverPhase += TWO_PI * hoverRate / 20f;
        if (hoverPhase > TWO_PI * 64) {
            hoverPhase -= TWO_PI * 64;
            prevHoverPhase -= TWO_PI * 64;
        }

        prevStrokeProgress = strokeProgress;
        if (strokeProgress >= 0) {
            strokeProgress += 1f / strokeDuration;
            if (strokeProgress >= 1f) {
                strokeProgress = -1;
                prevStrokeProgress = -1;
            }
        }
    }

    public WingPose pose(float partialTick, WingStyle style, WingStyle.Slot slot, WingPose out) {
        out.lerp(prevBase, base, partialTick);
        float time = age + partialTick;
        float hoverRaw = Mth.lerp(partialTick, prevHoverWeight, hoverWeight);
        float hw = hoverRaw * slot.amplitude();
        if (hw > 0.001f) {
            float ph = (float) Mth.lerp(partialTick, prevHoverPhase, hoverPhase) + slot.phase();
            float a = style.hoverAmplitude();
            scratch.set(-0.18f * Math.max(0f, Mth.cos(ph)), 0f, a * Mth.sin(ph), 0.55f * a * Mth.sin(ph - 0.9f),
                    0.4f * a * Mth.sin(ph - 1.5f), 0.12f * Mth.cos(ph));
            out.addScaled(scratch, hw);
        }
        if (strokeProgress >= 0 && prevStrokeProgress >= 0) {
            float t = wrap(Mth.lerp(partialTick, prevStrokeProgress, strokeProgress) + slot.phase() / TWO_PI);
            float a = style.strokeAmplitude() * slot.amplitude() * (1f - hoverRaw);
            out.stroke += a * style.stroke(t);
            out.elbow += 0.6f * a * style.stroke(wrap(t - 0.07f));
            out.tip += 0.45f * a * style.stroke(wrap(t - 0.14f));
            out.curl += 0.45f * style.curl(t) * slot.amplitude() * (1f - hoverRaw);
        }
        float sw = Mth.lerp(partialTick, prevStallWeight, stallWeight) * slot.amplitude();
        if (sw > 0.001f) {
            float a = time * 1.4f + slot.phase();
            scratch.set(-0.2f, 0.2f * Mth.sin(time * 0.5f), 0.3f + 0.6f * Mth.sin(a), 0.4f * Mth.sin(a - 1f),
                    0.3f * Mth.sin(a - 1.8f), 0.4f * Mth.sin(time * 0.7f));
            out.addScaled(scratch, sw);
        }
        float vw = Mth.lerp(partialTick, prevVibrationWeight, vibrationWeight) * slot.amplitude();
        if (vw > 0.001f) {
            float v = style.vibration() * Mth.sin(time * TWO_PI * style.vibrationRate() / 20f + slot.phase());
            out.stroke += vw * v;
            out.twist += vw * v * 0.3f;
        }
        if (idle || !style.folds()) {
            out.stroke += style.breath() * Mth.sin(time * 0.08f + slot.phase());
            out.sweep += style.breath() * 0.4f * Mth.sin(time * 0.06f);
        } else if (glide) {
            out.stroke += 0.03f * (Mth.sin(time * 0.37f) + Mth.sin(time * 0.23f));
        } else if (brake) {
            out.stroke += 0.04f * Mth.sin(time * 2.3f);
        }
        float offsetWeight = style.folds() ? Mth.clamp(out.spread, 0f, 1f) : 1f;
        out.sweep += slot.sweep() * offsetWeight;
        out.stroke += slot.lift() * offsetWeight;
        if (style.flat()) {
            out.twist += HALF_PI * (1f - out.axis);
        }
        out.spread = Mth.clamp(out.spread, 0f, 1.1f);
        return out;
    }

    private static float wrap(float t) {
        return t - (float) Math.floor(t);
    }
}
