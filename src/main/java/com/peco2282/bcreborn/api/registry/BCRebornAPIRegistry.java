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
