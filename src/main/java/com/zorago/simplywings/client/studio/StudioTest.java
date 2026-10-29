package com.zorago.simplywings.client.studio;

import com.zorago.simplywings.client.ClientFlight;
import com.zorago.simplywings.client.ClientWings;
import com.zorago.simplywings.client.gui.WingsScreen;
import com.zorago.simplywings.client.wiki.WikiData;
import com.zorago.simplywings.client.wiki.WikiScreen;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

final class StudioTest {
    private static final Map<KeyMapping, Integer> HELD = new HashMap<>();
    private static final Map<com.mojang.blaze3d.platform.InputConstants.Key, Integer> PRESSED = new HashMap<>();
    private static boolean soundLog;

    static boolean run(Minecraft mc, LocalPlayer player, String[] a, String line, Consumer<String> log) {
        switch (a[0].toLowerCase(Locale.ROOT)) {
            case "screen" -> {
                switch (a[1]) {
                    case "inventory" -> mc.setScreen(new InventoryScreen(player));
                    case "wings" -> mc.setScreen(new WingsScreen());
                    case "close" -> {
                        if (mc.screen != null) {
                            mc.screen.onClose();
                        }
                        mc.setScreen(null);
                    }
                    default -> throw new IllegalArgumentException("Unknown screen " + a[1]);
                }
            }
            case "tooltips" -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 1; i < a.length; i++) {
                    stacks.add(stack(a[i]));
                }
                mc.setScreen(new TooltipBoard(stacks));
            }
            case "slot" -> {
                AbstractContainerMenu menu = player.containerMenu;
                int slot = Integer.parseInt(a[1]);
                int button = a.length > 2 ? Integer.parseInt(a[2]) : 0;
                ClickType type = a.length > 3 ? ClickType.valueOf(a[3].toUpperCase(Locale.ROOT)) : ClickType.PICKUP;
                mc.gameMode.handleInventoryMouseClick(menu.containerId, slot, button, type, player);
            }
            case "button" -> mc.gameMode.handleInventoryButtonClick(player.containerMenu.containerId, Integer.parseInt(a[1]));
            case "menu" -> {
                AbstractContainerMenu menu = player.containerMenu;
                StringBuilder out = new StringBuilder("menu " + menu.getClass().getSimpleName() + " carried="
                        + describe(menu.getCarried()));
                for (Slot slot : menu.slots) {
                    if (slot.hasItem() && (a.length < 2 || slot.index < Integer.parseInt(a[1]))) {
                        out.append(" | ").append(slot.index).append('=').append(describe(slot.getItem()));
                    }
                }
                log.accept(out.toString());
            }
            case "inv" -> {
                StringBuilder out = new StringBuilder("inv");
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        out.append(" | ").append(i).append('=').append(describe(stack));
                    }
                }
                log.accept(out.toString());
            }
            case "tip" -> {
                ItemStack stack = a.length > 1 ? stack(a[1]) : player.getMainHandItem();
                List<Component> lines = stack.getTooltipLines(net.minecraft.world.item.Item.TooltipContext.of(mc.level), player, TooltipFlag.NORMAL);
                StringBuilder out = new StringBuilder("tip " + describe(stack));
                for (Component c : lines) {
                    out.append(" || ").append(c.getString());
                }
                log.accept(out.toString());
            }
            case "tab" -> {
                net.minecraft.world.item.CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(ResourceLocation.parse(a[1]));
                tab.buildContents(new net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters(
                        player.connection.enabledFeatures(), true, player.level().registryAccess()));
                StringBuilder out = new StringBuilder("tab " + a[1] + " size=" + tab.getDisplayItems().size());
                for (ItemStack stack : tab.getDisplayItems()) {
                    out.append(' ').append(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());
                }
                log.accept(out.toString());
            }
            case "icons" -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (net.minecraft.world.item.Item item : BuiltInRegistries.ITEM) {
                    if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(a[1])) {
                        stacks.add(new ItemStack(item));
                    }
                }
                int from = a.length > 2 ? Integer.parseInt(a[2]) : 0;
                mc.setScreen(new IconBoard(stacks.subList(Math.min(from, stacks.size()), stacks.size())));
                log.accept("icons " + stacks.size());
            }
            case "drops" -> {
                double range = a.length > 1 ? Double.parseDouble(a[1]) : 8.0;
                Map<String, Integer> counts = new java.util.TreeMap<>();
                for (net.minecraft.world.entity.item.ItemEntity item : player.level().getEntitiesOfClass(
                        net.minecraft.world.entity.item.ItemEntity.class, player.getBoundingBox().inflate(range))) {
                    counts.merge(BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString(), item.getItem().getCount(), Integer::sum);
                }
                log.accept("drops " + counts);
            }
            case "sprint" -> player.setSprinting(a[1].equals("on"));
            case "jei", "jeitype" -> {
                if (!net.neoforged.fml.ModList.get().isLoaded("jei")) {
                    log.accept("jei absent");
                    return true;
                }
                try {
                    Class<?> plugin = Class.forName("com.zorago.simplywings.compat.jei.SimplyWingsJeiPlugin");
                    Object ok = a[0].equals("jei")
                            ? plugin.getMethod("showItem", ItemStack.class, boolean.class).invoke(null, stack(a[1]), a.length > 2 && a[2].equals("uses"))
                            : plugin.getMethod("showCategory", String.class).invoke(null, a[1]);
                    log.accept(a[0] + " " + a[1] + " " + ok + " screen=" + (mc.screen == null ? "-" : mc.screen.getClass().getSimpleName()));
                } catch (ReflectiveOperationException e) {
                    log.accept("jei error " + e);
                }
            }
            case "newworld" -> {
                String name = line.substring(line.indexOf(' ') + 1).trim();
                mc.level.disconnect();
                mc.disconnect();
                mc.createWorldOpenFlows().createFreshLevel(name,
                        new net.minecraft.world.level.LevelSettings(name, net.minecraft.world.level.GameType.CREATIVE, false,
                                net.minecraft.world.Difficulty.PEACEFUL, true, new net.minecraft.world.level.GameRules(),
                                net.minecraft.world.level.WorldDataConfiguration.DEFAULT),
                        new net.minecraft.world.level.levelgen.WorldOptions(0L, false, false),
                        access -> access.registryOrThrow(net.minecraft.core.registries.Registries.WORLD_PRESET)
                                .getHolderOrThrow(net.minecraft.world.level.levelgen.presets.WorldPresets.FLAT).value().createWorldDimensions(),
                        new net.minecraft.client.gui.screens.TitleScreen());
                log.accept("newworld " + name);
            }
            case "respawn" -> {
                player.respawn();
                mc.setScreen(null);
            }
            case "select" -> player.getInventory().selected = Integer.parseInt(a[1]);
            case "attack" -> {
                double range = a.length > 1 ? Double.parseDouble(a[1]) : 6.0;
                String type = a.length > 2 ? a[2] : null;
                LivingEntity target = player.level().getEntitiesOfClass(LivingEntity.class,
                                player.getBoundingBox().inflate(range), e -> e != player && !(e instanceof Player) && e.isAlive()
                                        && (type == null || BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString().equals(type)))
                        .stream().min(Comparator.comparingDouble(player::distanceToSqr)).orElse(null);
                if (target == null) {
                    log.accept("attack none");
                } else {
                    mc.gameMode.attack(player, target);
                    player.swing(InteractionHand.MAIN_HAND);
                    log.accept("attack " + BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()));
                }
            }
            case "use" -> mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            case "useon" -> {
                BlockPos pos = new BlockPos(Integer.parseInt(a[1]), Integer.parseInt(a[2]), Integer.parseInt(a[3]));
                BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
                mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
            }
            case "pull" -> net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                    new com.zorago.simplywings.network.Payloads.GachaPull(Integer.parseInt(a[1])));
            case "click" -> {
                if (mc.screen != null) {
                    double sx = mc.getWindow().getGuiScaledWidth() * Double.parseDouble(a[1]);
                    double sy = mc.getWindow().getGuiScaledHeight() * Double.parseDouble(a[2]);
                    mc.screen.mouseClicked(sx, sy, 0);
                }
            }
            case "look" -> {
                player.setYRot(Float.parseFloat(a[1]));
                player.setXRot(Float.parseFloat(a[2]));
            }
            case "key" -> {
                KeyMapping key = key(mc, a[1]);
                int ticks = a.length > 2 ? Integer.parseInt(a[2]) : 1;
                KeyMapping.click(key.getKey());
                key.setDown(true);
                HELD.put(key, ticks);
            }
            case "soundlog" -> {
                soundLog = a[1].equals("on");
                log.accept("soundlog " + soundLog);
            }
            case "wikipages" -> {
                List<String> ids = WikiData.ids();
                log.accept("wikipages " + ids.size() + " " + String.join(",", ids));
            }
            case "wikicheck" -> {
                int failed = 0;
                List<String> ids = a.length > 1 ? List.of(a[1].split(",")) : WikiData.ids();
                for (String id : ids) {
                    try {
                        WikiScreen screen = new WikiScreen(null, id);
                        screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                        GuiGraphics g = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
                        screen.render(g, 0, 0, 0f);
                        g.flush();
                    } catch (RuntimeException e) {
                        failed++;
                        log.accept("wikifail " + id + " " + e);
                    }
                }
                log.accept("wikicheck " + ids.size() + " pages, " + failed + " failed");
            }
            case "flight" -> log.accept("flight " + ClientFlight.debugState());
            case "keys" -> {
                StringBuilder out = new StringBuilder("keys");
                for (KeyMapping k : new KeyMapping[]{com.zorago.simplywings.client.ModKeys.UP, com.zorago.simplywings.client.ModKeys.DOWN,
                        com.zorago.simplywings.client.ModKeys.LEFT, com.zorago.simplywings.client.ModKeys.RIGHT,
                        com.zorago.simplywings.client.ModKeys.ACCELERATE, com.zorago.simplywings.client.ModKeys.BRAKE}) {
                    out.append(' ').append(k.getName()).append('=').append(k.getKey().getName()).append('/').append(k.getKeyModifier())
                            .append(" isDown=").append(k.isDown()).append(" down=").append(com.zorago.simplywings.client.ModKeys.down(k));
                }
                log.accept(out.toString());
            }
            case "press" -> {
                com.mojang.blaze3d.platform.InputConstants.Key key = com.mojang.blaze3d.platform.InputConstants.getKey("key.keyboard." + a[1]);
                int ticks = a.length > 2 ? Integer.parseInt(a[2]) : 1;
                KeyMapping.set(key, true);
                KeyMapping.click(key);
                PRESSED.put(key, ticks);
                log.accept("press " + a[1] + " " + ticks);
            }
            case "cape" -> {
                net.minecraft.client.multiplayer.PlayerInfo info = mc.getConnection().getPlayerInfo(player.getUUID());
                try {
                    java.lang.reflect.Field field = net.minecraft.client.multiplayer.PlayerInfo.class.getDeclaredField("skinLookup");
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    java.util.function.Supplier<net.minecraft.client.resources.PlayerSkin> original =
                            (java.util.function.Supplier<net.minecraft.client.resources.PlayerSkin>) field.get(info);
                    boolean on = a[1].equals("on");
                    ResourceLocation cape = ResourceLocation.parse(a.length > 2 ? a[2] : "minecraft:textures/entity/elytra.png");
                    field.set(info, (java.util.function.Supplier<net.minecraft.client.resources.PlayerSkin>) () -> {
                        net.minecraft.client.resources.PlayerSkin skin = original.get();
                        return new net.minecraft.client.resources.PlayerSkin(skin.texture(), skin.textureUrl(), on ? cape : null,
                                skin.elytraTexture(), skin.model(), skin.secure());
                    });
                    log.accept("cape " + on + " " + cape);
                } catch (ReflectiveOperationException e) {
                    log.accept("cape error " + e);
                }
            }
            case "status" -> log.accept(String.format(Locale.ROOT,
                    "status pos=%.2f,%.2f,%.2f vel=%.3f,%.3f,%.3f ground=%s mode=%s flags=%d stamina=%.1f rescue=%d equipped=%s screen=%s",
                    player.getX(), player.getY(), player.getZ(), player.getDeltaMovement().x, player.getDeltaMovement().y,
                    player.getDeltaMovement().z, player.onGround(), ClientFlight.mode(), ClientFlight.flags(),
                    ClientWings.stamina(), ClientWings.rescueJumps(), ClientWings.equipped().map(Object::toString).orElse("-"),
                    mc.screen == null ? "-" : mc.screen.getClass().getSimpleName()));
            default -> {
                return false;
            }
        }
        return true;
    }

    static void tick() {
        PRESSED.entrySet().removeIf(entry -> {
            int left = entry.getValue() - 1;
            if (left <= 0) {
                KeyMapping.set(entry.getKey(), false);
                return true;
            }
            entry.setValue(left);
            KeyMapping.set(entry.getKey(), true);
            return false;
        });
        HELD.entrySet().removeIf(entry -> {
            int left = entry.getValue() - 1;
            if (left <= 0) {
                entry.getKey().setDown(false);
                return true;
            }
            entry.setValue(left);
            return false;
        });
    }

    static void sound(PlaySoundEvent event, Consumer<String> log) {
        if (!soundLog || event.getOriginalSound() == null) {
            return;
        }
        SoundInstance s = event.getOriginalSound();
        log.accept(String.format(Locale.ROOT, "sound %s %s at=%.1f,%.1f,%.1f relative=%s played=%s", s.getLocation(),
                s.getSource().getName(), s.getX(), s.getY(), s.getZ(), s.isRelative(), event.getSound() != null));
    }

    static void chat(ClientChatReceivedEvent event, Consumer<String> log) {
        log.accept("chat " + event.getMessage().getString().replace('\n', ' '));
    }

    private static KeyMapping key(Minecraft mc, String name) {
        for (KeyMapping key : mc.options.keyMappings) {
            if (key.getName().equals(name) || key.getName().endsWith("." + name)) {
                return key;
            }
        }
        throw new IllegalArgumentException("Unknown key " + name);
    }

    private static ItemStack stack(String id) {
        ResourceLocation rl = ResourceLocation.parse(id.contains(":") ? id : "simplywings:" + id);
        if (!BuiltInRegistries.ITEM.containsKey(rl)) {
            throw new IllegalArgumentException("Unknown item " + rl);
        }
        return new ItemStack(BuiltInRegistries.ITEM.get(rl));
    }

    private static String describe(ItemStack stack) {
        if (stack.isEmpty()) {
            return "empty";
        }
        String base = BuiltInRegistries.ITEM.getKey(stack.getItem()) + "x" + stack.getCount();
        String patch = stack.getComponentsPatch().isEmpty() ? "" : stack.getComponentsPatch().toString();
        return patch.isEmpty() ? base : base + patch;
    }

    private static final class IconBoard extends Screen {
        private final List<ItemStack> stacks;

        IconBoard(List<ItemStack> stacks) {
            super(Component.literal("Icons"));
            this.stacks = stacks;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.fill(0, 0, width, height, 0xFF8B8B8B);
            int columns = Math.max(1, (width - 8) / 40);
            graphics.pose().pushPose();
            graphics.pose().scale(2f, 2f, 1f);
            for (int i = 0; i < stacks.size(); i++) {
                int x = 4 + (i % columns) * 20;
                int y = 4 + (i / columns) * 20;
                graphics.renderItem(stacks.get(i), x, y);
            }
            graphics.pose().popPose();
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }

    private static final class TooltipBoard extends Screen {
        private final List<ItemStack> stacks;

        TooltipBoard(List<ItemStack> stacks) {
            super(Component.literal("Tooltips"));
            this.stacks = stacks;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            super.render(graphics, mouseX, mouseY, partialTick);
            int columns = Math.max(1, Math.min(stacks.size(), width / 230));
            int columnWidth = width / columns;
            for (int i = 0; i < stacks.size(); i++) {
                int x = 4 + (i % columns) * columnWidth;
                int y = 16 + (i / columns) * (height / Math.max(1, (stacks.size() + columns - 1) / columns));
                graphics.renderItem(stacks.get(i), x, y - 14);
                graphics.renderTooltip(font, stacks.get(i), x - 8, y + 4);
            }
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }

    private StudioTest() {
    }
}
