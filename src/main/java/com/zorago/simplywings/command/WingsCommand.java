package com.zorago.simplywings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.zorago.simplywings.gacha.Gacha;
import com.zorago.simplywings.gacha.GachaData;
import com.zorago.simplywings.gacha.GachaTier;
import java.util.Locale;
import com.zorago.simplywings.item.ModItems;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.flight.ServerFlight;
import com.zorago.simplywings.item.WingedEnergy;
import com.zorago.simplywings.structure.EndSanctums;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.zorago.simplywings.player.ModAttachments;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class WingsCommand {
    private static final DynamicCommandExceptionType UNKNOWN_WING =
            new DynamicCommandExceptionType(id -> Component.translatable("commands.simplywings.unknown", id));

    private static final SuggestionProvider<CommandSourceStack> WINGS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(ctx.getSource().registryAccess().registry(WingRegistries.WING)
                    .map(r -> r.keySet().stream()).orElseGet(java.util.stream.Stream::empty), builder);

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("simplywings")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("unlock")
                        .then(targeted(Commands.literal("all"), WingsCommand::unlockAll))
                        .then(targeted(Commands.argument("wing", ResourceLocationArgument.id()).suggests(WINGS), WingsCommand::unlock)))
                .then(Commands.literal("equip")
                        .then(targeted(Commands.argument("wing", ResourceLocationArgument.id()).suggests(WINGS), WingsCommand::equip)))
                .then(targeted(Commands.literal("unequip"), WingsCommand::unequip))
                .then(Commands.literal("stamina")
                        .then(targeted(Commands.argument("value", FloatArgumentType.floatArg(0)), WingsCommand::stamina)))
                .then(targeted(Commands.literal("reset"), WingsCommand::reset))
                .then(targeted(Commands.literal("info"), WingsCommand::info))
                .then(Commands.literal("forge").executes(ctx -> sanctum(ctx, EndSanctums.Sanctum.WING_FORGE)))
                .then(Commands.literal("sanctum")
                        .then(Commands.literal("build").executes(ctx -> sanctumBuild(ctx, Optional.empty()))
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                java.util.Arrays.stream(EndSanctums.Sanctum.values()).map(EndSanctums.Sanctum::id), builder))
                                        .executes(ctx -> sanctumBuild(ctx, EndSanctums.Sanctum.byId(StringArgumentType.getString(ctx, "name"))))))
                        .then(Commands.literal("export")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                java.util.Arrays.stream(EndSanctums.Sanctum.values()).map(EndSanctums.Sanctum::id), builder))
                                        .executes(WingsCommand::sanctumExport)))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                        java.util.Arrays.stream(EndSanctums.Sanctum.values()).map(EndSanctums.Sanctum::id), builder))
                                .executes(ctx -> sanctum(ctx, EndSanctums.Sanctum.byId(StringArgumentType.getString(ctx, "name")).orElse(null)))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> sanctum(ctx, EndSanctums.Sanctum.byId(StringArgumentType.getString(ctx, "name")).orElse(null))))))
                .then(Commands.literal("dragon").executes(WingsCommand::dragon))
                .then(targeted(Commands.literal("energy"), WingsCommand::energy))
                .then(Commands.literal("gacha")
                        .then(Commands.literal("feathers")
                                .then(targeted(Commands.argument("count", IntegerArgumentType.integer(1, 6400)), WingsCommand::feathers)))
                        .then(Commands.literal("pity")
                                .then(Commands.argument("s", IntegerArgumentType.integer(0, 100000))
                                        .then(Commands.argument("ss", IntegerArgumentType.integer(0, 100000))
                                                .then(targeted(Commands.argument("sss", IntegerArgumentType.integer(0, 100000)), WingsCommand::pity)))))
                        .then(targeted(Commands.literal("reset"), WingsCommand::gachaReset))
                        .then(targeted(Commands.literal("info"), WingsCommand::gachaInfo))
                        .then(Commands.literal("force")
                                .then(targeted(Commands.literal("x10"), ctx -> force(ctx, "x10")))
                                .then(targeted(Commands.literal("ss"), ctx -> force(ctx, "ss")))
                                .then(targeted(Commands.literal("sss"), ctx -> force(ctx, "sss")))
                                .then(targeted(Commands.literal("mythic"), ctx -> force(ctx, "mythic"))))
                        .then(Commands.literal("show")
                                .then(Commands.argument("animation", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(java.util.List.of("summon_b", "summon_a", "summon_s",
                                                "summon_ss", "summon_sss", "summon_mythic_night", "summon_mythic_light", "summon_x10"), builder))
                                        .executes(WingsCommand::gachaShow)))));
    }

    private static <T extends com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack, T>> T targeted(T node,
            com.mojang.brigadier.Command<CommandSourceStack> command) {
        return node.executes(command).then(Commands.argument("targets", EntityArgument.players()).executes(command));
    }

    private static Collection<ServerPlayer> targets(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        try {
            return EntityArgument.getPlayers(ctx, "targets");
        } catch (IllegalArgumentException e) {
            return List.of(ctx.getSource().getPlayerOrException());
        }
    }

    private static Component who(CommandContext<CommandSourceStack> ctx, ServerPlayer player, Component message) {
        return ctx.getSource().getEntity() == player ? message
                : Component.empty().append(message).append(Component.literal(" [" + player.getScoreboardName() + "]"));
    }

    private static int feathers(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            int count = IntegerArgumentType.getInteger(ctx, "count");
            while (count > 0) {
                int n = Math.min(64, count);
                ItemStack stack = new ItemStack(ModItems.SUMMONING_FEATHER.get(), n);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                count -= n;
            }
        }
        return players.size();
    }

    private static int force(CommandContext<CommandSourceStack> ctx, String what) throws CommandSyntaxException {
        java.util.List<com.zorago.simplywings.gacha.GachaTier> tiers = switch (what) {
            case "x10" -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.B, com.zorago.simplywings.gacha.GachaTier.A,
                    com.zorago.simplywings.gacha.GachaTier.S, com.zorago.simplywings.gacha.GachaTier.SS, com.zorago.simplywings.gacha.GachaTier.SSS,
                    com.zorago.simplywings.gacha.GachaTier.MYTHIC, com.zorago.simplywings.gacha.GachaTier.B, com.zorago.simplywings.gacha.GachaTier.A,
                    com.zorago.simplywings.gacha.GachaTier.S, com.zorago.simplywings.gacha.GachaTier.SS);
            case "ss" -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.SS);
            case "sss" -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.SSS);
            default -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.MYTHIC);
        };
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            com.zorago.simplywings.gacha.Gacha.FORCED.put(player.getUUID(), tiers);
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.literal("The next summon at the Draconic Altar is forced: " + tiers
                    + " (no feather used). Open the altar and press any summon button.")), false);
        }
        return players.size();
    }

    private static int gachaShow(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockPos found = null;
        BlockPos at = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(at.offset(-24, -12, -24), at.offset(24, 12, 24))) {
            if (player.level().getBlockState(pos).is(com.zorago.simplywings.block.ModBlocks.DRACONIC_ALTAR.get())
                    && (found == null || pos.distSqr(at) < found.distSqr(at))) {
                found = pos.immutable();
            }
        }
        if (found == null) {
            ctx.getSource().sendFailure(Component.literal("No Draconic Altar within 24 blocks"));
            return 0;
        }
        com.zorago.simplywings.gacha.AltarShows.startAnimation(player, found, StringArgumentType.getString(ctx, "animation"),
                java.util.List.of(0, 0, 0, 0, 0, 0, 1, 2, 3, 4));
        return 1;
    }

    private static int pity(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        for (ServerPlayer player : targets(ctx)) {
            ModAttachments.gacha(player).setPity(IntegerArgumentType.getInteger(ctx, "s"), IntegerArgumentType.getInteger(ctx, "ss"),
                    IntegerArgumentType.getInteger(ctx, "sss"));
        }
        return gachaInfo(ctx);
    }

    private static int gachaReset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        for (ServerPlayer player : targets(ctx)) {
            ModAttachments.gacha(player).clear();
        }
        return gachaInfo(ctx);
    }

    private static int gachaInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            GachaData data = ModAttachments.gacha(player);
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.literal("Pity S " + data.pity(GachaTier.S)
                    + "/" + Gacha.hard(GachaTier.S) + ", SS " + data.pity(GachaTier.SS) + "/" + Gacha.hard(GachaTier.SS)
                    + ", SSS " + data.pity(GachaTier.SSS) + "/" + Gacha.hard(GachaTier.SSS)
                    + ", total " + data.total() + ", reserve " + String.format(Locale.ROOT, "%.3f", data.reserve()))), false);
        }
        return players.size();
    }

    private static ResourceLocation wing(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "wing");
        if (WingRegistries.get(ctx.getSource().registryAccess(), id).isEmpty()) {
            throw UNKNOWN_WING.create(id.toString());
        }
        return id;
    }

    private static int unlock(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = wing(ctx);
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            ServerFlight.unlockAndEquip(player, id);
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.translatable("commands.simplywings.unlock", id.toString())), true);
        }
        return players.size();
    }

    private static int unlockAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int total = 0;
        for (ServerPlayer player : targets(ctx)) {
            int added = ServerFlight.unlockAll(player);
            total += added;
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.translatable("commands.simplywings.unlock_all", added)), true);
        }
        return total;
    }

    private static int equip(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = wing(ctx);
        int done = 0;
        for (ServerPlayer player : targets(ctx)) {
            if (!ModAttachments.get(player).collection().contains(id)) {
                ctx.getSource().sendFailure(who(ctx, player, Component.translatable("commands.simplywings.not_owned", id.toString())));
                continue;
            }
            ServerFlight.equip(player, Optional.of(id));
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.translatable("commands.simplywings.equip", id.toString())), true);
            done++;
        }
        return done;
    }

    private static int unequip(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            ServerFlight.equip(player, Optional.empty());
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.translatable("commands.simplywings.unequip")), true);
        }
        return players.size();
    }

    private static int stamina(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        float value = FloatArgumentType.getFloat(ctx, "value");
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            ServerFlight.setStamina(player, value);
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.translatable("commands.simplywings.stamina", value)), true);
        }
        return players.size();
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            ServerFlight.clear(player);
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.translatable("commands.simplywings.reset")), true);
        }
        return players.size();
    }

    private static int sanctum(CommandContext<CommandSourceStack> ctx, EndSanctums.Sanctum sanctum) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targets(ctx);
        ServerLevel end = ctx.getSource().getServer().getLevel(Level.END);
        BlockPos center = end == null || sanctum == null ? null : EndSanctums.place(end, sanctum);
        if (center == null) {
            ctx.getSource().sendFailure(Component.translatable("commands.simplywings.forge_failed"));
            return 0;
        }
        for (ServerPlayer player : players) {
            EndSanctums.give(player, end, sanctum, center);
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.forge", center.toShortString()), true);
        return 1;
    }

    private static int sanctumBuild(CommandContext<CommandSourceStack> ctx, Optional<EndSanctums.Sanctum> only) {
        EndSanctums.buildWorld(ctx.getSource().getLevel(), only);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.sanctum_build"), true);
        return 1;
    }

    private static int sanctumExport(CommandContext<CommandSourceStack> ctx) {
        Optional<EndSanctums.Sanctum> sanctum = EndSanctums.Sanctum.byId(StringArgumentType.getString(ctx, "name"));
        if (sanctum.isEmpty()) {
            ctx.getSource().sendFailure(Component.translatable("commands.simplywings.forge_failed"));
            return 0;
        }
        String path = EndSanctums.export(ctx.getSource().getLevel(), sanctum.get()).toString();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.sanctum_export", path), true);
        return 1;
    }

    private static int dragon(CommandContext<CommandSourceStack> ctx) {
        ServerLevel end = ctx.getSource().getServer().getLevel(Level.END);
        if (end == null) {
            return 0;
        }
        int kills = EndSanctums.dragonKilled(end);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.dragon", kills), true);
        return kills;
    }

    private static int energy(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int done = 0;
        for (ServerPlayer player : targets(ctx)) {
            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty()) {
                ctx.getSource().sendFailure(who(ctx, player, Component.translatable("commands.simplywings.no_item")));
                continue;
            }
            WingedEnergy.toggle(stack);
            boolean on = WingedEnergy.has(stack);
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.translatable(
                    on ? "commands.simplywings.energy_on" : "commands.simplywings.energy_off", stack.getHoverName())), false);
            done++;
        }
        return done;
    }

    private static int info(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targets(ctx);
        for (ServerPlayer player : players) {
            var data = ModAttachments.get(player);
            var stats = data.stats(player.registryAccess());
            String text = stats.map(s -> String.format(Locale.ROOT,
                    "%s | stamina %.1f/%.1f | accel %.2f | vmax %.1f | vmin %.1f | handling %.0f | stab %.1f | mode %s",
                    s.wing(), data.stamina(), s.stamina(), s.acceleration(), s.maxSpeed(), s.minSpeed(), s.handling(),
                    s.stabilization(), data.mode())).orElse("-");
            ctx.getSource().sendSuccess(() -> who(ctx, player, Component.literal(text)), false);
        }
        return players.size();
    }

    private WingsCommand() {
    }
}
