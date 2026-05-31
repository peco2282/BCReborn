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
package com.peco2282.bcreborn.robotics.ai;

import java.util.Iterator;
import java.util.LinkedList;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.common.utils.IBlockFilter;
import net.minecraft.nbt.CompoundTag;

public class AIRobotSearchBlock extends AIRobot {

	public BlockIndex blockFound;
	public LinkedList<BlockIndex> path;
	// private PathFindingSearch blockScanner = null; // TODO: Implement
	private IBlockFilter pathFound;
	private Iterator<BlockIndex> blockIter;
	private double maxDistanceToEnd;
	private IZone zone;

	public AIRobotSearchBlock(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotSearchBlock(EntityRobotBase iRobot, boolean random, IBlockFilter iFilter,
							  double iMaxDistanceToEnd) {
		super(iRobot);

		pathFound = iFilter;
		zone = iRobot.getZoneToWork();
		blockFound = null;
		path = null;
		maxDistanceToEnd = iMaxDistanceToEnd;
	}

	@Override
	public void start() {
		// Implementation simplified for now
	}

	@Override
	public void update() {
		// Implementation simplified for now
		terminate();
	}

	@Override
	public void end() {
	}

	@Override
	public boolean success() {
		return blockFound != null;
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}

	@Override
	public void writeSelfToNBT(CompoundTag nbt) {
		super.writeSelfToNBT(nbt);

		if (blockFound != null) {
			CompoundTag sub = new CompoundTag();
			blockFound.writeTo(sub);
			nbt.put("blockFound", sub);
		}
	}

	@Override
	public void loadSelfFromNBT(CompoundTag nbt) {
		super.loadSelfFromNBT(nbt);

		if (nbt.contains("blockFound")) {
			blockFound = new BlockIndex(nbt.getCompound("blockFound"));
		}
	}

	public boolean takeResource() {
		if (blockFound == null) return false;
		return robot.getRegistry().take(new ResourceIdBlock(blockFound.toBlockPos()), robot);
	}

	@Override
	public int getEnergyCost() {
		return 2;
	}

}