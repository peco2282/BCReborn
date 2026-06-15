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

import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.statements.ActionStationForbidRobot;
import com.peco2282.bcreborn.robotics.station.IStationFilter;

public class AIRobotSearchStation extends AIRobot<AIRobotSearchStation> {

  public DockingStation<?> targetStation;
  private IStationFilter filter;
  private IZone zone;

  public AIRobotSearchStation(RobotEntityBase iRobot) {
    super(RoboticsAIType.SEARCH_STATION, iRobot);
  }

  public AIRobotSearchStation(RobotEntityBase iRobot, IStationFilter iFilter, IZone iZone) {
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
    DockingStation<?> potentialStation = null;

    for (DockingStation<?> station : robot.getRegistry().getStations()) {
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
  public void delegateAIEnded(AIRobot<?> ai) {
    terminate();
  }

  @Override
  public boolean success() {
    return targetStation != null;
  }
}
