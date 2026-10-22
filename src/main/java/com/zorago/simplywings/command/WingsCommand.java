package com.zorago.simplywings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.flight.ServerFlight;
import com.zorago.simplywings.item.WingedEnergy;
import com.zorago.simplywings.structure.WingForge;
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
                .then(Commands.literal("forge").executes(WingsCommand::forge))
                .then(Commands.literal("energy").executes(WingsCommand::energy)));
    }

    private static ResourceLocation wing(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "wing");
        if (WingRegistries.get(ctx.getSource().registryAccess(), id).isEmpty()) {
            throw UNKNOWN_WING.create(id);
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

    private static int forge(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerLevel end = ctx.getSource().getServer().getLevel(Level.END);
        BlockPos center = end == null ? null : WingForge.place(end);
        if (center == null) {
            ctx.getSource().sendFailure(Component.translatable("commands.simplywings.forge_failed"));
            return 0;
        }
        WingForge.give(player, end, center);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.simplywings.forge", center.toShortString()), true);
        return 1;
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
        String text = stats.map(s -> String.format(java.util.Locale.ROOT,
                "%s | stamina %.1f/%.1f | accel %.2f | vmax %.1f | vmin %.1f | handling %.0f | stab %.1f | mode %s",
                s.wing(), data.stamina(), s.stamina(), s.acceleration(), s.maxSpeed(), s.minSpeed(), s.handling(),
                s.stabilization(), data.mode())).orElse("-");
        ctx.getSource().sendSuccess(() -> Component.literal(text), false);
        return 1;
    }

    private WingsCommand() {
    }
}
