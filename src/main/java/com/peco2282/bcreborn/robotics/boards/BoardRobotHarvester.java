/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.AIRobotHarvest;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class BoardRobotHarvester extends BoardRobotGenericSearchBlock {

	public BoardRobotHarvester(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public BoardRobotHarvester(EntityRobotBase iRobot, CompoundTag nbt) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("harvester");
	}

	@Override
	public boolean isExpectedBlock(Level world, int x, int y, int z) {
		return BuildCraftAPI.getWorldProperty("harvestable").get(world, new BlockPos(x, y, z));
	}

	@Override
	public void update() {
		if (blockFound() != null) {
			startDelegateAI(new AIRobotHarvest(robot, blockFound()));
		} else {
			super.update();
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotHarvest) {
			releaseBlockFound(ai.success());
		}
		super.delegateAIEnded(ai);
	}
}
