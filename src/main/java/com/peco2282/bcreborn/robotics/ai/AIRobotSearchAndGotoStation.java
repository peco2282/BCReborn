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
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.IStationFilter;

public class AIRobotSearchAndGotoStation extends AIRobot {

  private IStationFilter filter;
  private IZone zone;

  public AIRobotSearchAndGotoStation(EntityRobotBase iRobot) {
    super(iRobot);
  }

  public AIRobotSearchAndGotoStation(EntityRobotBase iRobot, IStationFilter iFilter, IZone iZone) {
    this(iRobot);

    filter = iFilter;
    zone = iZone;
  }

  @Override
  public void start() {
    startDelegateAI(new AIRobotSearchStation(robot, filter, zone));
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotSearchStation) {
      if (ai.success()) {
        startDelegateAI(new AIRobotGotoStation(robot, ((AIRobotSearchStation) ai).targetStation));
      } else {
        setSuccess(false);
        terminate();
      }
    } else if (ai instanceof AIRobotGotoStation) {
      setSuccess(ai.success());
      terminate();
    }
  }
}
