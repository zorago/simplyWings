package com.zorago.simplywings.essence;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class EssenceTanks {
    public static final int CAPACITY = 1000;

    private final int[] amount = new int[EssenceType.COUNT];

    public int get(EssenceType type) {
        return amount[type.ordinal()];
    }

    public void set(EssenceType type, int value) {
        amount[type.ordinal()] = Math.max(0, Math.min(CAPACITY, value));
    }

    public boolean canPay(EssenceCost cost) {
        for (EssenceType type : EssenceType.values()) {
            if (get(type) < cost.get(type)) {
                return false;
            }
        }
        return true;
    }

    public void pay(EssenceCost cost) {
        for (EssenceType type : EssenceType.values()) {
            set(type, get(type) - cost.get(type));
        }
    }

    public boolean pour(ItemStack container) {
        EssenceType type = EssenceContainerItem.type(container);
        if (type == null || get(type) >= CAPACITY) {
            return false;
        }
        int moved = EssenceContainerItem.drain(container, CAPACITY - get(type));
        set(type, get(type) + moved);
        return moved > 0;
    }

    public void save(CompoundTag tag) {
        for (EssenceType type : EssenceType.values()) {
            tag.putInt("Essence_" + type.getSerializedName(), get(type));
        }
    }

    public void load(CompoundTag tag) {
        for (EssenceType type : EssenceType.values()) {
            set(type, tag.getInt("Essence_" + type.getSerializedName()));
        }
    }
}
