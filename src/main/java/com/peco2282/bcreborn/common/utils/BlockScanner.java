/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.utils;


import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.common.Box;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class BlockScanner implements Iterable<BlockIndex> {

	Box box = new Box();
	Level world;

	int x, y, z;
	int iterationsPerCycle;
	int blocksDone = 0;

	class BlockIt implements Iterator<BlockIndex> {

		int it = 0;

		@Override
		public boolean hasNext() {
			return z <= box.zMax && it <= iterationsPerCycle;
		}

		@Override
		public BlockIndex next() {
			BlockIndex index = new BlockIndex(x, y, z);
			it++;
			blocksDone++;

			if (x < box.xMax) {
				x++;
			} else {
				x = box.xMin;

				if (y < box.yMax) {
					y++;
				} else {
					y = box.yMin;

					z++;
				}
			}

			return index;
		}

		@Override
		public void remove() {

		}
	}

	public BlockScanner(Box box, Level world, int iterationsPreCycle) {
		this.box = box;
		this.world = world;
		this.iterationsPerCycle = iterationsPreCycle;

		x = box.xMin;
		y = box.yMin;
		z = box.zMin;
	}

	public BlockScanner() {
	}

	@Override
	public Iterator<BlockIndex> iterator() {
		return new BlockIt();
	}

	public int totalBlocks() {
		return box.sizeX() * box.sizeY() * box.sizeZ();
	}

	public int blocksLeft() {
		return totalBlocks() - blocksDone;
	}

	public void writeToNBT(CompoundTag nbt) {
		nbt.putInt("x", x);
		nbt.putInt("y", y);
		nbt.putInt("z", z);
		nbt.putInt("blocksDone", blocksDone);
		nbt.putInt("iterationsPerCycle", iterationsPerCycle);
		CompoundTag boxNBT = new CompoundTag();
		box.writeToNBT(boxNBT);
		nbt.put("box", boxNBT);
	}

	public void readFromNBT(CompoundTag nbt) {
		x = nbt.getInt("x");
		y = nbt.getInt("y");
		z = nbt.getInt("z");
		blocksDone = nbt.getInt("blocksDone");
		iterationsPerCycle = nbt.getInt("iterationsPerCycle");
		box.initialize(nbt.getCompound("box"));
	}

	public BlockPos nextBlockPos() {
		BlockIndex index = iterator().next();
		return new BlockPos(index.x, index.y, index.z);
	}

}
