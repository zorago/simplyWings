package com.zorago.simplywings.gacha;

import com.zorago.simplywings.network.Payloads;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class AltarShows {
    public static final double RANGE = 512;
    private static final int MAX_TICKS = 36 * 20;
    private static final List<Show> ACTIVE = new ArrayList<>();
    private static final double CARD_RANGE = 64;
    private static final Map<UUID, GlobalPos> LAST_ALTAR = new HashMap<>();
    private static final int[][] FLAMES = {{-4, 6}, {-7, 0}, {-4, -6}, {4, -6}, {7, 0}, {4, 6}};

    private static final class Show {
        final ResourceKey<Level> dimension;
        final Payloads.SummonShow payload;
        final long end;
        final Set<UUID> sent = new HashSet<>();
        final List<BlockPos> lights = new ArrayList<>();

        Show(ResourceKey<Level> dimension, Payloads.SummonShow payload, long end) {
            this.dimension = dimension;
            this.payload = payload;
            this.end = end;
        }
    }

    public static void register(IEventBus bus) {
        bus.addListener(AltarShows::onTick);
        bus.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                Gacha.claim(player);
            }
        });
    }

    public static String animation(List<GachaData.Entry> results) {
        if (results.size() > 1) {
            return "summon_x10";
        }
        GachaData.Entry e = results.get(0);
        return switch (e.rank()) {
            case B -> "summon_b";
            case A -> "summon_a";
            case S -> "summon_s";
            case SS -> "summon_ss";
            case SSS -> "summon_sss";
            case MYTHIC -> e.item().getPath().contains("night") ? "summon_mythic_night" : "summon_mythic_light";
        };
    }

    public static int facing(BlockState state) {
        return state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.getValue(HorizontalDirectionalBlock.FACING).get2DDataValue()
                : Direction.SOUTH.get2DDataValue();
    }

    public static BlockPos rotate(BlockPos altar, int facing, int dx, int dy, int dz) {
        int x = dx;
        int z = dz;
        for (int i = 0; i < facing; i++) {
            int t = x;
            x = -z;
            z = t;
        }
        return altar.offset(x, dy, z);
    }

    public static void start(ServerPlayer player, BlockPos altar, List<GachaData.Entry> results) {
        List<Integer> tiers = new ArrayList<>();
        for (GachaData.Entry e : results) {
            tiers.add(e.tier());
        }
        tiers.sort(Integer::compare);
        startAnimation(player, altar, animation(results), tiers);
    }

    public static void startAnimation(ServerPlayer player, BlockPos altar, String anim, List<Integer> tiers) {
        ServerLevel level = player.serverLevel();
        level.getBlockEntity(altar);
        ACTIVE.removeIf(s -> s.dimension == level.dimension() && s.payload.pos().equals(altar) && clear(level, s));
        int facing = facing(level.getBlockState(altar));
        Payloads.SummonShow payload = new Payloads.SummonShow(altar, facing, anim, level.getGameTime() + 2,
                player.getRandom().nextInt(), tiers, player.getId());
        Show show = new Show(level.dimension(), payload, level.getGameTime() + MAX_TICKS);
        int top = level.getMaxBuildHeight() - 1;
        for (int dy = 3; dy <= 64 && altar.getY() + dy <= top; dy += 5) {
            light(level, altar.above(dy), 15, show.lights);
        }
        for (int[] f : FLAMES) {
            light(level, rotate(altar, facing, f[0], 4, f[1]), 15, show.lights);
        }
        ACTIVE.add(show);
        LAST_ALTAR.put(player.getUUID(), GlobalPos.of(level.dimension(), altar));
        broadcast(level, show);
    }

    public static void relayCard(ServerPlayer sender, Payloads.CardSound sound) {
        GlobalPos altar = LAST_ALTAR.get(sender.getUUID());
        if (altar == null || altar.dimension() != sender.level().dimension()) {
            return;
        }
        Payloads.CardSoundEvent event = new Payloads.CardSoundEvent(sender.getId(), altar.pos(), sound.key(), sound.tier());
        for (ServerPlayer player : sender.serverLevel().players()) {
            if (player != sender && player.distanceToSqr(altar.pos().getCenter()) <= CARD_RANGE * CARD_RANGE) {
                PacketDistributor.sendToPlayer(player, event);
            }
        }
    }

    private static void light(ServerLevel level, BlockPos pos, int value, List<BlockPos> placed) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.LIGHT) && state.getValue(LightBlock.LEVEL) < value) {
            level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, value), Block.UPDATE_CLIENTS);
            placed.add(pos);
        }
    }

    private static boolean clear(ServerLevel level, Show show) {
        for (BlockPos pos : show.lights) {
            if (level.getBlockState(pos).is(Blocks.LIGHT)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
        show.lights.clear();
        return true;
    }

    private static void broadcast(ServerLevel level, Show show) {
        BlockPos p = show.payload.pos();
        for (ServerPlayer player : level.players()) {
            double dx = player.getX() - p.getX();
            double dz = player.getZ() - p.getZ();
            if (dx * dx + dz * dz <= RANGE * RANGE && show.sent.add(player.getUUID())) {
                PacketDistributor.sendToPlayer(player, show.payload);
            }
        }
    }

    private static void onTick(ServerTickEvent.Post event) {
        if (ACTIVE.isEmpty()) {
            return;
        }
        MinecraftServer server = event.getServer();
        Iterator<Show> it = ACTIVE.iterator();
        while (it.hasNext()) {
            Show show = it.next();
            ServerLevel level = server.getLevel(show.dimension);
            if (level == null) {
                it.remove();
                continue;
            }
            if (level.getGameTime() >= show.end) {
                clear(level, show);
                it.remove();
            } else if (level.getGameTime() % 10 == 0) {
                broadcast(level, show);
            }
        }
    }

    public static void flameLights(ServerLevel level, BlockPos altar, BlockState state, boolean remove) {
        int facing = facing(state);
        for (int[] f : FLAMES) {
            for (int dy : new int[]{1, 3}) {
                BlockPos pos = rotate(altar, facing, f[0], dy, f[1]);
                BlockState s = level.getBlockState(pos);
                if (remove) {
                    if (s.is(Blocks.LIGHT)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                } else if (s.isAir()) {
                    level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 13), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    private AltarShows() {
    }
}
