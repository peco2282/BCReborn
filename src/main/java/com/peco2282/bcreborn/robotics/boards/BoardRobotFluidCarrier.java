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
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndLoadFluids;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnloadFluids;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;

public class BoardRobotFluidCarrier extends RedstoneBoardRobot<BoardRobotFluidCarrier> {

  public BoardRobotFluidCarrier(RobotEntityBase iRobot) {
    super(RoboticsAIType.FLUID_CARRIER, iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return BCBoardNBT.REGISTRY.get("fluidCarrier");
  }

  @Override
  public void update() {
    if (!robotHasFluid()) {
      if (robot.getLinkedStation() == null) return;
      startDelegateAI(new AIRobotGotoStationAndLoadFluids(robot, ActionRobotFilter.getGateFluidFilter(robot.getLinkedStation())));
    } else {
      startDelegateAI(new AIRobotGotoStationAndUnloadFluids(robot));
    }
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotGotoStationAndLoadFluids) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotGotoStationAndUnloadFluids) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    }
  }

  private boolean robotHasFluid() {
    // Simplified fluid check
    return false; // TODO: Implement proper fluid handler check
  }
}
