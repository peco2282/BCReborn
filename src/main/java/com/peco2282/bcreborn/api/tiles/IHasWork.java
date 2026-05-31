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
package com.peco2282.bcreborn.api.tiles;

/**
 * This interface should be implemented by any Tile Entity which carries out
 * work (crafting, ore processing, mining, et cetera).
 */
public interface IHasWork {
	/**
	 * Check if the Tile Entity is currently doing any work.
	 * @return True if the Tile Entity is doing work.
	 */
	boolean hasWork();
}
