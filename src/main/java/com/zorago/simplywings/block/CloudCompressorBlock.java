package com.zorago.simplywings.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CloudCompressorBlock extends BaseEntityBlock {
    public static final MapCodec<CloudCompressorBlock> CODEC = simpleCodec(CloudCompressorBlock::new);
    public static final int WIDTH = 3;
    public static final int HEIGHT = 5;
    public static final IntegerProperty PART_X = IntegerProperty.create("x", 0, WIDTH - 1);
    public static final IntegerProperty PART_Y = IntegerProperty.create("y", 0, HEIGHT - 1);
    public static final IntegerProperty PART_Z = IntegerProperty.create("z", 0, WIDTH - 1);

    public CloudCompressorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART_X, 1).setValue(PART_Y, 0).setValue(PART_Z, 1));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART_X, PART_Y, PART_Z);
    }

    public static boolean isController(BlockState state) {
        return state.getValue(PART_X) == 1 && state.getValue(PART_Y) == 0 && state.getValue(PART_Z) == 1;
    }

    public static BlockPos controller(BlockPos pos, BlockState state) {
        return pos.offset(1 - state.getValue(PART_X), -state.getValue(PART_Y), 1 - state.getValue(PART_Z));
    }

    public static Iterable<BlockPos> parts(BlockPos controller) {
        return BlockPos.betweenClosed(controller.offset(-1, 0, -1), controller.offset(1, HEIGHT - 1, 1));
    }

    public BlockState part(BlockPos controller, BlockPos pos) {
        return defaultBlockState()
                .setValue(PART_X, pos.getX() - controller.getX() + 1)
                .setValue(PART_Y, pos.getY() - controller.getY())
                .setValue(PART_Z, pos.getZ() - controller.getZ() + 1);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos origin = context.getClickedPos();
        Level level = context.getLevel();
        if (origin.getY() + HEIGHT - 1 >= level.getMaxBuildHeight()) {
            return null;
        }
        for (BlockPos pos : parts(origin)) {
            if (!pos.equals(origin) && !level.getBlockState(pos).canBeReplaced(context)) {
                return null;
            }
        }
        return defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            assemble(level, pos);
        }
    }

    public void assemble(LevelAccessor level, BlockPos controller) {
        for (BlockPos pos : parts(controller)) {
            if (!pos.equals(controller)) {
                level.setBlock(pos.immutable(), part(controller, pos), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockPos controller = controller(pos, state);
            if (isController(state) && level.getBlockEntity(pos) instanceof CloudCompressorBlockEntity compressor) {
                compressor.dropContents();
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
            for (BlockPos part : parts(controller)) {
                BlockState other = level.getBlockState(part);
                if (!part.equals(pos) && other.is(this) && controller(part, other).equals(controller)) {
                    level.removeBlock(part.immutable(), false);
                }
            }
            return;
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos controller = controller(pos, state);
        if (!level.isClientSide && !isController(state) && !player.isCreative()) {
            BlockState main = level.getBlockState(controller);
            if (main.is(this)) {
                Block.dropResources(main, level, controller, level.getBlockEntity(controller), player, player.getMainHandItem());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos controller = controller(pos, state);
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(controller) instanceof CloudCompressorBlockEntity compressor) {
            serverPlayer.openMenu(compressor, buf -> buf.writeBlockPos(controller));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return isController(state) ? new CloudCompressorBlockEntity(pos, state) : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlocks.CLOUD_COMPRESSOR_ENTITY.get(),
                level.isClientSide ? CloudCompressorBlockEntity::clientTick : CloudCompressorBlockEntity::serverTick);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
