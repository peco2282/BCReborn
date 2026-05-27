/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndLoadFluids;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnloadFluids;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import net.minecraftforge.fluids.FluidStack;

public class BoardRobotFluidCarrier extends RedstoneBoardRobot {

	public BoardRobotFluidCarrier(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("fluidCarrier");
	}

	@Override
	public void update() {
		if (!robotHasFluid()) {
			if (robot.getLinkedStation() == null) return;
			startDelegateAI(new AIRobotGotoStationAndLoadFluids(robot, ActionRobotFilter.getGateFluidFilter(robot.getLinkedStation())));
		} else {
			startDelegateAI(new AIRobotGotoStationAndUnloadFluids(robot));
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotGotoStationAndLoadFluids) {
			if (!ai.success()) {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotGotoStationAndUnloadFluids) {
			if (!ai.success()) {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		}
	}

	private boolean robotHasFluid() {
		// Simplified fluid check
		return false; // TODO: Implement proper fluid handler check
	}
}
