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
package com.peco2282.bcreborn.api.blueprints;


import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.world.level.Level;

/**
 * Interface providing contextual information for building or initializing blueprint slots.
 */
public interface IBuilderContext {

  /**
   * Rotates the given position to the left based on the context's orientation.
   *
   * @param pos The position to rotate.
   * @return The rotated {@link Position}.
   */
  Position rotatePositionLeft(Position pos);

  /**
   * Gets the surrounding box of the construction area.
   *
   * @return The {@link IBox}.
   */
  IBox surroundingBox();

  /**
   * Gets the world context.
   *
   * @return The {@link Level}.
   */
  Level world();

  /**
   * Gets the mapping registry for identifying blocks and entities.
   *
   * @return The {@link MappingRegistry}.
   */
  MappingRegistry getMappingRegistry();
}
