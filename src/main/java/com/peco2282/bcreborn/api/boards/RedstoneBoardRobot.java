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
