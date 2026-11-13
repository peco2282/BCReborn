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
import com.peco2282.bcreborn.common.inventory.ITransactor;
import com.peco2282.bcreborn.common.inventory.InventoryIterator;
import com.peco2282.bcreborn.common.inventory.Transactor;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import com.peco2282.bcreborn.robotics.statements.ActionStationProvideItems;
import net.minecraft.core.Direction;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class AIRobotLoad extends AIRobot<AIRobotLoad> {

  public static final int ANY_QUANTITY = -1;
  private IStackFilter filter;
  private int quantity;
  private int waitedCycles = 0;

  public AIRobotLoad(RobotEntityBase iRobot) {
    super(RoboticsAIType.LOAD, iRobot);
  }

  public AIRobotLoad(RobotEntityBase iRobot, IStackFilter iFilter, int iQuantity) {
    super(RoboticsAIType.LOAD, iRobot);

    filter = iFilter;
    quantity = iQuantity;
  }

  private static boolean canTake(DockingStation<?> station, IInvSlot slot, IStackFilter filter) {
    ItemStack stack = slot.getStackInSlot();
    return !stack.isEmpty()
      && slot.canTakeStackFromSlot(stack)
      && filter.matches(stack)
      && ActionStationProvideItems.canExtractItem(station, stack)
      && ActionRobotFilter.canInteractWithItem(station, filter, ActionStationProvideItems.class);
  }

  /**
   * Like {@link #load(RobotEntityBase, DockingStation, IStackFilter, int, boolean)} but returns the item instead of
   * loading it onto the robot. Only takes a single item.
   */
  public static ItemStack takeSingle(DockingStation<?> station, IStackFilter filter, boolean doTake) {
    if (station == null) {
      return ItemStack.EMPTY;
    }
    Container tileInventory = station.getItemInput();
    if (tileInventory == null) {
      return ItemStack.EMPTY;
    }

    for (IInvSlot slot : InventoryIterator.getIterable(tileInventory, station.getItemInputSide())) {
      if (!canTake(station, slot, filter)) {
        continue;
      }
      if (doTake) {
        return slot.decreaseStackInSlot(1);
      }
      return slot.getStackInSlot().copyWithCount(1);
    }
    return ItemStack.EMPTY;
  }

  /**
   * Original load(): moves items from the station's input inventory into the robot. The station must have an
   * active "provide items" action that allows the item.
   */
  public static boolean load(RobotEntityBase robot, DockingStation<?> station, IStackFilter filter,
                             int quantity, boolean doLoad) {
    if (station == null) {
      return false;
    }

    int loaded = 0;

    Container tileInventory = station.getItemInput();
    if (tileInventory == null) {
      return false;
    }

    for (IInvSlot slot : InventoryIterator.getIterable(tileInventory, station.getItemInputSide())) {
      if (!canTake(station, slot, filter)) {
        continue;
      }

      ITransactor robotTransactor = Transactor.getTransactorFor(robot);

      if (quantity == ANY_QUANTITY) {
        ItemStack added = robotTransactor.add(slot.getStackInSlot(), Direction.UP, doLoad);
        if (doLoad) {
          slot.decreaseStackInSlot(added.getCount());
        }
        return added.getCount() > 0;
      } else {
        ItemStack toAdd = slot.getStackInSlot().copy();
        if (toAdd.getCount() > quantity - loaded) {
          toAdd.setCount(quantity - loaded);
        }

        ItemStack added = robotTransactor.add(toAdd, Direction.UP, doLoad);
        if (doLoad) {
          slot.decreaseStackInSlot(added.getCount());
        }
        loaded += added.getCount();

        if (quantity - loaded <= 0) {
          return true;
        }
      }
    }
    return false;
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
