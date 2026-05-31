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

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;

public class AIRobotGotoStationAndUnload extends AIRobot {

  private final DockingStation station;

  public AIRobotGotoStationAndUnload(EntityRobotBase iRobot) {
    super(iRobot);

    station = null;
  }

  public AIRobotGotoStationAndUnload(EntityRobotBase iRobot, DockingStation iStation) {
    super(iRobot);

    station = iStation;
  }

  @Override
  public void start() {
    if (station == null) {
      startDelegateAI(new AIRobotGotoStationToUnload(robot));
    } else {
      startDelegateAI(new AIRobotGotoStation(robot, station));
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStationToUnload) {
      if (ai.success()) {
        startDelegateAI(new AIRobotUnload(robot));
      } else {
        setSuccess(false);
        terminate();
      }
    } else if (ai instanceof AIRobotGotoStation) {
      if (ai.success()) {
        startDelegateAI(new AIRobotUnload(robot));
      } else {
        setSuccess(false);
        terminate();
      }
    }
  }
}
