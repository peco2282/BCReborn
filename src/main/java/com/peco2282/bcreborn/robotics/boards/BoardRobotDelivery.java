/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.boards;

import java.util.ArrayList;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.AIRobotDeliverRequested;
import com.peco2282.bcreborn.robotics.ai.AIRobotDisposeItems;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndLoad;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchStackRequest;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class BoardRobotDelivery extends RedstoneBoardRobot {

	private ArrayList<ItemStack> deliveryBlacklist = new ArrayList<ItemStack>();

	// private StackRequest currentRequest = null; // TODO: Implement StackRequest

	public BoardRobotDelivery(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("delivery");
	}

	@Override
	public void update() {
		if (robot.containsItems()) {
			startDelegateAI(new AIRobotDisposeItems(robot));
			return;
		}

		// Simplified for now without StackRequest
		startDelegateAI(new AIRobotGotoSleep(robot));
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotDisposeItems) {
			update();
		} else {
			startDelegateAI(new AIRobotGotoSleep(robot));
		}
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}

	@Override
	public void writeSelfToNBT(CompoundTag nbt) {
		super.writeSelfToNBT(nbt);
	}

	@Override
	public void loadSelfFromNBT(CompoundTag nbt) {
		super.loadSelfFromNBT(nbt);
	}
}
