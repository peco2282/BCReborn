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
package com.peco2282.bcreborn.api.registry;

import com.peco2282.bcreborn.api.blueprints.ISchematicRegistry;
import org.jetbrains.annotations.ApiStatus;

/**
 * Central registry for BC Reborn API components.
 * <p>
 * This class provides static access to various API registries used throughout the mod.
 * Registry instances should be set internally during mod initialization.
 */
public class BCRebornAPIRegistry {
  /**
   * The schematic registry instance used for managing blueprint schematics.
   */
  private static ISchematicRegistry schematicRegistry;

  /**
   * Returns the schematic registry instance.
   *
   * @return The schematic registry, or {@code null} if not yet initialized.
   */

  public static ISchematicRegistry schematic() {
    return schematicRegistry;
  }

  /**
   * Sets the schematic registry instance.
   * <p>
   * This method is for internal use only and should be called during mod initialization.
   *
   * @param registry The schematic registry instance to set.
   */
  @ApiStatus.Internal
  public static void schematic(ISchematicRegistry registry) {
    schematicRegistry = registry;
  }

}
