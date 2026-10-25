package com.zorago.simplywings.gacha;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.item.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class SummoningLootModifier extends LootModifier {
    public static final MapCodec<SummoningLootModifier> CODEC = RecordCodecBuilder.mapCodec(
            i -> codecStart(i).apply(i, SummoningLootModifier::new));

    public SummoningLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        ResourceLocation table = context.getQueriedLootTableId();
        if (table == null || !table.getPath().startsWith("chests/")) {
            return loot;
        }
        if (!ResourceLocation.DEFAULT_NAMESPACE.equals(table.getNamespace()) && !ServerConfig.MODDED_CHESTS.get()) {
            return loot;
        }
        RandomSource random = context.getRandom();
        int luck = Math.max(0, Math.round(context.getLuck()));
        int count;
        if (context.getLevel().dimension() == Level.END || table.getPath().contains("end_city")) {
            int min = ServerConfig.END_CHEST_MIN.get();
            int max = Math.max(min, ServerConfig.END_CHEST_MAX.get());
            count = min + random.nextInt(max - min + 1) + luck;
        } else {
            double chance = ServerConfig.CHEST_CHANCE.get() + luck * ServerConfig.CHEST_LUCK_BONUS.get();
            if (random.nextDouble() >= chance) {
                return loot;
            }
            count = 1 + random.nextInt(Math.max(1, ServerConfig.CHEST_MAX.get()));
        }
        if (count > 0) {
            loot.add(new ItemStack(ModItems.SUMMONING_FEATHER.get(), count));
        }
        return loot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
