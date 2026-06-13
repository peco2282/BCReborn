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

import org.jetbrains.annotations.ApiStatus;

/**
 * API for the BuildCraft Builder and Architect systems.
 */
public final class BuilderAPI {
  /**
   * Energy cost to break a block.
   */
  public static final int BREAK_ENERGY = 160;
  /**
   * Energy cost to build a block.
   */
  public static final int BUILD_ENERGY = 240;

  /**
   * Gets the schematic registry.
   *
   * @return The {@link ISchematicRegistry}.
   */
  public static ISchematicRegistry schematic() {
    return schematicRegistry;
  }

  private static ISchematicRegistry schematicRegistry;

  /**
   * Sets the schematic registry.
   *
   * @param registry The registry to set.
   */
  @ApiStatus.Internal
  public static void schematic(ISchematicRegistry registry) {
    schematicRegistry = registry;
  }

  private BuilderAPI() {
  }
}
