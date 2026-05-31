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
package com.peco2282.bcreborn.api.blocks;


import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IColorRemovable {
  boolean removeColorFromBlock(Level world, int x, int y, int z, Direction side);
}
