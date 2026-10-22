package com.zorago.simplywings.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zorago.simplywings.item.WingedEnergy;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class WingedLootModifier extends LootModifier {
    public static final MapCodec<WingedLootModifier> CODEC = RecordCodecBuilder.mapCodec(
            i -> codecStart(i).apply(i, WingedLootModifier::new));

    public WingedLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        Entity victim = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        LootSource.Kind kind;
        ItemStack tool;
        if (state != null) {
            kind = LootSource.Kind.BLOCK;
            tool = context.getParamOrNull(LootContextParams.TOOL);
        } else if (BuiltInLootTables.FISHING.location().equals(context.getQueriedLootTableId())) {
            kind = LootSource.Kind.FISH;
            tool = context.getParamOrNull(LootContextParams.TOOL);
        } else if (victim != null && !(victim instanceof Player) && context.hasParam(LootContextParams.DAMAGE_SOURCE)
                && context.getParamOrNull(LootContextParams.ATTACKING_ENTITY) instanceof Player killer) {
            kind = LootSource.Kind.MOB;
            tool = killer.getMainHandItem();
        } else {
            return loot;
        }
        if (tool == null || !WingedEnergy.has(tool)) {
            return loot;
        }
        Registry<LootSource> sources = LootSource.registry(context.getLevel().registryAccess()).orElse(null);
        if (sources == null) {
            return loot;
        }
        for (LootSource source : sources) {
            if (source.kind() != kind) {
                continue;
            }
            boolean target = switch (kind) {
                case BLOCK -> source.matches(state);
                case MOB -> source.matches(victim.getType());
                case FISH -> source.targets().isEmpty();
            };
            if (!target || context.getRandom().nextFloat() >= source.chance()) {
                continue;
            }
            boolean allowed = true;
            for (LootItemCondition condition : source.conditions()) {
                if (!condition.test(context)) {
                    allowed = false;
                    break;
                }
            }
            if (allowed) {
                int count = source.min() + context.getRandom().nextInt(Math.max(1, source.max() - source.min() + 1));
                loot.add(new ItemStack(source.item(), count));
            }
        }
        return loot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
