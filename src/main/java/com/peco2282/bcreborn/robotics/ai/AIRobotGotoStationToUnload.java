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
import com.peco2282.bcreborn.robotics.IStationFilter;

public class AIRobotGotoStationToUnload extends AIRobot {

  public AIRobotGotoStationToUnload(EntityRobotBase iRobot) {
    super(iRobot);
  }

  @Override
  public void start() {
    startDelegateAI(new AIRobotSearchAndGotoStation(robot, new StationInventory(), robot.getZoneToLoadUnload()));
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotSearchAndGotoStation) {
      setSuccess(ai.success());
      terminate();
    }
  }

  private class StationInventory implements IStationFilter {
    @Override
    public boolean matches(DockingStation station) {
      return AIRobotUnload.unload(robot, station, false);
    }
  }

}
