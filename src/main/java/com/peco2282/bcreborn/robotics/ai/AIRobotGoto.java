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
import com.peco2282.bcreborn.api.robots.EntityRobotBase;

public abstract class AIRobotGoto extends AIRobot {

  protected float nextX, nextY, nextZ;
  protected double dirX, dirY, dirZ;

  public AIRobotGoto(EntityRobotBase iRobot) {
    super(iRobot);
  }

  protected void setDestination(EntityRobotBase robot, float x, float y, float z) {
    nextX = x;
    nextY = y;
    nextZ = z;

    dirX = nextX - robot.getX();
    dirY = nextY - robot.getY();
    dirZ = nextZ - robot.getZ();

    double magnitude = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);

    if (magnitude != 0) {
      dirX /= magnitude;
      dirY /= magnitude;
      dirZ /= magnitude;
    } else {
      dirX = 0;
      dirY = 0;
      dirZ = 0;
    }

    robot.setDeltaMovement(dirX / 10.0, dirY / 10.0, dirZ / 10.0);
  }

  @Override
  public int getEnergyCost() {
    return 3;
  }
}
