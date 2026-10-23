package com.qevro.escalux.block;

import com.qevro.escalux.compat.CreateCompat;
import com.qevro.escalux.config.EscaluxConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EscalatorBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty LEFT_CONNECTED = BooleanProperty.create("left_connected");
    public static final BooleanProperty RIGHT_CONNECTED = BooleanProperty.create("right_connected");
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 4, 1, 2, 16, 15),
            Block.box(14, 4, 1, 16, 16, 15), Block.box(2, 4, 1, 14, 5, 15));
    private final MotionDirection motionDirection;
    public enum MotionDirection { UP, DOWN }

    public EscalatorBlock(MotionDirection direction) {
        super(Properties.of().mapColor(MapColor.METAL).strength(3.5F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());
        this.motionDirection = direction;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, true).setValue(LEFT_CONNECTED, false).setValue(RIGHT_CONNECTED, false));
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockState placed = defaultBlockState().setValue(FACING, facing);
        return updateConnections(placed, context.getLevel(), context.getClickedPos());
    }
    @Override public BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override public BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, ACTIVE, LEFT_CONNECTED, RIGHT_CONNECTED); }
    @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return updateConnections(state, level, pos);
    }

    private BlockState updateConnections(BlockState state, LevelAccessor level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        boolean left = level.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock() instanceof EscalatorBlock;
        boolean right = level.getBlockState(pos.relative(facing.getClockWise())).getBlock() instanceof EscalatorBlock;
        return state.setValue(LEFT_CONNECTED, left).setValue(RIGHT_CONNECTED, right);
    }

    @Override public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.getItemInHand(hand).is(com.qevro.escalux.item.EscaluxItems.MAINTENANCE_KEY.get())) {
            if (!level.isClientSide) {
                boolean next = !state.getValue(ACTIVE);
                level.setBlock(pos, state.setValue(ACTIVE, next), 3);
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(next ? "message.escalux.started" : "message.escalux.stopped"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !state.getValue(ACTIVE) || !entity.isAlive() || entity.isShiftKeyDown()) return;
        // Only boost entities whose feet are actually on the tread surface.
        if (entity.getY() < pos.getY() + 0.20D || entity.getY() > pos.getY() + 1.35D) return;
        Direction facing = state.getValue(FACING);
        double speed = EscaluxConfig.ESCALATOR_SPEED.get();
        if (CreateCompat.isBetaBridgeEnabled()) speed = CreateCompat.applyCreateSpeed(level, pos, speed);
        double sign = motionDirection == MotionDirection.UP ? 1.0D : -1.0D;
        entity.setDeltaMovement(entity.getDeltaMovement().x * 0.35D + facing.getStepX() * speed * sign,
                entity.getDeltaMovement().y + (motionDirection == MotionDirection.UP ? 0.035D : -0.015D),
                entity.getDeltaMovement().z * 0.35D + facing.getStepZ() * speed * sign);
        entity.hurtMarked = true;
        if (entity.tickCount % 40 == 0) level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CHAIN_STEP,
                net.minecraft.sounds.SoundSource.BLOCKS, 0.12F, 0.75F + level.random.nextFloat() * 0.15F);
    }
}
