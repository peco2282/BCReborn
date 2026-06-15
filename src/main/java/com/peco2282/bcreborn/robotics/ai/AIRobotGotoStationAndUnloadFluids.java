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
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;

public class AIRobotGotoStationAndUnloadFluids extends AIRobot<AIRobotGotoStationAndUnloadFluids> {

  public AIRobotGotoStationAndUnloadFluids(RobotEntityBase iRobot) {
    super(RoboticsAIType.GOTO_STATION_AND_UNLOAD_FLUIDS, iRobot);
  }

  @Override
  public void start() {
    startDelegateAI(new AIRobotGotoStationToUnloadFluids(robot));
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotGotoStationToUnloadFluids) {
      if (ai.success()) {
        startDelegateAI(new AIRobotUnloadFluids(robot));
      } else {
        setSuccess(false);
        terminate();
      }
    }
  }
}
