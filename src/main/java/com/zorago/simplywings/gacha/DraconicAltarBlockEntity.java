package com.zorago.simplywings.gacha;

import com.zorago.simplywings.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DraconicAltarBlockEntity extends BlockEntity {
    public DraconicAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DRACONIC_ALTAR_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DraconicAltarBlockEntity altar) {
        if (level instanceof ServerLevel server && (level.getGameTime() + pos.asLong()) % 100 == 0) {
            AltarShows.flameLights(server, pos, state, false);
        }
    }
}
