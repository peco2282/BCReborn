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

public class AIRobotGotoSleep extends AIRobot {

  public AIRobotGotoSleep(RobotEntityBase iRobot) {
    super(iRobot);
  }

  @Override
  public void start() {
    robot.getRegistry().releaseResources(robot);
    startDelegateAI(new AIRobotGotoStation(robot, robot.getLinkedStation()));
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStation) {
      startDelegateAI(new AIRobotSleep(robot));
    } else if (ai instanceof AIRobotSleep) {
      terminate();
    }
  }
}
