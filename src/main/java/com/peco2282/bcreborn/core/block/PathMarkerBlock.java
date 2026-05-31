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
import com.peco2282.bcreborn.core.block.entity.PathMarkerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class PathMarkerBlock extends MarkerBlock {
  public PathMarkerBlock() {
    super(Properties.of());
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return new PathMarkerBlockEntity(p_153215_, p_153216_);
  }
}
