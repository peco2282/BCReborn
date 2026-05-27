/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.IStationFilter;
import com.peco2282.bcreborn.robotics.statements.ActionStationForbidRobot;

public class AIRobotSearchStation extends AIRobot {

	public DockingStation targetStation;
	private IStationFilter filter;
	private IZone zone;

	public AIRobotSearchStation(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotSearchStation(EntityRobotBase iRobot, IStationFilter iFilter, IZone iZone) {
		this(iRobot);

		filter = iFilter;
		zone = iZone;
	}

	@Override
	public void start() {
		if (robot.getDockingStation() != null
				&& filter.matches(robot.getDockingStation())) {
			targetStation = robot.getDockingStation();
			terminate();
			return;
		}

		double potentialStationDistance = Float.MAX_VALUE;
		DockingStation potentialStation = null;

		for (DockingStation station : robot.getRegistry().getStations()) {
			if (!station.isInitialized()) {
				continue;
			}

			if (station.isTaken() && station.robotIdTaking() != robot.getRobotId()) {
				continue;
			}

			if (zone != null && !zone.contains(station.x(), station.y(), station.z())) {
				continue;
			}

			if (filter.matches(station)) {
				if (ActionStationForbidRobot.isForbidden(station, robot)) {
					continue;
				}

				double dx = robot.getX() - station.x();
				double dy = robot.getY() - station.y();
				double dz = robot.getZ() - station.z();
				double distance = dx * dx + dy * dy + dz * dz;

				if (potentialStation == null || distance < potentialStationDistance) {
					potentialStation = station;
					potentialStationDistance = distance;
				}
			}
		}

		if (potentialStation != null) {
			targetStation = potentialStation;
		}

		terminate();
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		terminate();
	}

	@Override
	public boolean success() {
		return targetStation != null;
	}
}
