package com.zorago.simplywings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ForgeBenchBlockEntity extends EssenceBenchBlockEntity {
    public static final int ENERGY = 1;
    public static final int GRID = 2;
    public static final int GRID_CELLS = 21;
    public static final int SIZE = GRID + GRID_CELLS;
    public static final int EVENT_COMPONENT = 1;
    public static final int EVENT_ENERGY = 3;

    public ForgeBenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.FORGE_BENCH_ENTITY.get(), pos, state, SIZE);
    }

    public void playComponentCraft() {
        playEvent(EVENT_COMPONENT);
    }

    public void playEnergy() {
        playEvent(EVENT_ENERGY);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ForgeBenchBlockEntity bench) {
        bench.tickServer();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ForgeBenchBlockEntity bench) {
        ForgeBenchEffects.tick(level, pos, state, bench);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simplywings.forge_bench");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ForgeBenchMenu(containerId, inventory, this);
    }
}
