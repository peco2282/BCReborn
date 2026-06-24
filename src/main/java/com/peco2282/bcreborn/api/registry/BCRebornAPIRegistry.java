package com.peco2282.bcreborn.api.registry;

import com.peco2282.bcreborn.api.blueprints.ISchematicRegistry;
import org.jetbrains.annotations.ApiStatus;

public class BCRebornAPIRegistry {
  private static ISchematicRegistry schematicRegistry;
  public static ISchematicRegistry schematic() {
    return schematicRegistry;
  }

  @ApiStatus.Internal
  public static void schematic(ISchematicRegistry registry) {
    schematicRegistry = registry;
  }

}
