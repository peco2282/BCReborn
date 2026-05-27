/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.world.item.ItemStack;

import net.minecraft.core.Direction;

import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.transport.IInjectable;
import com.peco2282.bcreborn.common.inventory.InventoryIterator;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.common.inventory.filters.ArrayStackOrListFilter;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import com.peco2282.bcreborn.robotics.statements.ActionStationAcceptItems;

public class AIRobotUnload extends AIRobot {

	private int waitedCycles = 0;

	public AIRobotUnload(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public void update() {
		waitedCycles++;

		if (waitedCycles > 40) {
			if (unload(robot, robot.getDockingStation(), true)) {
				waitedCycles = 0;
			} else {
				setSuccess(!robot.containsItems());
				terminate();
			}
		}
	}

	public static boolean unload(EntityRobotBase robot, DockingStation station, boolean doUnload) {
		if (station == null) {
			return false;
		}

		IInjectable output = station.getItemOutput();
		if (output == null) {
			return false;
		}

		Direction injectSide = station.getItemOutputSide();
		if (!output.canInjectItems(injectSide)) {
			return false;
		}

		for (IInvSlot robotSlot : InventoryIterator.getIterable((net.minecraft.world.Container) robot, Direction.UP)) {
			if (robotSlot.getStackInSlot().isEmpty()) {
				continue;
			}

			// In 1.20.1 we use a simplified version for now to avoid buildcraft API dependency issues
			// Logic should be restored once statements API is fully ported
			boolean canInteract = true; 

			if (!canInteract) {
				continue;
			}

			ItemStack stack = robotSlot.getStackInSlot();
			int used = output.injectItem(stack, doUnload, injectSide, null);

			if (used > 0) {
				if (doUnload) {
					robotSlot.decreaseStackInSlot(used);
				}
				return true;
			}
		}

		ItemStack held = robot.getMainHandItem();
		if (!held.isEmpty()) {
			// Simplified interact check
			boolean canInteract = true;

			if (!canInteract) {
				return false;
			}

			int used = output.injectItem(held, doUnload, injectSide, null);

			if (used > 0) {
				if (doUnload) {
					if (held.getCount() <= used) {
						robot.setItemInUse(ItemStack.EMPTY);
					} else {
						held.shrink(used);
					}
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public int getEnergyCost() {
		return 10;
	}
}
