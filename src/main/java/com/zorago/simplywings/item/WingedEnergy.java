package com.zorago.simplywings.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

public final class WingedEnergy {
    public static boolean has(ItemStack stack) {
        return stack.has(ModComponents.WINGED_ENERGY.get());
    }

    public static void toggle(ItemStack stack) {
        if (has(stack)) {
            stack.remove(ModComponents.WINGED_ENERGY.get());
            stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        } else {
            stack.set(ModComponents.WINGED_ENERGY.get(), Unit.INSTANCE);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
    }

    private WingedEnergy() {
    }
}
