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
package com.peco2282.bcreborn.common.utils;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IZone;

import java.util.Iterator;
import java.util.Random;

public class BlockScannerZoneRandom implements Iterable<BlockIndex> {

	private Random rand;
	private IZone zone;
	private int x;
	private int y;
	private int z;

	class BlockIt implements Iterator<BlockIndex> {

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public BlockIndex next() {
			BlockIndex block = zone.getRandomBlockIndex(rand);
			return new BlockIndex(block.x - x, block.y - y, block.z - z);
		}

		@Override
		public void remove() {
		}
	}

	public BlockScannerZoneRandom(int iX, int iY, int iZ, Random iRand, IZone iZone) {
		x = iX;
		y = iY;
		z = iZ;
		rand = iRand;
		zone = iZone;
	}

	@Override
	public Iterator<BlockIndex> iterator() {
		return new BlockIt();
	}

}
