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


import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.common.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class TileBuffer {

  private final SafeTimeTracker tracker = new SafeTimeTracker(20, 5);
  private final Level world;
  private final int x, y, z;
  private final boolean loadUnloaded;
  private Block block = null;
  private BlockEntity tile;

  public TileBuffer(Level world, int x, int y, int z, boolean loadUnloaded) {
    this.world = world;
    this.x = x;
    this.y = y;
    this.z = z;
    this.loadUnloaded = loadUnloaded;

    refresh();
  }

  public static TileBuffer[] makeBuffer(Level world, int x, int y, int z, boolean loadUnloaded) {
    TileBuffer[] buffer = new TileBuffer[6];

    for (int i = 0; i < 6; i++) {
      Direction d = Direction.from3DDataValue(i);
      buffer[i] = new TileBuffer(world, x + d.getStepX(), y + d.getStepY(), z + d.getStepZ(), loadUnloaded);
    }

    return buffer;
  }

  public void refresh() {
    tile = null;
    block = null;

    BlockPos pos = new BlockPos(x, y, z);
    if (!loadUnloaded && !world.isLoaded(pos)) {
      return;
    }

    block = world.getBlockState(pos).getBlock();
    tile = world.getBlockEntity(pos);
  }

  public void set(Block block, BlockEntity tile) {
    this.block = block;
    this.tile = tile;
    tracker.markTime(world);
  }

  private void tryRefresh() {
    if (Utils.CAULDRON_DETECTED || (tile != null && tile.isRemoved()) || (tile == null && tracker.markTimeIfDelay(world))) {
      refresh();
    }
  }

  public Block getBlock() {
    tryRefresh();

    return block;
  }

  public BlockEntity getTile() {
    return getTile(false);
  }

  public BlockEntity getTile(boolean forceUpdate) {
    if (!Utils.CAULDRON_DETECTED && tile != null && !tile.isRemoved()) {
      return tile;
    }

    if (Utils.CAULDRON_DETECTED || (forceUpdate && tile != null && tile.isRemoved()) || tracker.markTimeIfDelay(world)) {
      refresh();

      if (tile != null && !tile.isRemoved()) {
        return tile;
      }
    }

    return null;
  }

  public boolean exists() {
    if (tile != null && !Utils.CAULDRON_DETECTED && !tile.isRemoved()) {
      return true;
    }

    return world.isLoaded(new BlockPos(x, y, z));
  }
}
