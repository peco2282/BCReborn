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
import net.minecraft.nbt.CompoundTag;

public abstract class RedstoneBoardRobot implements IRedstoneBoard<Object> {

	protected final EntityRobotBase robot;

	public RedstoneBoardRobot(Object robot) {
		this.robot = (EntityRobotBase) robot;
	}

	@Override
	public abstract RedstoneBoardRobotNBT getNBTHandler();

	@Override
	public final void updateBoard(Object container) {
		update();
	}

	public void update() {

	}

	public void end() {

	}

	public void startDelegateAI(AIRobot ai) {
		// This should be handled by EntityRobot
	}

	public void delegateAIEnded(AIRobot ai) {

	}

	public boolean canLoadFromNBT() {
		return true;
	}

	public void writeSelfToNBT(CompoundTag nbt) {

	}

	public void loadSelfFromNBT(CompoundTag nbt) {

	}
}
