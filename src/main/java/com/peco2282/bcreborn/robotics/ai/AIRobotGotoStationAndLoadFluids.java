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

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.inventory.filters.IFluidFilter;

public class AIRobotGotoStationAndLoadFluids extends AIRobot {

	private IFluidFilter filter;

	public AIRobotGotoStationAndLoadFluids(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotGotoStationAndLoadFluids(EntityRobotBase iRobot, IFluidFilter iFilter) {
		this(iRobot);

		filter = iFilter;
	}

	@Override
	public void start() {
		startDelegateAI(new AIRobotGotoStationToLoadFluids(robot, filter));
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotGotoStationToLoadFluids) {
			if (filter != null && ai.success()) {
				startDelegateAI(new AIRobotLoadFluids(robot, filter));
			} else {
				setSuccess(false);
				terminate();
			}
		}
	}
}
