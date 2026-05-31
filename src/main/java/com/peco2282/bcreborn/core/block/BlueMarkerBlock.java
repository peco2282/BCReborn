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
package com.peco2282.bcreborn.core.block;

import com.peco2282.bcreborn.common.block.MarkerBlock;
import com.peco2282.bcreborn.core.block.entity.BlueMarkerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlueMarkerBlock extends MarkerBlock {
  public BlueMarkerBlock() {
    super(Properties.of());
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return new BlueMarkerBlockEntity(p_153215_, p_153216_);
  }

  @Override
  public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
    if (p_60506_.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }

    BlockEntity be = p_60504_.getBlockEntity(p_60505_);
    if (be instanceof BlueMarkerBlockEntity marker) {
      marker.tryConnection();
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof BlueMarkerBlockEntity marker) {
      marker.updateSignals();
    }
    super.neighborChanged(state, level, pos, block, fromPos, isMoving);
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof BlueMarkerBlockEntity marker) {
        marker.destroy();
      }
      super.onRemove(state, level, pos, newState, isMoving);
    }
  }
}
