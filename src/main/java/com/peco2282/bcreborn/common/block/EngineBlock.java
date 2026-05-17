package com.peco2282.bcreborn.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class EngineBlock extends BuildCraftBlock {
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
  public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());

  public static final VoxelShape SHAPE_UP =
      Shapes.or(box(0, 0, 0, 16, 4, 16), box(4, 4, 4, 12, 16, 12));
  public static final VoxelShape SHAPE_DOWN =
      Shapes.or(box(0, 12, 0, 16, 16, 16), box(4, 0, 4, 12, 12, 12));
  public static final VoxelShape SHAPE_SOUTH =
      Shapes.or(box(0, 0, 0, 16, 16, 4), box(4, 4, 4, 12, 12, 16));
  public static final VoxelShape SHAPE_NORTH =
      Shapes.or(box(4, 4, 0, 12, 12, 12), box(0, 0, 12, 16, 16, 16));
  public static final VoxelShape SHAPE_EAST =
      Shapes.or(box(0, 0, 0, 4, 16, 16), box(4, 4, 4, 16, 12, 12));
  public static final VoxelShape SHAPE_WEST =
      Shapes.or(box(0, 4, 4, 12, 12, 12), box(12, 0, 0, 16, 16, 16));

  public EngineBlock(Properties p_49795_) {
    super(p_49795_);
    this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false).setValue(FACING, Direction.UP));
  }

  public EngineBlock() {
    this(Properties.of().noOcclusion());
  }

  @Override
  public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
    Direction direction = p_60555_.getValue(FACING);
    return switch (direction) {
      case DOWN -> SHAPE_DOWN;
      case UP -> SHAPE_UP;
      case NORTH -> SHAPE_NORTH;
      case SOUTH -> SHAPE_SOUTH;
      case WEST -> SHAPE_WEST;
      case EAST -> SHAPE_EAST;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState p_49232_) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    p_49915_.add(ACTIVE, FACING);
  }

  @Override
  public boolean isRotatable() {
    return true;
  }

  @Override
  public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
    return Shapes.block();
  }
}
