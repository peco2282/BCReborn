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

/**
 * Interface representing a pipe's block entity.
 * Extends {@link IInjectable}.
 */
public interface IPipeTile extends IInjectable {
  /**
   * Gets the type of the pipe.
   *
   * @return The {@link PipeType}.
   */
  PipeType getPipeType();

  /**
   * Gets the world the pipe is in.
   *
   * @return The {@link Level}.
   */
  Level getWorld();

  /**
   * Gets the position of the pipe.
   *
   * @return The {@link BlockPos}.
   */
  BlockPos getPos();

  /**
   * Checks if the pipe is connected on the specified side.
   *
   * @param with The direction to check.
   * @return True if connected.
   */
  boolean isPipeConnected(Direction with);

  /**
   * Gets the neighbor block in the specified direction.
   *
   * @param dir The direction.
   * @return The {@link Block}.
   */
  Block getNeighborBlock(Direction dir);

  /**
   * Gets the neighbor block entity in the specified direction.
   *
   * @param dir The direction.
   * @return The {@link BlockEntity}.
   */
  BlockEntity getNeighborTile(Direction dir);

  /**
   * Gets the neighbor pipe in the specified direction.
   *
   * @param dir The direction.
   * @return The {@link IPipe}, or null if not a pipe.
   */
  IPipe getNeighborPipe(Direction dir);

  /**
   * Gets the {@link IPipe} instance for this tile.
   *
   * @return The pipe.
   */
  IPipe getPipe();

  /**
   * Gets the color of the pipe.
   *
   * @return The {@link DyeColor}, or null if not colored.
   */
  DyeColor getPipeColor();

  /**
   * Gets the pluggable on the specified side.
   *
   * @param direction The direction.
   * @return The {@link PipePluggable}, or null if none.
   */
  PipePluggable<?> getPipePluggable(Direction direction);

  /**
   * Sets the pluggable on the specified side.
   *
   * @param direction The direction.
   * @param pluggable The pluggable to set, or null to remove.
   */
  void setPipePluggable(Direction direction, PipePluggable<?> pluggable);

  /**
   * Checks if there is a pluggable on the specified side.
   *
   * @param direction The direction.
   * @return True if a pluggable is present.
   */
  boolean hasPipePluggable(Direction direction);

  /**
   * Checks if there is a pluggable that blocks connections on the specified side.
   *
   * @param direction The direction.
   * @return True if a blocking pluggable is present.
   */
  boolean hasBlockingPluggable(Direction direction);

  /**
   * Schedules a neighbor change update.
   */
  void scheduleNeighborChange();

  /**
   * Schedules a rendering update for the pipe.
   */
  void scheduleRenderUpdate();

  /**
   * Injects an item into the pipe.
   *
   * @param stack The stack to inject.
   * @param doAdd If true, actually add the item; if false, just simulate.
   * @param from  The direction the item is coming from.
   * @return The number of items successfully injected.
   * @deprecated Use specific transport module methods if possible.
   */
  @Deprecated
  int injectItem(ItemStack stack, boolean doAdd, Direction from);

  /**
   * Represents the type of a pipe.
   */
  enum PipeType {
    ITEM, FLUID, POWER, STRUCTURE
  }
}
