package com.zorago.simplywings.gacha;

import com.zorago.simplywings.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class OfferingFountainBlockEntity extends BlockEntity {
    public OfferingFountainBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.OFFERING_FOUNTAIN_ENTITY.get(), pos, state);
    }

    public AABB renderBounds() {
        return new AABB(worldPosition).inflate(2.5, 0, 2.5).expandTowards(0, 3, 0);
    }
}
