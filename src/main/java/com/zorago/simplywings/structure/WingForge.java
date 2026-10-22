package com.zorago.simplywings.structure;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.item.ModItems;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class WingForge {
    private static final ResourceLocation TEMPLATE = SimplyWings.id("wing_forge");
    private static final String DATA_NAME = SimplyWings.MOD_ID + "_wing_forge";

    public static void register(IEventBus bus) {
        bus.addListener(WingForge::onDeath);
    }

    private static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon) || !(event.getEntity().level() instanceof ServerLevel level)
                || level.dimension() != Level.END) {
            return;
        }
        EndDragonFight fight = level.getDragonFight();
        if (fight == null || !fight.hasPreviouslyKilledDragon()) {
            return;
        }
        BlockPos center = place(level);
        if (center != null) {
            giveCompasses(level, center);
        }
    }

    public static BlockPos place(ServerLevel level) {
        Data data = level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(Data::new, Data::load, null), DATA_NAME);
        if (data.center == null) {
            Random random = new Random(level.getSeed() ^ 0x57494E47L);
            double angle = random.nextDouble() * Math.PI * 2;
            int radius = ServerConfig.FORGE_RADIUS.get();
            data.center = new BlockPos((int) Math.round(Math.cos(angle) * radius), ServerConfig.FORGE_Y.get(),
                    (int) Math.round(Math.sin(angle) * radius));
            data.setDirty();
        }
        StructureTemplate template = level.getStructureManager().getOrCreate(TEMPLATE);
        Vec3i size = template.getSize();
        if (size.getX() == 0) {
            SimplyWings.LOGGER.error("Missing structure template {}", TEMPLATE);
            return null;
        }
        BlockPos origin = data.center.offset(-size.getX() / 2, 0, -size.getZ() / 2);
        template.placeInWorld(level, origin, origin, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_CLIENTS);
        return data.center.above(2);
    }

    public static void giveCompasses(ServerLevel level, BlockPos center) {
        double radius = ServerConfig.FORGE_ISLAND_RADIUS.get();
        for (ServerPlayer player : level.players()) {
            if (player.getX() * player.getX() + player.getZ() * player.getZ() <= radius * radius) {
                give(player, level, center);
            }
        }
    }

    public static void give(ServerPlayer player, ServerLevel level, BlockPos center) {
        ItemStack compass = new ItemStack(ModItems.FORGE_COMPASS.get());
        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), center)), false));
        if (!player.getInventory().add(compass)) {
            player.drop(compass, false);
        }
        player.displayClientMessage(Component.translatable("message.simplywings.forge_compass"), false);
    }

    private static final class Data extends SavedData {
        private BlockPos center;

        static Data load(CompoundTag tag, HolderLookup.Provider registries) {
            Data data = new Data();
            data.center = NbtUtils.readBlockPos(tag, "center").orElse(null);
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            if (center != null) {
                tag.put("center", NbtUtils.writeBlockPos(center));
            }
            return tag;
        }
    }

    private WingForge() {
    }
}
