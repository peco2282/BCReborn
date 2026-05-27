/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;

public class AIRobotMain extends AIRobot {

	private AIRobot overridingAI;
	private int rechargeCooldown;

	public AIRobotMain(EntityRobotBase iRobot) {
		super(iRobot);
		rechargeCooldown = 0;
	}

	@Override
	public int getEnergyCost() {
		return 0;
	}

	@Override
	public void preempt(AIRobot ai) {
		if (robot.getEnergy() <= EntityRobotBase.SHUTDOWN_ENERGY
				&& (robot.getDockingStation() == null || !robot.getDockingStation().providesPower())) {
			if (!(ai instanceof AIRobotShutdown)) {
				BCLog.logger.info("Shutting down robot " + robot.toString() + " - no power");
				startDelegateAI(new AIRobotShutdown(robot));
			}
		} else if (robot.getEnergy() < EntityRobotBase.SAFETY_ENERGY) {
			if (!(ai instanceof AIRobotRecharge) && !(ai instanceof AIRobotShutdown)) {
				if (rechargeCooldown-- <= 0) {
					startDelegateAI(new AIRobotRecharge(robot));
				}
			}
		} else if (!(ai instanceof AIRobotRecharge)) {
			if (overridingAI != null && ai != overridingAI) {
				startDelegateAI(overridingAI);
			}
		}
	}

	@Override
	public void update() {
		AIRobot board = robot.getBoard();

		if (board != null) {
			startDelegateAI(board);
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotRecharge) {
			if (!ai.success()) {
				rechargeCooldown = 120;
			}
		}
		if (ai == overridingAI) {
			overridingAI = null;
		}
	}

	public void setOverridingAI(AIRobot ai) {
		if (overridingAI == null) {
			overridingAI = ai;
		}
	}

	public AIRobot getOverridingAI() {
		return overridingAI;
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}
}
