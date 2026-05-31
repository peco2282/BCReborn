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

public class AIRobotGotoStationToUnloadFluids extends AIRobot {

  public AIRobotGotoStationToUnloadFluids(EntityRobotBase iRobot) {
    super(iRobot);
  }

  @Override
  public void update() {
    startDelegateAI(new AIRobotSearchAndGotoStation(robot, new StationFilter(),
      robot.getZoneToLoadUnload()));
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotSearchAndGotoStation) {
      setSuccess(ai.success());
      terminate();
    }
  }

  private class StationFilter implements IStationFilter {

    @Override
    public boolean matches(DockingStation station) {
      return AIRobotUnloadFluids.unload(robot, station, false) > 0;
    }

  }
}
