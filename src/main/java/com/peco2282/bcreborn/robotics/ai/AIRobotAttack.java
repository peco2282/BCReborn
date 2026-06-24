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
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import net.minecraft.world.entity.Entity;

public class AIRobotAttack extends AIRobot<AIRobotAttack> {

  private Entity target;

  private int delay = 10;

  public AIRobotAttack(RobotEntityBase iRobot) {
    super(RoboticsAIType.ATTACK, iRobot);
  }

  public AIRobotAttack(RobotEntityBase iRobot, Entity iTarget) {
    this(iRobot);

    target = iTarget;
  }

  @Override
  public void preempt(AIRobot<?> ai) {
    if (ai instanceof AIRobotGotoBlock) {
      // target may become null in the event of a load. In that case, just
      // go to the expected location.
      if (target != null && robot.distanceTo(target) <= 2.0) {
        abortDelegateAI();
        robot.setItemActive(true);
      }
    }
  }

  @Override
  public void update() {
    if (target == null || target.isRemoved()) {
      terminate();
      return;
    }

    if (robot.distanceTo(target) > 2.0) {
      startDelegateAI(new AIRobotGotoBlock(robot, (int) Math.floor(target.getX()),
        (int) Math.floor(target.getY()), (int) Math.floor(target.getZ())));
      robot.setItemActive(false);

      return;
    }

    delay++;

    if (delay > 20) {
      delay = 0;
      if (robot instanceof RobotEntity robotImpl) {
        robotImpl.attackTargetEntityWithCurrentItem(target);
      }
      robot.aimItemAt((int) Math.floor(target.getX()), (int) Math.floor(target.getY()),
        (int) Math.floor(target.getZ()));
    }
  }

  @Override
  public void end() {
    robot.setItemActive(false);
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotGotoBlock) {
      if (!ai.success()) {
        robot.unreachableEntityDetected(target);
      }
      terminate();
    }
  }

  @Override
  public int getEnergyCost() {
    return 10;
  }
}
