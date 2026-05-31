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
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.AIRobotBreak;
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchAndEquipItemStack;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnload;
import net.minecraft.world.item.ItemStack;

public abstract class BoardRobotGenericBreakBlock extends BoardRobotGenericSearchBlock {

  public BoardRobotGenericBreakBlock(EntityRobotBase iRobot) {
    super(iRobot);
  }

  public abstract boolean isExpectedTool(ItemStack stack);

  @Override
  public final void update() {
    ItemStack held = robot.getMainHandItem();
    if (!isExpectedTool(ItemStack.EMPTY) && held.isEmpty()) {
      startDelegateAI(new AIRobotFetchAndEquipItemStack(robot, (ItemStack stack) -> !stack.isEmpty()
        && (stack.getDamageValue() < stack.getMaxDamage())
        && isExpectedTool(stack)));
    } else if (!held.isEmpty() && held.getDamageValue() >= held.getMaxDamage()) {
      startDelegateAI(new AIRobotGotoStationAndUnload(robot));
    } else if (blockFound() != null) {
      startDelegateAI(new AIRobotBreak(robot, blockFound()));
    } else {
      super.update();
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotFetchAndEquipItemStack || ai instanceof AIRobotGotoStationAndUnload) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotBreak) {
      releaseBlockFound(ai.success());
    }
    super.delegateAIEnded(ai);
  }
}
