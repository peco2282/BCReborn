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
package com.peco2282.bcreborn.core.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class WorldPropertyIsOre extends WorldProperty {
	public WorldPropertyIsOre() {
	}

	@Override
	public boolean get(BlockGetter blockAccess, BlockState state, BlockPos pos) {
		if (state == null) {
			return false;
		}
		return state.is(Tags.Blocks.ORES);
	}
}
