package com.zorago.simplywings.client;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

public final class SoundCommand {
    private static final int GAP = 30;
    private static final int MAX = 128;
    private static final Deque<Entry> QUEUE = new ArrayDeque<>();
    private static int wait;

    private record Entry(ResourceLocation event, Sound sound, String label) {
    }

    private static final SuggestionProvider<CommandSourceStack> SOUNDS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(Minecraft.getInstance().getSoundManager().getAvailableSounds(), builder);

    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("simplywings")
                .then(Commands.literal("sound")
                        .then(Commands.literal("list")
                                .then(Commands.argument("group", StringArgumentType.greedyString())
                                        .executes(ctx -> list(ctx, StringArgumentType.getString(ctx, "group")))))
                        .then(Commands.literal("stop")
                                .executes(ctx -> stop(ctx)))
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .suggests(SOUNDS)
                                .executes(ctx -> play(ctx, 1f, 1f))
                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0.5f, 2f))
                                        .executes(ctx -> play(ctx, FloatArgumentType.getFloat(ctx, "pitch"), 1f))
                                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0f))
                                                .executes(ctx -> play(ctx, FloatArgumentType.getFloat(ctx, "pitch"),
                                                        FloatArgumentType.getFloat(ctx, "volume"))))))));
    }

    private static int play(CommandContext<CommandSourceStack> ctx, float pitch, float volume) {
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "id");
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        if (manager.getSoundEvent(id) == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown sound event: " + id));
            return 0;
        }
        manager.play(new SimpleSoundInstance(id, SoundSource.MASTER, volume, pitch, SoundInstance.createUnseededRandom(), false, 0,
                SoundInstance.Attenuation.NONE, 0, 0, 0, true));
        ctx.getSource().sendSystemMessage(Component.literal("Playing " + id + " (pitch " + pitch + ", volume " + volume + ")"));
        return 1;
    }

    private static int stop(CommandContext<CommandSourceStack> ctx) {
        QUEUE.clear();
        Minecraft.getInstance().getSoundManager().stop();
        ctx.getSource().sendSystemMessage(Component.literal("Sound list stopped"));
        return 1;
    }

    private static boolean matches(ResourceLocation id, String group) {
        String full = id.toString();
        String path = id.getPath();
        return full.startsWith(group) || path.startsWith(group) || path.contains("." + group);
    }

    private static int list(CommandContext<CommandSourceStack> ctx, String group) {
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        List<ResourceLocation> ids = new ArrayList<>();
        for (ResourceLocation id : manager.getAvailableSounds()) {
            if (matches(id, group)) {
                ids.add(id);
            }
        }
        ids.sort(null);
        QUEUE.clear();
        for (ResourceLocation id : ids) {
            WeighedSoundEvents events = manager.getSoundEvent(id);
            if (events == null) {
                continue;
            }
            Map<ResourceLocation, Sound> variations = new LinkedHashMap<>();
            int weight = Math.min(events.getWeight(), MAX);
            for (int v = 0; v < weight; v++) {
                int fixed = v;
                Sound sound = events.getSound(new LegacyRandomSource(0L) {
                    @Override
                    public int nextInt(int bound) {
                        return Math.min(fixed, bound - 1);
                    }
                });
                if (sound != SoundManager.EMPTY_SOUND && sound != SoundManager.INTENTIONALLY_EMPTY_SOUND) {
                    variations.putIfAbsent(sound.getLocation(), sound);
                }
            }
            int n = 0;
            for (Sound sound : variations.values()) {
                n++;
                if (QUEUE.size() < MAX) {
                    QUEUE.add(new Entry(id, sound, id + " " + n + "/" + variations.size() + " (" + sound.getLocation() + ")"));
                }
            }
        }
        wait = 0;
        if (QUEUE.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No sound event matches " + group));
            return 0;
        }
        ctx.getSource().sendSystemMessage(Component.literal(ids.size() + " events, " + QUEUE.size() + " variations queued"
                + (QUEUE.size() >= MAX ? " (capped at " + MAX + ")" : "")));
        return QUEUE.size();
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (QUEUE.isEmpty() || mc.player == null) {
            if (mc.player == null) {
                QUEUE.clear();
            }
            return;
        }
        if (wait-- > 0) {
            return;
        }
        Entry e = QUEUE.poll();
        mc.getSoundManager().play(new Variation(e.event(), e.sound()));
        mc.player.displayClientMessage(Component.literal(e.label()), false);
        wait = GAP;
    }

    private static final class Variation extends AbstractSoundInstance {
        private final Sound fixed;

        Variation(ResourceLocation event, Sound fixed) {
            super(event, SoundSource.MASTER, SoundInstance.createUnseededRandom());
            this.fixed = fixed;
            this.attenuation = SoundInstance.Attenuation.NONE;
            this.relative = true;
        }

        @Override
        public WeighedSoundEvents resolve(SoundManager manager) {
            WeighedSoundEvents events = super.resolve(manager);
            this.sound = fixed;
            return events;
        }
    }

    private SoundCommand() {
    }
}
