package com.zorago.simplywings.structure;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.item.ModItems;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class EndSanctums {
    private static final String DATA_NAME = SimplyWings.MOD_ID + "_sanctums";
    private static final BlockPos TEMPLATE_CENTER = new BlockPos(28, 18, 28);

    public enum Sanctum {
        SKY_SANCTUM("sky_sanctum", 2, ModItems.SANCTUM_COMPASS::get),
        WING_FORGE("wing_forge", 3, ModItems.FORGE_COMPASS::get),
        CLOUD_REFINERY("cloud_refinery", 4, ModItems.REFINERY_COMPASS::get);

        private final String id;
        private final int dragon;
        private final Supplier<Item> compass;

        Sanctum(String id, int dragon, Supplier<Item> compass) {
            this.id = id;
            this.dragon = dragon;
            this.compass = compass;
        }

        public String id() {
            return id;
        }

        public int dragon() {
            return dragon;
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
    }

    private static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon && event.getEntity().level() instanceof ServerLevel level
                && level.dimension() == Level.END) {
            dragonKilled(level);
        }
    }

    public static int dragonKilled(ServerLevel level) {
        Data data = data(level);
        EndDragonFight fight = level.getDragonFight();
        if (data.kills == 0 && fight != null && fight.hasPreviouslyKilledDragon()) {
            data.kills = 1;
        }
        data.kills++;
        data.setDirty();
        for (Sanctum sanctum : Sanctum.values()) {
            if (data.kills >= sanctum.dragon()) {
                BlockPos center = place(level, sanctum);
                if (center != null) {
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
            if (Double.isNaN(data.angle)) {
                data.angle = new Random(level.getSeed() ^ 0x57494E47L).nextDouble() * Math.PI * 2;
            }
            double angle = data.angle + sanctum.ordinal() * Math.PI * 2 / 3;
            int radius = ServerConfig.FORGE_RADIUS.get();
            data.centers[sanctum.ordinal()] = new BlockPos((int) Math.round(Math.cos(angle) * radius), ServerConfig.FORGE_Y.get(),
                    (int) Math.round(Math.sin(angle) * radius));
            data.setDirty();
        }
        return data.centers[sanctum.ordinal()];
    }

    public static BlockPos place(ServerLevel level, Sanctum sanctum) {
        ResourceLocation id = SimplyWings.id(sanctum.id());
        StructureTemplate template = level.getStructureManager().getOrCreate(id);
        if (template.getSize().getX() == 0) {
            SimplyWings.LOGGER.error("Missing structure template {}", id);
            return null;
        }
        BlockPos center = center(level, sanctum);
        BlockPos origin = center.subtract(TEMPLATE_CENTER);
        StructurePlaceSettings settings = new StructurePlaceSettings().addProcessor(new KeepExisting());
        template.placeInWorld(level, origin, origin, settings, level.getRandom(), Block.UPDATE_CLIENTS);
        return center.above();
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

    private static final class KeepExisting extends StructureProcessor {
        @Override
        public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos,
                                                                 StructureTemplate.StructureBlockInfo blockInfo,
                                                                 StructureTemplate.StructureBlockInfo relative,
                                                                 StructurePlaceSettings settings) {
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
        private final BlockPos[] centers = new BlockPos[Sanctum.values().length];

        static Data load(CompoundTag tag, HolderLookup.Provider registries) {
            Data data = new Data();
            data.kills = tag.getInt("kills");
            data.angle = tag.contains("angle") ? tag.getDouble("angle") : Double.NaN;
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
