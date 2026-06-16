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
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndLoad;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnload;
import com.peco2282.bcreborn.robotics.ai.AIRobotLoad;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;

public class BoardRobotCarrier extends RedstoneBoardRobot<BoardRobotCarrier> {

  public BoardRobotCarrier(RobotEntityBase iRobot) {
    super(RoboticsAIType.CARRIER, iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return BCBoardNBT.REGISTRY.get("carrier");
  }

  @Override
  public void update() {
    if (!robot.containsItems()) {
      if (robot.getLinkedStation() == null) return;
      startDelegateAI(new AIRobotGotoStationAndLoad(robot, ActionRobotFilter.getGateFilter(robot.getLinkedStation()), AIRobotLoad.ANY_QUANTITY));
    } else {
      startDelegateAI(new AIRobotGotoStationAndUnload(robot));
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStationAndLoad) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotGotoStationAndUnload) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    }
  }
}
