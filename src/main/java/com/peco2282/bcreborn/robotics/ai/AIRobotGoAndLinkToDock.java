/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.util.ForgeDirection;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;

public class AIRobotGoAndLinkToDock extends AIRobot {

	private DockingStation station;

	public AIRobotGoAndLinkToDock(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotGoAndLinkToDock(EntityRobotBase iRobot, DockingStation iStation) {
		this(iRobot);

		station = iStation;
	}

	@Override
	public void start() {
		if (station == robot.getLinkedStation() && station == robot.getDockingStation()) {
			terminate();
		} else {
			if (station != null && station.takeAsMain(robot)) {
				startDelegateAI(new AIRobotGotoBlock(robot,
						station.x() + station.side().offsetX * 2,
						station.y() + station.side().offsetY * 2,
						station.z() + station.side().offsetZ * 2));
			} else {
				setSuccess(false);
				terminate();
			}
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotGotoBlock) {
			if (ai.success()) {
				startDelegateAI(new AIRobotStraightMoveTo(robot,
						station.x() + 0.5F + station.side().offsetX * 0.5F,
						station.y() + 0.5F + station.side().offsetY * 0.5F,
						station.z() + 0.5F + station.side().offsetZ * 0.5F));
			} else {
				terminate();
			}
		} else if (ai instanceof AIRobotStraightMoveTo) {
			if (ai.success()) {
				robot.dock(station);
			}
			terminate();
		}
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}

	@Override
	public void writeSelfToNBT(CompoundTag nbt) {
		super.writeSelfToNBT(nbt);

		if (station != null && station.index() != null) {
			CompoundTag indexNBT = new CompoundTag();
			station.index().writeTo(indexNBT);
			nbt.setTag("stationIndex", indexNBT);
			nbt.setByte("stationSide", (byte) station.side().ordinal());
		}
	}

	@Override
	public void loadSelfFromNBT(CompoundTag nbt) {
		if (nbt.contains("stationIndex")) {
			BlockIndex index = new BlockIndex(nbt.getCompound("stationIndex"));
			ForgeDirection side = ForgeDirection.values()[nbt.getByte("stationSide")];

			station = robot.getRegistry().getStation(index.x, index.y, index.z, side);
		} else {
			station = robot.getLinkedStation();
		}
	}
}
