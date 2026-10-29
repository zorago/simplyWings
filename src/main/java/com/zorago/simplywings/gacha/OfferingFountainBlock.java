package com.zorago.simplywings.gacha;

import com.mojang.serialization.MapCodec;
import com.zorago.simplywings.sound.ModSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OfferingFountainBlock extends BaseEntityBlock {
    public static final MapCodec<OfferingFountainBlock> CODEC = simpleCodec(OfferingFountainBlock::new);
    public static final int SIZE = 5;
    public static final int CENTER = 2;
    public static final IntegerProperty PART_X = IntegerProperty.create("x", 0, SIZE - 1);
    public static final IntegerProperty PART_Z = IntegerProperty.create("z", 0, SIZE - 1);
    private static final VoxelShape[][] SHAPES = new VoxelShape[SIZE][SIZE];

    static {
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                int dx = x - CENTER;
                int dz = z - CENTER;
                SHAPES[x][z] = dx == 0 && dz == 0 ? Shapes.or(Block.box(0, 0, 0, 16, 12, 16), Block.box(3, 12, 3, 13, 16, 13))
                        : Block.box(dx == -CENTER ? 8 : 0, 0, dz == -CENTER ? 8 : 0, dx == CENTER ? 8 : 16, 12, dz == CENTER ? 8 : 16);
            }
        }
    }

    public OfferingFountainBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART_X, CENTER).setValue(PART_Z, CENTER));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART_X, PART_Z);
    }

    public static boolean isController(BlockState state) {
        return state.getValue(PART_X) == CENTER && state.getValue(PART_Z) == CENTER;
    }

    public static BlockPos controller(BlockPos pos, BlockState state) {
        return pos.offset(CENTER - state.getValue(PART_X), 0, CENTER - state.getValue(PART_Z));
    }

    public static List<BlockPos> parts(BlockPos controller) {
        List<BlockPos> out = new ArrayList<>();
        for (int dx = -CENTER; dx <= CENTER; dx++) {
            for (int dz = -CENTER; dz <= CENTER; dz++) {
                if (Math.abs(dx) == CENTER && Math.abs(dz) == CENTER) {
                    continue;
                }
                out.add(controller.offset(dx, 0, dz));
            }
        }
        return out;
    }

    private BlockState part(BlockPos controller, BlockPos pos) {
        return defaultBlockState().setValue(PART_X, pos.getX() - controller.getX() + CENTER)
                .setValue(PART_Z, pos.getZ() - controller.getZ() + CENTER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos origin = context.getClickedPos();
        Level level = context.getLevel();
        for (BlockPos pos : parts(origin)) {
            if (!pos.equals(origin) && !level.getBlockState(pos).canBeReplaced(context)) {
                if (context.getPlayer() != null && !level.isClientSide) {
                    context.getPlayer().displayClientMessage(Component.translatable("message.simplywings.fountain_no_room"), true);
                }
                return null;
            }
        }
        return defaultBlockState();
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide || !isController(state) || oldState.is(this)) {
            return;
        }
        for (BlockPos part : parts(pos)) {
            if (!part.equals(pos) && level.getBlockState(part).canBeReplaced()) {
                level.setBlock(part, part(pos, part), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (state.is(newState.getBlock())) {
            return;
        }
        BlockPos controller = controller(pos, state);
        for (BlockPos part : parts(controller)) {
            BlockState other = level.getBlockState(part);
            if (!part.equals(pos) && other.is(this) && controller(part, other).equals(controller)) {
                level.removeBlock(part, false);
            }
        }
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
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider((id, inventory, p) -> new FountainMenu(id, inventory, ContainerLevelAccess.create(level, controller)),
                    Component.translatable("container.simplywings.offering_fountain")), buf -> buf.writeBlockPos(controller));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(PART_X)][state.getValue(PART_Z)];
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
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return isController(state) ? new OfferingFountainBlockEntity(pos, state) : null;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!isController(state)) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;
        level.addParticle(ParticleTypes.SPLASH, x + (random.nextDouble() - 0.5) * 0.8, y + 1.7, z + (random.nextDouble() - 0.5) * 0.8,
                0, 0.2, 0);
        double a = random.nextDouble() * Math.PI * 2;
        level.addParticle(ParticleTypes.SPLASH, x + Math.cos(a) * 1.0, y + 0.65, z + Math.sin(a) * 1.0, 0, 0.1, 0);
        if (random.nextInt(2) == 0) {
            double b = random.nextDouble() * Math.PI * 2;
            level.addParticle(ParticleTypes.END_ROD, x + Math.cos(b) * 1.9, y + 1.0, z + Math.sin(b) * 1.9, 0, 0.04, 0);
        }
        if (random.nextInt(24) == 0) {
            level.playLocalSound(x, y + 1, z, ModSounds.FOUNTAIN_AMBIENT.get(), SoundSource.BLOCKS, 1f, 0.9f + random.nextFloat() * 0.2f, false);
        }
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.ENCHANT, x, y + 3.0, z, (random.nextDouble() - 0.5) * 2, -0.5, (random.nextDouble() - 0.5) * 2);
        }
    }
}
