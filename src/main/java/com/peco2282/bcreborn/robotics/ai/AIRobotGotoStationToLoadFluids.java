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
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.inventory.filters.IFluidFilter;
import com.peco2282.bcreborn.robotics.IStationFilter;

public class AIRobotGotoStationToLoadFluids extends AIRobot {

	private IFluidFilter filter;

	public AIRobotGotoStationToLoadFluids(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotGotoStationToLoadFluids(EntityRobotBase iRobot, IFluidFilter iFiler) {
		this(iRobot);

		filter = iFiler;
	}

	@Override
	public void update() {
		startDelegateAI(new AIRobotSearchAndGotoStation(robot, new StationFilter(),
				robot.getZoneToLoadUnload()));
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotSearchAndGotoStation) {
			setSuccess(ai.success());
			terminate();
		}
	}

	private class StationFilter implements IStationFilter {

		@Override
		public boolean matches(DockingStation station) {
			return AIRobotLoadFluids.load(robot, station, filter, false) > 0;
		}

	}
}
