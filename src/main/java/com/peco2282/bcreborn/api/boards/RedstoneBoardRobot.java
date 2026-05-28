/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.boards;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;

public abstract class RedstoneBoardRobot extends AIRobot implements IRedstoneBoard<EntityRobotBase> {

	protected final EntityRobotBase robot;

	public RedstoneBoardRobot(EntityRobotBase robot) {
        super(robot);
        this.robot = robot;
	}

	@Override
	public abstract RedstoneBoardRobotNBT getNBTHandler();

	@Override
	public final void updateBoard(EntityRobotBase container) {

	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}
}
