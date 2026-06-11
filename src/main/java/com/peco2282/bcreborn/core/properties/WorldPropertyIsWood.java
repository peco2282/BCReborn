/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class WorldPropertyIsWood extends WorldProperty {

	public WorldPropertyIsWood() {
	}

	@Override
	public boolean get(BlockGetter blockAccess, BlockState state, BlockPos pos) {
		if (state == null) {
			return false;
		}
		return state.is(BlockTags.LOGS);
	}
}
