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
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchItem;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnload;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;

import java.util.HashSet;
import java.util.Set;

public class BoardRobotPicker extends RedstoneBoardRobot {

	public static Set<Integer> targettedItems = new HashSet<>();

	public BoardRobotPicker(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public static void onServerStart() {
	}

	private void fetchNewItem() {
		if (robot.getLinkedStation() == null) return;
		startDelegateAI(new AIRobotFetchItem(robot, 250, ActionRobotFilter.getGateFilter(robot
				.getLinkedStation()), robot.getZoneToWork()));
	}

	@Override
	public void update() {
		fetchNewItem();
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotFetchItem) {
			if (ai.success()) {
				// if we find an item - that may have been cancelled.
				// let's try to get another one
				fetchNewItem();
			} else if (robot.containsItems()) {
				startDelegateAI(new AIRobotGotoStationAndUnload(robot));
			} else {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotGotoStationAndUnload) {
			if (!ai.success()) {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		}
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("picker");
	}
}
