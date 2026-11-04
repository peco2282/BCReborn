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
package com.peco2282.bcreborn.core.properties;

import com.peco2282.bcreborn.api.core.IWorldProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public abstract class WorldProperty implements IWorldProperty {

  public Map<ResourceKey<Level>, DimensionProperty> properties = new HashMap<>();

  /**
   * Evaluates the property for a loaded position directly from its current block state; unloaded positions are
   * false (as in the original) and are never loaded.
   * <p>
   * The original cached results per chunk ({@link DimensionProperty}) and invalidated them through an
   * {@code IWorldAccess} block-update listener, which 1.20.1 does not have. Without invalidation the cache went
   * stale as soon as a block changed (and {@code putIfAbsent} returned null on first use), so results are no
   * longer cached.
   */
  @Override
  public boolean get(Level world, BlockPos pos) {
    if (!world.isLoaded(pos)) {
      return false;
    }
    return get(world, world.getBlockState(pos), pos);
  }

  @Override
  public void clear() {
    for (DimensionProperty p : properties.values()) {
      if (p != null) {
        p.clear();
      }
    }

    properties.clear();
  }

  protected abstract boolean get(BlockGetter blockAccess, BlockState state, BlockPos pos);
}
