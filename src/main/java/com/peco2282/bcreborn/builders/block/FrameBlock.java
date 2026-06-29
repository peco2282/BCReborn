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
package com.peco2282.bcreborn.builders.block;

import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FrameBlock extends BuildCraftBlock {
  public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
  public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
  public static final BooleanProperty EAST = BlockStateProperties.EAST;
  public static final BooleanProperty WEST = BlockStateProperties.WEST;
  public static final BooleanProperty UP = BlockStateProperties.UP;
  public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
  public static final Map<Direction, BooleanProperty> PROPERTY_MAP = new EnumMap<>(Direction.class);
  private static final ThreadLocal<Boolean> isRemovingFrames = new ThreadLocal<>();
  private static final float MIN = 0.25f;
  private static final float MAX = 0.75f;
  private static final VoxelShape CORE_SHAPE = Block.box(MIN * 16, MIN * 16, MIN * 16, MAX * 16, MAX * 16, MAX * 16);
  private static final Map<Direction, VoxelShape> SIDE_SHAPES = new EnumMap<>(Direction.class);

  static {
    PROPERTY_MAP.put(Direction.NORTH, NORTH);
    PROPERTY_MAP.put(Direction.SOUTH, SOUTH);
    PROPERTY_MAP.put(Direction.EAST, EAST);
    PROPERTY_MAP.put(Direction.WEST, WEST);
    PROPERTY_MAP.put(Direction.UP, UP);
    PROPERTY_MAP.put(Direction.DOWN, DOWN);
  }

  static {
    SIDE_SHAPES.put(Direction.NORTH, box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 4.0D));
    SIDE_SHAPES.put(Direction.SOUTH, box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 16.0D));
    SIDE_SHAPES.put(Direction.WEST, box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D));
    SIDE_SHAPES.put(Direction.EAST, box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D));
    SIDE_SHAPES.put(Direction.DOWN, box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D));
    SIDE_SHAPES.put(Direction.UP, box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D));
  }

  public FrameBlock() {
    super(Properties.of().noOcclusion());
    this.registerDefaultState(this.stateDefinition.any()
      .setValue(NORTH, false)
      .setValue(SOUTH, false)
      .setValue(EAST, false)
      .setValue(WEST, false)
      .setValue(UP, false)
      .setValue(DOWN, false));
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return null;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    VoxelShape shape = CORE_SHAPE;
    for (Direction direction : Direction.values()) {
      if (state.getValue(PROPERTY_MAP.get(direction))) {
        shape = Shapes.or(shape, SIDE_SHAPES.get(direction));
      }
    }
    return shape;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return getShape(state, level, pos, context);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = this.defaultBlockState();
    for (Direction direction : Direction.values()) {
      state = state.setValue(PROPERTY_MAP.get(direction), canConnectTo(context.getLevel(), context.getClickedPos(), direction));
    }
    return state;
  }

  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.level.LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
    return state.setValue(PROPERTY_MAP.get(direction), canConnectTo(level, currentPos, direction));
  }

  public boolean canConnectTo(net.minecraft.world.level.LevelAccessor level, BlockPos pos, Direction direction) {
    BlockPos neighborPos = pos.relative(direction);
    BlockState neighborState = level.getBlockState(neighborPos);
    return neighborState.getBlock() instanceof FrameBlock;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
    return true;
  }

  @Override
  public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
    return 1.0f;
  }

  // Drop nothing — frames are temporary scaffolding placed by the Quarry/Builder
  @Override
  public List<ItemStack> getDrops(BlockState state, Builder builder) {
    return new ArrayList<>();
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.is(newState.getBlock())) {
      super.onRemove(state, level, pos, newState, isMoving);
      return;
    }
    if (!level.isClientSide && isRemovingFrames.get() == null) {
      removeNeighboringFrames(level, pos);
    }
    super.onRemove(state, level, pos, newState, isMoving);
  }

  private void removeNeighboringFrames(Level level, BlockPos origin) {
    isRemovingFrames.set(true);
    try {
      Set<BlockPos> visited = new HashSet<>();
      Deque<BlockPos> queue = new ArrayDeque<>();
      queue.add(origin);
      visited.add(origin);

      while (!queue.isEmpty()) {
        BlockPos current = queue.poll();
        for (Direction dir : Direction.values()) {
          BlockPos neighbor = current.relative(dir);
          if (!visited.contains(neighbor) && level.isLoaded(neighbor)) {
            if (level.getBlockState(neighbor).getBlock() instanceof FrameBlock) {
              visited.add(neighbor);
              queue.add(neighbor);
              level.removeBlock(neighbor, false);
            }
          }
        }
      }
    } finally {
      isRemovingFrames.remove();
    }
  }
}
