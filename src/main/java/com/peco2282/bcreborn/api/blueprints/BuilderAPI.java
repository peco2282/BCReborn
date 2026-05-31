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

import com.peco2282.bcreborn.api.registry.BCRegistryKeys;

public final class BuilderAPI {
  public static final int BREAK_ENERGY = 160;
  public static final int BUILD_ENERGY = 240;
  /**
   * @deprecated Use {@link BCRegistryKeys#SCHEMATIC}
   */
  @Deprecated
  public static ISchematicRegistry schematicRegistry;
  public static ISchematicHelper schematicHelper;

  private BuilderAPI() {
  }
}
