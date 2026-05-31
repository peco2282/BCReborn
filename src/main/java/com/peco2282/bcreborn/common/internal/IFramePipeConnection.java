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
package com.peco2282.bcreborn.common.internal;


import net.minecraft.world.level.BlockGetter;

public interface IFramePipeConnection {
	boolean isPipeConnected(BlockGetter blockAccess, int x1, int y1, int z1, int x2, int y2, int z2);
}
