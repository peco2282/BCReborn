/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.IStationFilter;

public class AIRobotSearchAndGotoStation extends AIRobot {

	private IStationFilter filter;
	private IZone zone;

	public AIRobotSearchAndGotoStation(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotSearchAndGotoStation(EntityRobotBase iRobot, IStationFilter iFilter, IZone iZone) {
		this(iRobot);

		filter = iFilter;
		zone = iZone;
	}

	@Override
	public void start() {
		startDelegateAI(new AIRobotSearchStation(robot, filter, zone));
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotSearchStation) {
			if (ai.success()) {
				startDelegateAI(new AIRobotGotoStation(robot, ((AIRobotSearchStation) ai).targetStation));
			} else {
				setSuccess(false);
				terminate();
			}
		} else if (ai instanceof AIRobotGotoStation) {
			setSuccess(ai.success());
			terminate();
		}
	}
}
