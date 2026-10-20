package com.zorago.simplywings.anim;

import com.zorago.simplywings.player.FlightMode;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class WingAnimator {
    private static final float TWO_PI = (float) (Math.PI * 2);

    private final WingPose base = new WingPose();
    private final WingPose baseTarget = new WingPose();
    private final WingPose oscillation = new WingPose();
    private final WingPose current = new WingPose();
    private final WingPose previous = new WingPose();
    private final WingPose scratch = new WingPose();

    private float hoverPhase;
    private float hoverWeight;
    private float stallWeight;
    private float strokeProgress = -1;
    private float strokeDuration = 10;
    private int age;
    private boolean initialized;
    private float bank;
    private float prevBank;
    private float charge;

    public void flap(float flapRate) {
        strokeProgress = 0;
        strokeDuration = Math.max(4f, 20f / Math.max(0.5f, flapRate));
    }

    public void setCharge(float charge) {
        this.charge = Mth.clamp(charge, 0f, 1f);
    }

    public float bank(float partialTick) {
        return Mth.lerp(partialTick, prevBank, bank);
    }

    public void tick(Player player, FlightMode mode, int flags, float hoverRate) {
        age++;
        prevBank = bank;
        float turnRate = Mth.wrapDegrees(player.yBodyRot - player.yBodyRotO) * 20f;
        float targetBank = mode == FlightMode.TRAVEL ? Mth.clamp(turnRate * 0.5f, -45f, 45f) : 0f;
        bank += (targetBank - bank) * 0.25f;
        boolean brake = (flags & FlightMode.FLAG_BRAKE) != 0;
        boolean stall = (flags & FlightMode.FLAG_STALL) != 0;
        float pitch = player.getXRot();
        float rate = 0.3f;
        boolean hovering = false;

        switch (mode) {
            case STABILIZED -> {
                baseTarget.set(0.9f, 0.15f, 0.25f, 0f, 0f, 0.05f);
                hovering = true;
            }
            case TRAVEL -> {
                if (brake) {
                    baseTarget.set(1f, 0.55f, 0.45f, -0.25f, -0.35f, 1.1f);
                    rate = 0.35f;
                } else if (pitch > 35f) {
                    float dive = Mth.clamp((pitch - 35f) / 40f, 0f, 1f);
                    baseTarget.set(Mth.lerp(dive, 0.9f, 0.3f), Mth.lerp(dive, -0.1f, -0.65f), 0.05f, 0f, -0.1f, 0f);
                } else {
                    baseTarget.set(1f, -0.05f, 0.06f, 0f, -0.05f, 0f);
                }
                baseTarget.withAxis(1f);
            }
            case NONE -> {
                if (player.onGround() || player.isPassenger()) {
                    baseTarget.set(charge * 0.95f, charge * -0.2f, charge * 0.7f, 0f, 0f, 0f);
                    rate = charge > 0 ? 0.3f : 0.2f;
                } else {
                    baseTarget.set(0.55f, 0.1f, 0.5f, 0.1f, 0f, 0.2f);
                }
            }
        }

        if (!initialized) {
            base.copy(baseTarget);
            initialized = true;
        }
        base.approach(baseTarget, rate);
        hoverWeight += ((hovering ? 1f : 0f) - hoverWeight) * 0.25f;
        stallWeight += ((stall ? 1f : 0f) - stallWeight) * 0.25f;

        hoverPhase += TWO_PI * hoverRate / 20f;
        if (hoverPhase > TWO_PI) {
            hoverPhase -= TWO_PI;
        }

        oscillation.set(0, 0, 0, 0, 0, 0);
        if (hoverWeight > 0.01f) {
            float s = Mth.sin(hoverPhase);
            scratch.set(-0.18f * Math.max(0f, Mth.cos(hoverPhase)), 0f, 0.85f * s,
                    0.5f * Mth.sin(hoverPhase - 0.9f), 0.35f * Mth.sin(hoverPhase - 1.5f), 0.12f * Mth.cos(hoverPhase));
            oscillation.addScaled(scratch, hoverWeight);
        }
        if (strokeProgress >= 0) {
            float t = strokeProgress * TWO_PI;
            float envelope = Mth.sin(strokeProgress * (float) Math.PI);
            scratch.set(-0.2f * Math.max(0f, Mth.cos(t)) * envelope, 0f, 0.75f * Mth.sin(t),
                    0.55f * Mth.sin(t - 0.9f) * envelope, 0.4f * Mth.sin(t - 1.5f) * envelope, 0.1f * Mth.cos(t) * envelope)
                    .withCurl(0.45f * Math.max(0f, -Mth.cos(t)) * envelope);
            oscillation.addScaled(scratch, 1f - hoverWeight);
            strokeProgress += 1f / strokeDuration;
            if (strokeProgress >= 1f) {
                strokeProgress = -1;
            }
        }
        if (stallWeight > 0.01f) {
            float a = age * 1.4f;
            scratch.set(-0.2f, 0.2f * Mth.sin(age * 0.5f), 0.3f + 0.6f * Mth.sin(a), 0.4f * Mth.sin(a - 1f),
                    0.3f * Mth.sin(a - 1.8f), 0.4f * Mth.sin(age * 0.7f));
            oscillation.addScaled(scratch, stallWeight);
        }
        if (mode == FlightMode.NONE && (player.onGround() || player.isPassenger())) {
            oscillation.stroke += 0.04f * Mth.sin(age * 0.08f);
        } else if (mode == FlightMode.TRAVEL && !brake) {
            oscillation.stroke += 0.03f * (Mth.sin(age * 0.37f) + Mth.sin(age * 0.23f));
        } else if (brake) {
            oscillation.stroke += 0.04f * Mth.sin(age * 2.3f);
        }

        previous.copy(current);
        current.copy(base).addScaled(oscillation, 1f);
        current.spread = Mth.clamp(current.spread, 0f, 1.1f);
    }

    public WingPose pose(float partialTick, WingPose out) {
        return out.lerp(previous, current, partialTick);
    }
}
