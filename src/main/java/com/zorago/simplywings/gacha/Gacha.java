package com.zorago.simplywings.gacha;

import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.data.WingRarity;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.item.MaterialIds;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.item.WingItem;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.ModAttachments;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredItem;

public final class Gacha {
    public static final int MULTI = 10;
    public static final java.util.Map<java.util.UUID, List<GachaTier>> FORCED = new java.util.HashMap<>();
    private static final java.util.Map<java.util.UUID, List<ItemStack>> PENDING = new java.util.HashMap<>();

    public static double rate(GachaTier tier, int pulls) {
        return switch (tier) {
            case S -> soft(ServerConfig.RATE_S.get(), tier, pulls);
            case SS -> soft(ServerConfig.RATE_SS.get(), tier, pulls);
            case SSS -> soft(ServerConfig.RATE_SSS.get(), tier, pulls);
            case A -> ServerConfig.RATE_A.get();
            default -> 0.0;
        };
    }

    public static int hard(GachaTier tier) {
        return switch (tier) {
            case S -> ServerConfig.HARD_S.get();
            case SS -> ServerConfig.HARD_SS.get();
            default -> ServerConfig.HARD_SSS.get();
        };
    }

    public static int softStart(GachaTier tier) {
        int start = switch (tier) {
            case S -> ServerConfig.SOFT_S.get();
            case SS -> ServerConfig.SOFT_SS.get();
            default -> ServerConfig.SOFT_SSS.get();
        };
        return Math.min(start, hard(tier) - 1);
    }

    private static double soft(double base, GachaTier tier, int pulls) {
        int start = softStart(tier);
        if (pulls >= hard(tier)) {
            return 1.0;
        }
        return Math.min(1.0, base + (1.0 - base) * Math.max(0, pulls - start) / (hard(tier) - start));
    }

    public static int feathers(ServerPlayer player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.SUMMONING_FEATHER.get())) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void consume(ServerPlayer player, int count) {
        for (ItemStack stack : player.getInventory().items) {
            if (count <= 0) {
                return;
            }
            if (stack.is(ModItems.SUMMONING_FEATHER.get())) {
                int take = Math.min(count, stack.getCount());
                stack.shrink(take);
                count -= take;
            }
        }
    }

    public static void onPull(ServerPlayer player, Payloads.GachaPull request) {
        if (!(player.containerMenu instanceof AltarMenu)) {
            return;
        }
        java.util.Optional<net.minecraft.core.BlockPos> altar = ((AltarMenu) player.containerMenu).pos();
        if (altar.isPresent() && AltarShows.busy(player.serverLevel(), altar.get()) > 0) {
            player.displayClientMessage(Component.translatable("message.simplywings.altar_busy"), true);
            return;
        }
        List<GachaTier> forced = FORCED.remove(player.getUUID());
        int count = forced != null ? forced.size() : request.count() >= MULTI ? MULTI : 1;
        if (forced == null && !player.isCreative() && feathers(player) < count) {
            player.displayClientMessage(Component.translatable("message.simplywings.gacha_feathers"), true);
            return;
        }
        if (forced == null && !player.isCreative()) {
            consume(player, count);
        }
        GachaData data = ModAttachments.gacha(player);
        List<GachaData.Entry> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (forced != null) {
                ItemStack stack = draw(player, forced.get(i), player.getRandom());
                results.add(new GachaData.Entry(forced.get(i).ordinal(), BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getCount()));
            } else {
                results.add(roll(player, data));
            }
        }
        claim(player);
        List<ItemStack> pending = PENDING.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
        for (GachaData.Entry entry : results) {
            data.record(entry);
            pending.add(new ItemStack(BuiltInRegistries.ITEM.get(entry.item()), entry.count()));
        }
        PacketDistributor.sendToPlayer(player, new Payloads.GachaResults(results, state(player)));
        altar.ifPresent(pos -> AltarShows.start(player, pos, results));
    }

    public static void claim(ServerPlayer player) {
        List<ItemStack> pending = PENDING.remove(player.getUUID());
        if (pending == null) {
            return;
        }
        for (ItemStack stack : pending) {
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }

    public static Payloads.GachaState state(ServerPlayer player) {
        GachaData data = ModAttachments.gacha(player);
        return new Payloads.GachaState(data.pity(GachaTier.S), data.pity(GachaTier.SS), data.pity(GachaTier.SSS),
                data.total(), feathers(player), data.history());
    }

    public static GachaData.Entry roll(ServerPlayer player, GachaData data) {
        data.tickPity();
        RandomSource random = player.getRandom();
        double r = random.nextDouble();
        double acc = 0;
        GachaTier tier = null;
        for (GachaTier t : new GachaTier[]{GachaTier.SSS, GachaTier.SS, GachaTier.S}) {
            acc += rate(t, data.pity(t));
            if (r < acc) {
                tier = t;
                break;
            }
        }
        if (tier == null) {
            tier = r < acc + rate(GachaTier.A, 0) ? GachaTier.A : GachaTier.B;
        }
        if (tier == GachaTier.SSS && random.nextDouble() < ServerConfig.MYTHIC_CHANCE.get()) {
            tier = GachaTier.MYTHIC;
        }
        data.reset(tier);
        ItemStack stack = draw(player, tier, random);
        return new GachaData.Entry(tier.ordinal(), BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getCount());
    }

    private static ItemStack draw(ServerPlayer player, GachaTier tier, RandomSource random) {
        return switch (tier) {
            case B -> {
                List<Item> pool = ModItems.materials(MaterialIds.DROPS);
                yield new ItemStack(pool.get(random.nextInt(pool.size())));
            }
            case A -> {
                List<Item> pool = new ArrayList<>(ModItems.materials(MaterialIds.INTERMEDIATES));
                pool.addAll(ModItems.materials(MaterialIds.PARTS));
                yield new ItemStack(pool.get(random.nextInt(pool.size())));
            }
            default -> {
                List<WingItem> pool = wings(player.registryAccess()).get(rarity(tier));
                if (pool == null || pool.isEmpty()) {
                    yield new ItemStack(ModItems.SUMMONING_FEATHER.get());
                }
                if (tier == GachaTier.MYTHIC) {
                    List<WingItem> fresh = pool.stream()
                            .filter(w -> !ModAttachments.get(player).collection().contains(w.wing())).toList();
                    if (!fresh.isEmpty()) {
                        pool = fresh;
                    }
                }
                yield new ItemStack(pool.get(random.nextInt(pool.size())));
            }
        };
    }

    private static WingRarity rarity(GachaTier tier) {
        return switch (tier) {
            case S -> WingRarity.RARE;
            case SS -> WingRarity.EPIC;
            case SSS -> WingRarity.LEGENDARY;
            default -> WingRarity.MYTHIC;
        };
    }

    public static Map<WingRarity, List<WingItem>> wings(RegistryAccess access) {
        Map<WingRarity, List<WingItem>> out = new EnumMap<>(WingRarity.class);
        for (DeferredItem<WingItem> holder : ModItems.wings()) {
            WingItem item = holder.get();
            WingRegistries.get(access, item.wing()).ifPresent(def -> out.computeIfAbsent(def.rarity(), k -> new ArrayList<>()).add(item));
        }
        return out;
    }

    private Gacha() {
    }
}
