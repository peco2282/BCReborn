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
package com.peco2282.bcreborn.api.transport.pluggable;

import net.minecraft.world.level.block.Block;

/**
 * Interface for pluggables that act as facades.
 */
public interface IFacadePluggable {

  /**
   * Gets the block that this facade is mimicking.
   *
   * @return The mimicking {@link Block}.
   */
  Block getCurrentBlock();

  /**
   * Gets the metadata/state identifier of the mimicking block.
   *
   * @return The metadata value.
   */
  int getCurrentMetadata();

  /**
   * Checks if the facade is transparent.
   *
   * @return True if transparent.
   */
  boolean isTransparent();

  /**
   * Checks if the facade is hollow.
   *
   * @return True if hollow.
   */
  boolean isHollow();
}
