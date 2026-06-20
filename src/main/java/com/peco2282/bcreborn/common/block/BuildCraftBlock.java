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

import com.peco2282.bcreborn.api.blocks.IRotatable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public abstract class BuildCraftBlock extends BaseEntityBlock implements IRotatable {
  public static final DirectionProperty FACING = BlockStateProperties.FACING;
  public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

  public BuildCraftBlock(Properties p_49795_) {
    super(p_49795_);
  }

  public abstract void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_);

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
    var state = super.getStateForPlacement(p_49820_);
    if (this.isHorizontalRotatable()) {
      return state.setValue(HORIZONTAL_FACING, p_49820_.getHorizontalDirection().getOpposite());
    }
    if (this.isRotatable()) {
      return state.setValue(FACING, p_49820_.getNearestLookingDirection().getOpposite());
    }
    return state;
  }

  @Override
  public RenderShape getRenderShape(BlockState p_49232_) {
    return RenderShape.MODEL;
  }

  @Override
  public boolean isRotatable() {
    return true;
  }

  @Override
  public boolean isHorizontalRotatable() {
    return false;
  }

  @Override
  public @Nullable DirectionProperty getDirectionProperty() {
    return isHorizontalRotatable() ? HORIZONTAL_FACING : isRotatable() ? FACING : null;
  }
}
