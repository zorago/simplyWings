package com.zorago.simplywings.structure;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.sound.ModSounds;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class EndSanctums {
    private static final String DATA_NAME = SimplyWings.MOD_ID + "_sanctums";
    private static final BlockPos ISLAND_CENTER = new BlockPos(28, 18, 28);
    private static final int ISLAND_REACH = 36;
    private static final int BUILD_SPACING = 100;
    private static final int BUILD_Y = 0;

    public enum Sanctum {
        SKY_SANCTUM("sky_sanctum", 0, () -> 2, () -> 2, ModItems.SANCTUM_COMPASS::get, ISLAND_CENTER),
        WING_FORGE("wing_forge", 1, () -> 3, () -> 3, ModItems.FORGE_COMPASS::get, ISLAND_CENTER),
        CLOUD_REFINERY("cloud_refinery", 2, () -> 4, () -> 4, ModItems.REFINERY_COMPASS::get, ISLAND_CENTER),
        SUMMONING_SANCTUM("summoning_sanctum", -1, () -> 1, ServerConfig.SUMMONING_COMPASS_DRAGON::get,
                ModItems.SUMMONING_COMPASS::get, ISLAND_CENTER),
        OFFERING_FOUNTAIN("offering_fountain", 3, ServerConfig.FOUNTAIN_DRAGON::get, ServerConfig.FOUNTAIN_DRAGON::get,
                ModItems.FOUNTAIN_COMPASS::get, ISLAND_CENTER);

        private final String id;
        private final int corner;
        private final Supplier<Integer> dragon;
        private final Supplier<Integer> compassDragon;
        private final Supplier<Item> compass;
        private final BlockPos templateCenter;

        Sanctum(String id, int corner, Supplier<Integer> dragon, Supplier<Integer> compassDragon, Supplier<Item> compass,
                BlockPos templateCenter) {
            this.id = id;
            this.corner = corner;
            this.dragon = dragon;
            this.compassDragon = compassDragon;
            this.compass = compass;
            this.templateCenter = templateCenter;
        }

        public String id() {
            return id;
        }

        public int dragon() {
            return dragon.get();
        }

        public int compassDragon() {
            return compassDragon.get();
        }

        public boolean island() {
            return corner >= 0;
        }

        public Item compass() {
            return compass.get();
        }

        public static Optional<Sanctum> byId(String id) {
            for (Sanctum sanctum : values()) {
                if (sanctum.id.equals(id.toLowerCase(Locale.ROOT))) {
                    return Optional.of(sanctum);
                }
            }
            return Optional.empty();
        }
    }

    public static void register(IEventBus bus) {
        bus.addListener(EndSanctums::onDeath);
        bus.addListener(EndSanctums::onSpawn);
    }

    private static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon && event.getEntity().level() instanceof ServerLevel level
                && level.dimension() == Level.END) {
            dragonKilled(level);
        }
    }

    private static void onSpawn(FinalizeSpawnEvent event) {
        if (!(event.getEntity() instanceof EnderMan) || event.getSpawnType() != MobSpawnType.NATURAL
                && event.getSpawnType() != MobSpawnType.CHUNK_GENERATION) {
            return;
        }
        if (event.getLevel().getLevel().dimension() == Level.END && near(event.getLevel().getLevel(), event.getX(), event.getY(), event.getZ())) {
            event.setSpawnCancelled(true);
        }
    }

    public static boolean near(ServerLevel level, double x, double y, double z) {
        Data data = data(level);
        for (Sanctum sanctum : Sanctum.values()) {
            BlockPos center = data.centers[sanctum.ordinal()];
            if (center != null && Math.abs(x - center.getX()) <= ISLAND_REACH && Math.abs(z - center.getZ()) <= ISLAND_REACH
                    && y > center.getY() - 30 && y < center.getY() + 40) {
                return true;
            }
        }
        return false;
    }

    public static int dragonKilled(ServerLevel level) {
        Data data = data(level);
        EndDragonFight fight = level.getDragonFight();
        if (data.kills == 0 && fight != null && fight.hasPreviouslyKilledDragon()) {
            data.kills = 1;
        }
        data.kills++;
        data.setDirty();
        boolean rebuild = data.kills >= ServerConfig.REBUILD_DRAGON.get();
        for (Sanctum sanctum : Sanctum.values()) {
            if (data.kills >= sanctum.dragon()) {
                boolean fresh = data.centers[sanctum.ordinal()] == null;
                BlockPos center = place(level, sanctum, rebuild);
                if (fresh && center != null) {
                    for (ServerPlayer player : level.players()) {
                        player.playNotifySound(ModSounds.SANCTUM_APPEAR.get(), SoundSource.AMBIENT, 1f, 1f);
                    }
                }
                if (center != null && data.kills >= sanctum.compassDragon()) {
                    giveCompasses(level, sanctum, center);
                }
            }
        }
        return data.kills;
    }

    public static int kills(ServerLevel level) {
        return data(level).kills;
    }

    private static Data data(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(Data::new, Data::load, null), DATA_NAME);
    }

    public static BlockPos center(ServerLevel level, Sanctum sanctum) {
        Data data = data(level);
        if (data.centers[sanctum.ordinal()] == null) {
            if (!sanctum.island()) {
                int height = level.getStructureManager().getOrCreate(SimplyWings.id(sanctum.id())).getSize().getY();
                data.centers[sanctum.ordinal()] = new BlockPos(0, level.getMaxBuildHeight() - height + sanctum.templateCenter.getY(), 0);
                data.setDirty();
                return data.centers[sanctum.ordinal()];
            }
            if (Double.isNaN(data.angle)) {
                data.angle = new Random(level.getSeed() ^ 0x57494E47L).nextDouble() * Math.PI * 2;
                data.square = true;
            }
            double angle;
            if (data.square) {
                angle = data.angle + sanctum.corner * Math.PI / 2;
            } else if (sanctum.corner < 3) {
                angle = data.angle + sanctum.corner * Math.PI * 2 / 3;
            } else {
                angle = widestGap(level, data);
            }
            int radius = ServerConfig.FORGE_RADIUS.get();
            data.centers[sanctum.ordinal()] = new BlockPos((int) Math.round(Math.cos(angle) * radius), ServerConfig.FORGE_Y.get(),
                    (int) Math.round(Math.sin(angle) * radius));
            data.setDirty();
        }
        return data.centers[sanctum.ordinal()];
    }

    private static double widestGap(ServerLevel level, Data data) {
        java.util.List<Double> angles = new java.util.ArrayList<>();
        for (Sanctum other : Sanctum.values()) {
            if (other.island() && other.corner < 3) {
                BlockPos c = center(level, other);
                angles.add(Math.atan2(c.getZ(), c.getX()));
            }
        }
        java.util.Collections.sort(angles);
        double best = 0;
        double at = data.angle;
        for (int i = 0; i < angles.size(); i++) {
            double a = angles.get(i);
            double b = i + 1 < angles.size() ? angles.get(i + 1) : angles.get(0) + Math.PI * 2;
            if (b - a > best) {
                best = b - a;
                at = (a + b) / 2;
            }
        }
        return at;
    }

    public static BlockPos place(ServerLevel level, Sanctum sanctum) {
        return place(level, sanctum, true);
    }

    public static BlockPos place(ServerLevel level, Sanctum sanctum, boolean rebuild) {
        ResourceLocation id = SimplyWings.id(sanctum.id());
        StructureTemplate template = level.getStructureManager().getOrCreate(id);
        if (template.getSize().getX() == 0) {
            SimplyWings.LOGGER.error("Missing structure template {}", id);
            return null;
        }
        boolean first = data(level).centers[sanctum.ordinal()] == null;
        BlockPos center = center(level, sanctum);
        BlockPos origin = center.subtract(sanctum.templateCenter);
        StructurePlaceSettings settings = new StructurePlaceSettings().addProcessor(new Repair(rebuild || first));
        template.placeInWorld(level, origin, origin, settings, level.getRandom(), Block.UPDATE_CLIENTS);
        return center.above();
    }

    public static BlockPos buildOrigin(Sanctum sanctum) {
        return new BlockPos(sanctum.ordinal() * BUILD_SPACING, BUILD_Y, 0);
    }

    public static void buildWorld(ServerLevel level, Optional<Sanctum> only) {
        for (Sanctum sanctum : Sanctum.values()) {
            if (only.isPresent() && only.get() != sanctum) {
                continue;
            }
            StructureTemplate template = level.getStructureManager().getOrCreate(SimplyWings.id(sanctum.id()));
            Vec3i size = template.getSize();
            BlockPos origin = buildOrigin(sanctum);
            template.placeInWorld(level, origin, origin, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_CLIENTS);
            BlockPos min = origin.offset(-1, -1, -1);
            BlockPos max = origin.offset(size.getX(), size.getY(), size.getZ());
            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                int edges = (pos.getX() == min.getX() || pos.getX() == max.getX() ? 1 : 0)
                        + (pos.getY() == min.getY() || pos.getY() == max.getY() ? 1 : 0)
                        + (pos.getZ() == min.getZ() || pos.getZ() == max.getZ() ? 1 : 0);
                if (edges >= 2) {
                    level.setBlock(pos, Blocks.RED_CONCRETE.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
            BlockPos sign = new BlockPos(origin.getX() + size.getX() / 2, min.getY() + 1, max.getZ() + 2);
            level.setBlock(sign.below(), Blocks.RED_CONCRETE.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.setBlock(sign, Blocks.OAK_SIGN.defaultBlockState(), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(sign) instanceof SignBlockEntity entity) {
                entity.updateText(text -> text.setMessage(0, Component.literal(sanctum.id()))
                        .setMessage(1, Component.literal("/simplywings sanctum"))
                        .setMessage(2, Component.literal("export " + sanctum.id())), true);
            }
        }
    }

    public static Path export(ServerLevel level, Sanctum sanctum) {
        ResourceLocation id = SimplyWings.id(sanctum.id());
        StructureTemplate template = level.getStructureManager().getOrCreate(id);
        Vec3i size = template.getSize();
        template.fillFromWorld(level, buildOrigin(sanctum), size, false, Blocks.AIR);
        level.getStructureManager().save(id);
        return level.getServer().getWorldPath(LevelResource.GENERATED_DIR)
                .resolve(id.getNamespace()).resolve("structures").resolve(id.getPath() + ".nbt");
    }

    public static void giveCompasses(ServerLevel level, Sanctum sanctum, BlockPos center) {
        double radius = ServerConfig.FORGE_ISLAND_RADIUS.get();
        for (ServerPlayer player : level.players()) {
            if (player.getX() * player.getX() + player.getZ() * player.getZ() <= radius * radius
                    && !player.getInventory().contains(stack -> stack.is(sanctum.compass()))) {
                give(player, level, sanctum, center);
            }
        }
    }

    public static void give(ServerPlayer player, ServerLevel level, Sanctum sanctum, BlockPos center) {
        ItemStack compass = new ItemStack(sanctum.compass());
        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), center)), false));
        if (!player.getInventory().add(compass)) {
            player.drop(compass, false);
        }
        player.displayClientMessage(Component.translatable("message.simplywings." + sanctum.id()), false);
    }

    private static final class Repair extends StructureProcessor {
        private final boolean everything;

        private Repair(boolean everything) {
            this.everything = everything;
        }

        @Override
        public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos,
                                                                 StructureTemplate.StructureBlockInfo blockInfo,
                                                                 StructureTemplate.StructureBlockInfo relative,
                                                                 StructurePlaceSettings settings) {
            if (relative.nbt() != null && relative.state().is(Blocks.BARREL) && !relative.nbt().getList("Items", Tag.TAG_COMPOUND).isEmpty()) {
                return relative;
            }
            if (!everything && !SimplyWings.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(relative.state().getBlock()).getNamespace())) {
                return null;
            }
            return level.getBlockState(relative.pos()).is(relative.state().getBlock()) ? null : relative;
        }

        @Override
        protected StructureProcessorType<?> getType() {
            return StructureProcessorType.NOP;
        }
    }

    private static final class Data extends SavedData {
        private int kills;
        private double angle = Double.NaN;
        private boolean square;
        private final BlockPos[] centers = new BlockPos[Sanctum.values().length];

        static Data load(CompoundTag tag, HolderLookup.Provider registries) {
            Data data = new Data();
            data.kills = tag.getInt("kills");
            data.angle = tag.contains("angle") ? tag.getDouble("angle") : Double.NaN;
            data.square = tag.getBoolean("square");
            for (Sanctum sanctum : Sanctum.values()) {
                data.centers[sanctum.ordinal()] = NbtUtils.readBlockPos(tag, sanctum.id()).orElse(null);
            }
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            tag.putInt("kills", kills);
            if (!Double.isNaN(angle)) {
                tag.putDouble("angle", angle);
            }
            tag.putBoolean("square", square);
            for (Sanctum sanctum : Sanctum.values()) {
                if (centers[sanctum.ordinal()] != null) {
                    tag.put(sanctum.id(), NbtUtils.writeBlockPos(centers[sanctum.ordinal()]));
                }
            }
            return tag;
        }
    }

    private EndSanctums() {
    }
}
