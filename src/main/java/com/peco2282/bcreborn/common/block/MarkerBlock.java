/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public abstract class MarkerBlock extends BuildCraftBlock {
  private static final double W = 0.15;
  private static final double H = 0.65;

  private static final VoxelShape SHAPE_DOWN = Shapes.create(new AABB(0.5 - W, 1.0 - H, 0.5 - W, 0.5 + W, 1.0, 0.5 + W));
  private static final VoxelShape SHAPE_UP = Shapes.create(new AABB(0.5 - W, 0.0, 0.5 - W, 0.5 + W, H, 0.5 + W));
  private static final VoxelShape SHAPE_SOUTH = Shapes.create(new AABB(0.5 - W, 0.5 - W, 0.0, 0.5 + W, 0.5 + W, H));
  private static final VoxelShape SHAPE_NORTH = Shapes.create(new AABB(0.5 - W, 0.5 - W, 1.0 - H, 0.5 + W, 0.5 + W, 1.0));
  private static final VoxelShape SHAPE_EAST = Shapes.create(new AABB(0.0, 0.5 - W, 0.5 - W, H, 0.5 + W, 0.5 + W));
  private static final VoxelShape SHAPE_WEST = Shapes.create(new AABB(1.0 - H, 0.5 - W, 0.5 - W, 1.0, 0.5 + W, 0.5 + W));

  public MarkerBlock(Properties properties) {
    super(properties.lightLevel(state -> 8).noCollission().instabreak());
  }

  private static VoxelShape getShapeForDirection(Direction dir) {
    return switch (dir) {
      case DOWN -> SHAPE_DOWN;
      case UP -> SHAPE_UP;
      case SOUTH -> SHAPE_SOUTH;
      case NORTH -> SHAPE_NORTH;
      case EAST -> SHAPE_EAST;
      case WEST -> SHAPE_WEST;
    };
  }

  // -----------------------------------------------------------------------
  // Shape
  // -----------------------------------------------------------------------

  /**
   * Returns true if the block at {@code pos} can support a marker attached on {@code side}.
   * Mirrors BuildCraft's canPlaceTorch logic.
   */
  public static boolean canPlaceMarker(LevelReader level, BlockPos pos, Direction side) {
    BlockState state = level.getBlockState(pos);
    return state.isFaceSturdy(level, pos, side);
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  // -----------------------------------------------------------------------
  // Placement
  // -----------------------------------------------------------------------

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return getShapeForDirection(state.getValue(FACING));
  }

  @Override
  public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    Direction facing = state.getValue(FACING);
    // The support block is one step in the opposite direction of FACING
    BlockPos supportPos = pos.relative(facing.getOpposite());
    return canPlaceMarker(level, supportPos, facing);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    // Use the clicked face as the FACING direction (marker points away from the wall)
    Direction facing = context.getClickedFace();
    BlockState state = defaultBlockState().setValue(FACING, facing);
    if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
      return state;
    }
    // Try all directions as fallback
    for (Direction dir : Direction.values()) {
      BlockState candidate = defaultBlockState().setValue(FACING, dir);
      if (candidate.canSurvive(context.getLevel(), context.getClickedPos())) {
        return candidate;
      }
    }
    return null;
  }

  // -----------------------------------------------------------------------
  // Neighbor change — drop if support is gone
  // -----------------------------------------------------------------------

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    if (!state.canSurvive(level, pos)) {
      dropResources(state, level, pos);
      level.removeBlock(pos, false);
    }
  }
}
