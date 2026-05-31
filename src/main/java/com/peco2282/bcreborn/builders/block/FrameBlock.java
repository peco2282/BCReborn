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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FrameBlock extends BuildCraftBlock {
  private static final ThreadLocal<Boolean> isRemovingFrames = new ThreadLocal<>();

  private static final float MIN = 0.25f;
  private static final float MAX = 0.75f;
  private static final VoxelShape SHAPE = Block.box(MIN * 16, MIN * 16, MIN * 16, MAX * 16, MAX * 16, MAX * 16);

  public FrameBlock() {
    super(Properties.of().noOcclusion());
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
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
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
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
