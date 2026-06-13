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

/**
 * Defines the permissions for building a schematic in different contexts.
 */
public enum BuildingPermission {
  /**
   * No restrictions; blueprints containing this schematic can be built in all contexts.
   */
  ALL,

  /**
   * Blueprints containing this schematic can only be built in creative mode.
   * This may be due to the block being uncraftable in survival or known issues with survival building.
   */
  CREATIVE_ONLY,

  /**
   * Blueprints containing this schematic cannot be built at all.
   * Typically used for legacy blueprints that are too broken to be placed.
   */
  NONE,
}