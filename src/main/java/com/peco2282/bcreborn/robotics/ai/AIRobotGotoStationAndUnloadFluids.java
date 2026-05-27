/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;

public class AIRobotGotoStationAndUnloadFluids extends AIRobot {

	public AIRobotGotoStationAndUnloadFluids(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public void start() {
		startDelegateAI(new AIRobotGotoStationToUnloadFluids(robot));
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotGotoStationToUnloadFluids) {
			if (ai.success()) {
				startDelegateAI(new AIRobotUnloadFluids(robot));
			} else {
				setSuccess(false);
				terminate();
			}
		}
	}
}
