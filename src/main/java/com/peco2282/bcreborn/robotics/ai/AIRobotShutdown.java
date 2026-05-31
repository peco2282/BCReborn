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

public class AIRobotShutdown extends AIRobot {
  private int skip;
  private double motionX;
  private double motionZ;

  public AIRobotShutdown(EntityRobotBase iRobot) {
    super(iRobot);
    skip = 0;
    motionX = robot.getDeltaMovement().x;
    motionZ = robot.getDeltaMovement().z;
  }

  @Override
  public void start() {
    robot.undock();
    robot.setDeltaMovement(motionX, -0.075, motionZ);
  }

  private boolean isBlocked(double yOffset) {
    return !robot.level().noCollision(robot, robot.getBoundingBox().move(robot.getDeltaMovement().x, yOffset, robot.getDeltaMovement().z));
  }

  @Override
  public void update() {
    if (skip == 0) {
      if (!isBlocked(-0.075)) {
        robot.setDeltaMovement(robot.getDeltaMovement().x, -0.075, robot.getDeltaMovement().z);
      } else {
        while (isBlocked(0)) {
          robot.setPos(robot.getX(), robot.getY() + 0.075, robot.getZ());
        }
        robot.setDeltaMovement(0, 0, 0);
        if (motionX != 0 || motionZ != 0) {
          motionX = 0;
          motionZ = 0;
          skip = 0;
        } else {
          skip = 20;
        }
      }
    } else {
      skip--;
    }
  }

  @Override
  public int getEnergyCost() {
    return 0;
  }
}
