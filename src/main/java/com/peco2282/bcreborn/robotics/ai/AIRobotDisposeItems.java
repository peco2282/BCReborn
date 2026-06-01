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

import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.inventory.InventoryIterator;
import net.minecraft.world.entity.item.ItemEntity;

public class AIRobotDisposeItems extends AIRobot {

  public AIRobotDisposeItems(RobotEntityBase iRobot) {
    super(iRobot);
  }

  @Override
  public void start() {
    startDelegateAI(new AIRobotGotoStationAndUnload(robot));
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStationAndUnload) {
      if (ai.success()) {
        if (robot.containsItems()) {
          startDelegateAI(new AIRobotGotoStationAndUnload(robot));
        } else {
          terminate();
        }
      } else {
        for (IInvSlot slot : InventoryIterator.getIterable(robot)) {
          if (slot.getStackInSlot() != null) {
            final ItemEntity entity = new ItemEntity(
              robot.level(),
              robot.getX(),
              robot.getY(),
              robot.getZ(),
              slot.getStackInSlot());

            robot.level().addFreshEntity(entity);

            slot.setStackInSlot(null);
          }
        }
        terminate();
      }
    }
  }
}
