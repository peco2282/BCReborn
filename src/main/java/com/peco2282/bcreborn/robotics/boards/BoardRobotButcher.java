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
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.AIRobotAttack;
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchAndEquipItemStack;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnload;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class BoardRobotButcher extends RedstoneBoardRobot {

	public BoardRobotButcher(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("butcher");
	}

	@Override
	public final void update() {
		ItemStack held = robot.getMainHandItem();
		if (held.isEmpty()) {
			startDelegateAI(new AIRobotFetchAndEquipItemStack(robot, stack -> {
				return stack.getItem() instanceof SwordItem;
			}));
		} else if (held.getDamageValue() >= held.getMaxDamage()) {
			startDelegateAI(new AIRobotGotoStationAndUnload(robot));
		} else {
			startDelegateAI(new AIRobotSearchEntity(robot, entity -> {
				return entity instanceof Animal;
			}, 250, robot.getZoneToWork()));
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotFetchAndEquipItemStack) {
			if (!ai.success()) {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotSearchEntity searchAI) {
			if (ai.success()) {
				startDelegateAI(new AIRobotAttack(robot, searchAI.target));
			} else {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		}
	}
}
