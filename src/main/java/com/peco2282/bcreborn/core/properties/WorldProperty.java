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

import com.peco2282.bcreborn.api.core.IWorldProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public abstract class WorldProperty implements IWorldProperty {

	public Map<ResourceKey<Level>, DimensionProperty> properties = new HashMap<>();

	@Override
	public synchronized boolean get(Level world, BlockPos pos) {
		return getDimension(world).get(pos.getX(), pos.getY(), pos.getZ());
	}

	private DimensionProperty getDimension(Level world) {
		ResourceKey<Level> key = world.dimension();

		DimensionProperty result = properties.get(key);

		if (result == null) {
			result = new DimensionProperty(world, this);
			properties.put(key, result);
		}

		return result;
	}

	@Override
	public void clear() {
		for (DimensionProperty p : properties.values()) {
			if (p != null) {
				p.clear();
			}
		}

		properties.clear();
	}

	protected abstract boolean get(BlockGetter blockAccess, BlockState state, BlockPos pos);
}
