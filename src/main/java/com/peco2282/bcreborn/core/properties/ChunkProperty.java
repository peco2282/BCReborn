/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.properties;

import net.minecraft.world.level.BlockGetter;
import java.util.BitSet;

public class ChunkProperty {

	private final BitSet property;
	private final int worldHeight;
	private final int minBuildHeight;

	public ChunkProperty(BlockGetter blockAccess, int iWorldHeight, int iMinBuildHeight) {
		worldHeight = iWorldHeight;
		minBuildHeight = iMinBuildHeight;
		property = new BitSet(16 * 16 * worldHeight);
	}

	public boolean get(int xChunk, int y, int zChunk) {
		int index = getIndex(xChunk, y, zChunk);
		if (index < 0 || index >= property.size()) return false;
		return property.get(index);
	}

	public void set(int xChunk, int y, int zChunk, boolean value) {
		int index = getIndex(xChunk, y, zChunk);
		if (index >= 0 && index < 16 * 16 * worldHeight) {
			property.set(index, value);
		}
	}

	private int getIndex(int x, int y, int z) {
		int relativeY = y - minBuildHeight;
		if (relativeY < 0 || relativeY >= worldHeight) return -1;
		return (x * worldHeight * 16) + (relativeY * 16) + z;
	}
}
