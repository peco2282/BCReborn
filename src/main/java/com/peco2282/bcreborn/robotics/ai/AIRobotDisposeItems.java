/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.entity.item.EntityItem;

import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.lib.inventory.InventoryIterator;

public class AIRobotDisposeItems extends AIRobot {

	public AIRobotDisposeItems(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public void start() {
		startDelegateAI(new AIRobotGotoStationAndUnload(robot));
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotGotoStationAndUnload) {
			if (ai.success()) {
				if (robot.containsItems()) {
					startDelegateAI(new AIRobotGotoStationAndUnload(robot));
				} else {
					terminate();
				}
			} else {
				for (IInvSlot slot : InventoryIterator.getIterable(robot)) {
					if (slot.getStackInSlot() != null) {
						final EntityItem entity = new EntityItem(
								robot.level(),
								robot.getX(),
								robot.getY(),
								robot.getZ(),
								slot.getStackInSlot());

						robot.level().spawnEntityInWorld(entity);

						slot.setStackInSlot(null);
					}
				}
				terminate();
			}
		}
	}
}
