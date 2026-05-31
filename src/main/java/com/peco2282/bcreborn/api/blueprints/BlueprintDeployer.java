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

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.io.File;

/**
 * This class is provided as a utility class for third-party mods that would
 * like to easily deploy structures that are written in blueprints. It does
 * not offer control on material that needs to get in, or how the structure
 * is deployed, but allows to create contents of a blueprint in one cycle.
 * Note that these functionalities will only work if BuildCraft is installed.
 */
public abstract class BlueprintDeployer {

	/**
	 * The deployed instantiated by BuildCraft. This is set by the BuildCraft
	 * builder mod. Mods that want to work with BuildCraft not installed should
	 * check for this value to be not null.
	 */
	public static BlueprintDeployer instance;

	/**
	 * Deploy the contents of the blueprints as if the builder was located at
	 * {x, y, z} facing the direction dir.
	 */
	public abstract void deployBlueprint(Level world, int x, int y, int z,
	                                     Direction dir, File file);
			
	/**
	*Deploy the contents of the byte array as if the builder was located at
	*{x, y, z} facing the direction dir.
	*/
	
	public abstract void deployBlueprintFromFileStream(Level world, int x, int y,
	int z, Direction dir, byte [] data);

}
