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
package com.peco2282.bcreborn.core.block.entity;

import com.peco2282.bcreborn.api.tiles.ITileAreaProvider;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlueMarkerBlockEntity extends MarkerBlockEntity implements ITileAreaProvider {
  public BlueMarkerBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesCore.BLUE_MARKER.get(), pos, state);
  }
}
