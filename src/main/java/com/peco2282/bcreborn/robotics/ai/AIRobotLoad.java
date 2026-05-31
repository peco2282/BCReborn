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
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class AIRobotLoad extends AIRobot {

  public static final int ANY_QUANTITY = -1;
  private IStackFilter filter;
  private int quantity;
  private int waitedCycles = 0;

  public AIRobotLoad(EntityRobotBase iRobot) {
    super(iRobot);
  }

  public AIRobotLoad(EntityRobotBase iRobot, IStackFilter iFilter, int iQuantity) {
    super(iRobot);

    filter = iFilter;
    quantity = iQuantity;
  }

  public static ItemStack takeSingle(DockingStation station, IStackFilter filter, boolean doTake) {
    if (station == null) {
      return ItemStack.EMPTY;
    }

    Container tileInventory = station.getItemInput();
    if (tileInventory == null) {
      return ItemStack.EMPTY;
    }

    for (int i = 0; i < tileInventory.getContainerSize(); i++) {
      ItemStack stack = tileInventory.getItem(i);
      if (stack.isEmpty()) continue;

      if (filter.matches(stack)) {
        if (doTake) {
          return tileInventory.removeItem(i, 1);
        } else {
          ItemStack res = stack.copy();
          res.setCount(1);
          return res;
        }
      }
    }
    return ItemStack.EMPTY;
  }

  public static boolean load(EntityRobotBase robot, DockingStation station, IStackFilter filter,
                             int quantity, boolean doLoad) {
    if (station == null) {
      return false;
    }

    Container tileInventory = station.getItemInput();
    if (tileInventory == null) {
      return false;
    }

    // Simplified loading logic for now
    ItemStack stack = takeSingle(station, filter, doLoad);
    // In 1.20.1 we should probably use capabilities or robot's inventory
    // For now just return true if we could take something
    return !stack.isEmpty();
  }

  @Override
  public void update() {
    if (filter == null) {
      terminate();
      return;
    }

    waitedCycles++;

    if (waitedCycles > 40) {
      setSuccess(load(robot, robot.getDockingStation(), filter, quantity, true));
      terminate();
    }
  }

  @Override
  public int getEnergyCost() {
    return 8;
  }
}
