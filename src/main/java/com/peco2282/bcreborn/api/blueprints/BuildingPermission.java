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
 * Schematics recorded in the blueprints can restrict situations where a
 * blueprint can be used. A same schematic class can have different
 * permissions depending on its contents. It's particularly useful when
 * fixing a schematic, if blueprints that saved the previous version should
 * not be built because of a bug (such as dupe bug on inventories).
 */
public enum BuildingPermission {
	/**
	 * No restrictions, blueprints using this schematic are good in all
	 * contexts.
	 */
	ALL,

	/**
	 * This blueprints containing this schematic can only be used in
	 * creative. Maybe the block can't be crafted in survival in the first
	 * place, or the content of the schematic is known to have dupe bugs.
	 */
	CREATIVE_ONLY,

	/**
	 * Blueprints containing this schematic should not be built. This is
	 * typically used when a critical problems have been fixed, but older
	 * versions of the schematic are too badly broken to be retreived.
	 */
	NONE,
}