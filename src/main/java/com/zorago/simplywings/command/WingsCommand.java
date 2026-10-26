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
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
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
                        .then(Commands.literal("all").executes(WingsCommand::unlockAll))
                        .then(Commands.argument("wing", ResourceLocationArgument.id()).suggests(WINGS)
                                .executes(WingsCommand::unlock)))
                .then(Commands.literal("equip")
                        .then(Commands.argument("wing", ResourceLocationArgument.id()).suggests(WINGS)
                                .executes(WingsCommand::equip)))
                .then(Commands.literal("unequip").executes(WingsCommand::unequip))
                .then(Commands.literal("stamina")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0))
                                .executes(WingsCommand::stamina)))
                .then(Commands.literal("reset").executes(WingsCommand::reset))
                .then(Commands.literal("info").executes(WingsCommand::info))
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
                                .executes(ctx -> sanctum(ctx, EndSanctums.Sanctum.byId(StringArgumentType.getString(ctx, "name")).orElse(null)))))
                .then(Commands.literal("dragon").executes(WingsCommand::dragon))
                .then(Commands.literal("energy").executes(WingsCommand::energy))
                .then(Commands.literal("gacha")
                        .then(Commands.literal("feathers")
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 6400)).executes(WingsCommand::feathers)))
                        .then(Commands.literal("pity")
                                .then(Commands.argument("s", IntegerArgumentType.integer(0, 99))
                                        .then(Commands.argument("ss", IntegerArgumentType.integer(0, 499))
                                                .then(Commands.argument("sss", IntegerArgumentType.integer(0, 999)).executes(WingsCommand::pity)))))
                        .then(Commands.literal("reset").executes(WingsCommand::gachaReset))
                        .then(Commands.literal("info").executes(WingsCommand::gachaInfo))
                        .then(Commands.literal("force")
                                .then(Commands.literal("x10").executes(ctx -> force(ctx, "x10")))
                                .then(Commands.literal("ss").executes(ctx -> force(ctx, "ss")))
                                .then(Commands.literal("sss").executes(ctx -> force(ctx, "sss")))
                                .then(Commands.literal("mythic").executes(ctx -> force(ctx, "mythic"))))
                        .then(Commands.literal("show")
                                .then(Commands.argument("animation", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(java.util.List.of("summon_b", "summon_a", "summon_s",
                                                "summon_ss", "summon_sss", "summon_mythic_night", "summon_mythic_light", "summon_x10"), builder))
                                        .executes(WingsCommand::gachaShow)))));
    }

    private static int feathers(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int count = IntegerArgumentType.getInteger(ctx, "count");
        while (count > 0) {
            int n = Math.min(64, count);
            ItemStack stack = new ItemStack(ModItems.SUMMONING_FEATHER.get(), n);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
            count -= n;
        }
        return 1;
    }

    private static int force(CommandContext<CommandSourceStack> ctx, String what) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        java.util.List<com.zorago.simplywings.gacha.GachaTier> tiers = switch (what) {
            case "x10" -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.B, com.zorago.simplywings.gacha.GachaTier.A,
                    com.zorago.simplywings.gacha.GachaTier.S, com.zorago.simplywings.gacha.GachaTier.SS, com.zorago.simplywings.gacha.GachaTier.SSS,
                    com.zorago.simplywings.gacha.GachaTier.MYTHIC, com.zorago.simplywings.gacha.GachaTier.B, com.zorago.simplywings.gacha.GachaTier.A,
                    com.zorago.simplywings.gacha.GachaTier.S, com.zorago.simplywings.gacha.GachaTier.SS);
            case "ss" -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.SS);
            case "sss" -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.SSS);
            default -> java.util.List.of(com.zorago.simplywings.gacha.GachaTier.MYTHIC);
        };
        com.zorago.simplywings.gacha.Gacha.FORCED.put(player.getUUID(), tiers);
        ctx.getSource().sendSuccess(() -> Component.literal("The next summon at the Draconic Altar is forced: " + tiers
                + " (no feather used). Open the altar and press any summon button."), false);
        return 1;
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
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ModAttachments.gacha(player).setPity(IntegerArgumentType.getInteger(ctx, "s"), IntegerArgumentType.getInteger(ctx, "ss"),
                IntegerArgumentType.getInteger(ctx, "sss"));
        return gachaInfo(ctx);
    }

    private static int gachaReset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ModAttachments.gacha(ctx.getSource().getPlayerOrException()).clear();
        return gachaInfo(ctx);
    }

    private static int gachaInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        GachaData data = ModAttachments.gacha(player);
        ctx.getSource().sendSuccess(() -> Component.literal("Pity S " + data.pity(GachaTier.S)
                + "/" + Gacha.HARD_S + ", SS " + data.pity(GachaTier.SS) + "/" + Gacha.HARD_SS
                + ", SSS " + data.pity(GachaTier.SSS) + "/" + Gacha.HARD_SSS
                + ", total " + data.total() + ", reserve " + String.format(Locale.ROOT, "%.3f", data.reserve())), false);
        return 1;
    }

    private static ResourceLocation wing(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "wing");
        if (WingRegistries.get(ctx.getSource().registryAccess(), id).isEmpty()) {
            throw UNKNOWN_WING.create(id.toString());
        }
        return id;
    }

    private static int unlock(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceLocation id = wing(ctx);
        ServerFlight.unlockAndEquip(player, id);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.unlock", id.toString()), false);
        return 1;
    }

    private static int unlockAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int added = ServerFlight.unlockAll(ctx.getSource().getPlayerOrException());
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.unlock_all", added), false);
        return added;
    }

    private static int equip(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceLocation id = wing(ctx);
        if (!ModAttachments.get(player).collection().contains(id)) {
            ctx.getSource().sendFailure(Component.translatable("commands.simplywings.not_owned", id.toString()));
            return 0;
        }
        ServerFlight.equip(player, Optional.of(id));
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.equip", id.toString()), false);
        return 1;
    }

    private static int unequip(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerFlight.equip(ctx.getSource().getPlayerOrException(), Optional.empty());
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.unequip"), false);
        return 1;
    }

    private static int stamina(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        float value = FloatArgumentType.getFloat(ctx, "value");
        ServerFlight.setStamina(ctx.getSource().getPlayerOrException(), value);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.stamina", value), false);
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerFlight.clear(ctx.getSource().getPlayerOrException());
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.reset"), false);
        return 1;
    }

    private static int sanctum(CommandContext<CommandSourceStack> ctx, EndSanctums.Sanctum sanctum) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerLevel end = ctx.getSource().getServer().getLevel(Level.END);
        BlockPos center = end == null || sanctum == null ? null : EndSanctums.place(end, sanctum);
        if (center == null) {
            ctx.getSource().sendFailure(Component.translatable("commands.simplywings.forge_failed"));
            return 0;
        }
        EndSanctums.give(player, end, sanctum, center);
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
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            ctx.getSource().sendFailure(Component.translatable("commands.simplywings.no_item"));
            return 0;
        }
        WingedEnergy.toggle(stack);
        boolean on = WingedEnergy.has(stack);
        ctx.getSource().sendSuccess(() -> Component.translatable(on ? "commands.simplywings.energy_on" : "commands.simplywings.energy_off",
                stack.getHoverName()), false);
        return 1;
    }

    private static int info(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        var data = ModAttachments.get(player);
        var stats = data.stats(player.registryAccess());
        String text = stats.map(s -> String.format(Locale.ROOT,
                "%s | stamina %.1f/%.1f | accel %.2f | vmax %.1f | vmin %.1f | handling %.0f | stab %.1f | mode %s",
                s.wing(), data.stamina(), s.stamina(), s.acceleration(), s.maxSpeed(), s.minSpeed(), s.handling(),
                s.stabilization(), data.mode())).orElse("-");
        ctx.getSource().sendSuccess(() -> Component.literal(text), false);
        return 1;
    }

    private WingsCommand() {
    }
}
