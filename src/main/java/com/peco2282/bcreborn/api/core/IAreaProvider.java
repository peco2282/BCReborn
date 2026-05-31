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
package com.peco2282.bcreborn.api.core;

/**
 * To be implemented by TileEntities able to provide a square area on the world, typically BuildCraft markers.
 */
public interface IAreaProvider {
	int xMin();

	int yMin();

	int zMin();

	int xMax();

	int yMax();

	int zMax();

	/**
	 * Remove from the world all objects used to define the area.
	 */
	void removeFromWorld();
}
