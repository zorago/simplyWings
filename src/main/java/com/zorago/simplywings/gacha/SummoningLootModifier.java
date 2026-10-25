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
        Level level = context.getLevel();
        double chance = level.dimension() == Level.END || table.getPath().contains("end_city") ? ServerConfig.END_CHEST_CHANCE.get()
                : level.dimension() == Level.NETHER || table.getPath().contains("bastion") || table.getPath().contains("nether_bridge")
                ? ServerConfig.NETHER_CHEST_CHANCE.get() : ServerConfig.CHEST_CHANCE.get();
        chance += Math.max(0, context.getLuck()) * ServerConfig.CHEST_LUCK_BONUS.get();
        if (random.nextDouble() >= chance) {
            return loot;
        }
        int min = ServerConfig.CHEST_MIN.get();
        int count = min + random.nextInt(Math.max(min, ServerConfig.CHEST_MAX.get()) - min + 1);
        loot.add(new ItemStack(ModItems.SUMMONING_FEATHER.get(), count));
        return loot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
