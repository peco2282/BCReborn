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
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilterTool;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class AIRobotFetchAndEquipItemStack extends AIRobot {

  private IStackFilter filter;
  private int delay = 0;

  public AIRobotFetchAndEquipItemStack(EntityRobotBase iRobot) {
    super(iRobot);
  }

  public AIRobotFetchAndEquipItemStack(EntityRobotBase iRobot, IStackFilter iFilter) {
    this(iRobot);

    this.filter = iFilter;
    if (iRobot.getLinkedStation() != null) {
      IStackFilter gateFilter = ActionRobotFilterTool.getGateFilter(iRobot.getLinkedStation());
      this.filter = stack -> gateFilter.matches(stack) && iFilter.matches(stack);
    }
  }

  @Override
  public void start() {
    startDelegateAI(new AIRobotGotoStationToLoad(robot, filter, 1));
  }

  @Override
  public void update() {
    if (robot.getDockingStation() == null) {
      setSuccess(false);
      terminate();
    }

    if (delay++ > 40) {
      if (equipItemStack()) {
        terminate();
      } else {
        delay = 0;
        startDelegateAI(new AIRobotGotoStationToLoad(robot, filter, 1));
      }
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStationToLoad) {
      if (filter == null) {
        // filter can't be retreived, usually because of a load operation.
        // Force a hard abort, preventing parent AI to continue normal
        // sequence of actions and possibly re-starting this.
        abort();
        return;
      }
      if (!ai.success()) {
        setSuccess(false);
        terminate();
      }
    }
  }

  private boolean equipItemStack() {
    if (robot.getDockingStation() == null) {
      return false;
    }
    Container tileInventory = robot.getDockingStation().getItemInput();
    if (tileInventory == null) {
      return false;
    }

    ItemStack possible = AIRobotLoad.takeSingle(robot.getDockingStation(), filter, true);
    if (possible.isEmpty()) {
      return false;
    }
    robot.setItemInUse(possible);
    return true;
  }
}
