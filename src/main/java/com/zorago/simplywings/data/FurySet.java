package com.zorago.simplywings.data;

import com.zorago.simplywings.SimplyWings;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public final class FurySet {
    public static final ResourceLocation NIGHT = SimplyWings.id("night");
    public static final ResourceLocation LIGHT = SimplyWings.id("light");
    public static final int CHARGE_TICKS = 40;
    public static final int DASH_WINDOW_TICKS = 10;

    public static boolean infiniteStamina(Collection<ResourceLocation> collection) {
        return collection.contains(NIGHT) && collection.contains(LIGHT);
    }

    public static boolean isFury(Optional<ResourceLocation> wing) {
        return wing.filter(id -> id.equals(NIGHT) || id.equals(LIGHT)).isPresent();
    }

    public static boolean isNight(Optional<ResourceLocation> wing) {
        return wing.filter(NIGHT::equals).isPresent();
    }

    private FurySet() {
    }
}
