/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.core;

import net.minecraft.core.BlockPos;

import java.util.Random;

public interface IZone {

	double distanceTo(BlockIndex index);

	double distanceToSquared(BlockIndex index);

	boolean contains(double x, double y, double z);

	default boolean contains(BlockPos pos) {
		return contains(pos.getX(), pos.getY(), pos.getZ());
	}

	BlockIndex getRandomBlockIndex(Random rand);

}
