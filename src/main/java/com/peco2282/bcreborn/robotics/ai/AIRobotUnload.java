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
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.transport.IInjectable;
import com.peco2282.bcreborn.common.inventory.InventoryIterator;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class AIRobotUnload extends AIRobot<AIRobotUnload> {

  private int waitedCycles = 0;

  public AIRobotUnload(RobotEntityBase iRobot) {
    super(RoboticsAIType.UNLOAD, iRobot);
  }

  public static boolean unload(RobotEntityBase robot, DockingStation<?> station, boolean doUnload) {
    IInjectable output = station.getItemOutput();
    if (output == null) {
      return false;
    }

    Direction injectSide = station.getItemOutputSide();
    if (!output.canInjectItems(injectSide)) {
      return false;
    }

    for (IInvSlot robotSlot : InventoryIterator.getIterable(robot, Direction.UP)) {
      if (robotSlot.getStackInSlot().isEmpty()) {
        continue;
      }

      // In 1.20.1 we use a simplified version for now to avoid buildcraft API dependency issues
      // Logic should be restored once statements API is fully ported
      boolean canInteract = true;

      if (!canInteract) {
        continue;
      }

      ItemStack stack = robotSlot.getStackInSlot();
      int used = output.injectItem(stack, doUnload, injectSide, null);

      if (used > 0) {
        if (doUnload) {
          robotSlot.decreaseStackInSlot(used);
        }
        return true;
      }
    }

    ItemStack held = robot.getMainHandItem();
    if (!held.isEmpty()) {
      // Simplified interact check
      boolean canInteract = true;

      if (!canInteract) {
        return false;
      }

      int used = output.injectItem(held, doUnload, injectSide, null);

      if (used > 0) {
        if (doUnload) {
          if (held.getCount() <= used) {
            robot.setItemInUse(ItemStack.EMPTY);
          } else {
            held.shrink(used);
          }
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public void update() {
    waitedCycles++;

    if (waitedCycles > 40) {
      if (unload(robot, robot.getDockingStation(), true)) {
        waitedCycles = 0;
      } else {
        setSuccess(!robot.containsItems());
        terminate();
      }
    }
  }

  @Override
  public int getEnergyCost() {
    return 10;
  }
}
