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
package com.peco2282.bcreborn.robotics.map;

public final class MapUtils {
	private MapUtils() {

	}

	public static long getIDFromCoords(int x, int z) {
		return ((long) (x & 0xFFFFFF) << 24) | (z & 0xFFFFFF);
	}

	public static long getIDFromCoords(net.minecraft.world.level.ChunkPos pos) {
		return getIDFromCoords(pos.x, pos.z);
	}

	public static long getIDFromCoords(net.minecraft.world.level.chunk.ChunkAccess chunk) {
		return getIDFromCoords(chunk.getPos());
	}

	public static int getXFromID(long id) {
		return (int) (id >> 24);
	}

	public static int getZFromID(long id) {
		int z = (int) (id & 0xFFFFFF);
		if (z >= 0x800000) {
			return -(z ^ 0xFFFFFF);
		} else {
			return z;
		}
	}

}
