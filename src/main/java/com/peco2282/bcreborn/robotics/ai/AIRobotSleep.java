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
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.statements.ActionRobotWakeUp;

public class AIRobotSleep extends AIRobot<AIRobotSleep> {

  private static final int SLEEPING_TIME = 60 * 20;
  private int sleptTime = 0;

  public AIRobotSleep(RobotEntityBase iRobot) {
    super(RoboticsAIType.SLEEP, iRobot);
  }

  @Override
  public void preempt(AIRobot<?> ai) {
    for (StatementSlot s : robot.getLinkedStation().getActiveActions()) {
      if (s.statement instanceof ActionRobotWakeUp) {
        terminate();
      }
    }
  }

  @Override
  public void update() {
    sleptTime++;

    if (sleptTime > SLEEPING_TIME) {
      terminate();
    }
  }

  @Override
  public int getEnergyCost() {
    // This trick is so we get 0.1 RF per tick.
    return sleptTime % 10 == 0 ? 1 : 0;
  }
}
