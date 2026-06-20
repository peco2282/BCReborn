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

import com.peco2282.bcreborn.api.tiles.IBlockEntityAreaProvider;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.core.CoreBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlueMarkerBlockEntity extends MarkerBlockEntity implements IBlockEntityAreaProvider {
  public BlueMarkerBlockEntity(BlockPos pos, BlockState state) {
    super(CoreBlockEntityTypes.BLUE_MARKER.get(), pos, state);
  }
}
