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
package com.peco2282.bcreborn.api.transport;

import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPipeTile extends IInjectable {
  PipeType getPipeType();

  Level getWorld();

  BlockPos getPos();

  boolean isPipeConnected(Direction with);

  Block getNeighborBlock(Direction dir);

  BlockEntity getNeighborTile(Direction dir);

  IPipe getNeighborPipe(Direction dir);

  IPipe getPipe();

  DyeColor getPipeColor();

  PipePluggable getPipePluggable(Direction direction);

  boolean hasPipePluggable(Direction direction);

  boolean hasBlockingPluggable(Direction direction);

  void scheduleNeighborChange();

  void scheduleRenderUpdate();

  @Deprecated
  int injectItem(ItemStack stack, boolean doAdd, Direction from);

  enum PipeType {
    ITEM, FLUID, POWER, STRUCTURE
  }
}
