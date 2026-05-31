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
package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.nbt.CompoundTag;

import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.robots.IRequestProvider;
import com.peco2282.bcreborn.common.inventory.InvUtils;
import com.peco2282.bcreborn.common.inventory.filters.ArrayStackOrListFilter;
import com.peco2282.bcreborn.robotics.StackRequest;
import net.minecraft.world.item.ItemStack;

public class AIRobotDeliverRequested extends AIRobot {

	private StackRequest requested;
	private boolean delivered = false;

	public AIRobotDeliverRequested(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotDeliverRequested(EntityRobotBase robot, StackRequest request) {
		this(robot);

		requested = request;
	}

	@Override
	public void start() {
		if (requested != null) {
			startDelegateAI(new AIRobotGotoStation(robot, requested.getStation(robot.level())));
		} else {
			setSuccess(false);
			terminate();
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotGotoStation) {
			if (!ai.success()) {
				setSuccess(false);
				terminate();
				return;
			}

			IRequestProvider requester = requested.getRequester(robot.level());
			if (requester == null) {
				setSuccess(false);
				terminate();
				return;
			}

			// TODO: Make this not exceed the requested amount of items.

			int count = 0;

			for (IInvSlot slot : InvUtils.getItems(robot, new ArrayStackOrListFilter(requested.getStack()))) {
				int difference = slot.getStackInSlot().getCount();
				ItemStack newStack = requester.offerItem(requested.getSlot(), slot.getStackInSlot().copy());

				if (newStack == null) {
					slot.setStackInSlot(ItemStack.EMPTY);
				} else if (newStack.getCount() != slot.getStackInSlot().getCount()) {
					slot.setStackInSlot(newStack);
					difference = newStack.getCount() - difference;
				}

				count += difference;
			}

			setSuccess(count > 0);
			terminate();
		}
	}

	@Override
	public boolean success() {
		return delivered;
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}

	@Override
	public void writeSelfToNBT(CompoundTag nbt) {
		super.writeSelfToNBT(nbt);

		if (requested != null) {
			CompoundTag requestNBT = new CompoundTag();
			requested.writeToNBT(requestNBT);
			nbt.put("currentRequest", requestNBT);
		}
	}

	@Override
	public void loadSelfFromNBT(CompoundTag nbt) {
		super.loadSelfFromNBT(nbt);
		if (nbt.contains("currentRequest")) {
			requested = StackRequest.loadFromNBT(nbt.getCompound("currentRequest"));
		}
	}
}
