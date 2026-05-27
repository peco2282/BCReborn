package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;

public class BoardRobotEmpty extends RedstoneBoardRobot {

	public BoardRobotEmpty(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return RedstoneBoardRobotEmptyNBT.instance;
	}

	@Override
	public void update() {
		startDelegateAI(new AIRobotGotoSleep(robot));
	}
}
