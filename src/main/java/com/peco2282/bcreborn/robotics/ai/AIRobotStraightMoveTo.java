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

import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import net.minecraft.nbt.CompoundTag;

public class AIRobotStraightMoveTo extends AIRobotGoto {

  private double prevDistance = Double.MAX_VALUE;

  private float x, y, z;

  public AIRobotStraightMoveTo(RobotEntityBase iRobot) {
    super(iRobot);
  }

  public AIRobotStraightMoveTo(RobotEntityBase iRobot, float ix, float iy, float iz) {
    this(iRobot);
    x = ix;
    y = iy;
    z = iz;
    robot.aimItemAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
  }

  @Override
  public void start() {
    robot.undock();
    setDestination(robot, x, y, z);
  }

  @Override
  public void update() {
    double distance = robot.distanceToSqr(nextX, nextY, nextZ);

    if (distance < prevDistance) {
      prevDistance = distance;
    } else {
      robot.setDeltaMovement(0, 0, 0);

      robot.setPos(x, y, z);

      terminate();
    }
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    nbt.putFloat("x", x);
    nbt.putFloat("y", y);
    nbt.putFloat("z", z);
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("x")) {
      x = nbt.getFloat("x");
      y = nbt.getFloat("y");
      z = nbt.getFloat("z");
    }
  }
}
