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

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.*;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BoardRobotBomber extends RedstoneBoardRobot {

  private final int flyingHeight = 20;

  public BoardRobotBomber(EntityRobotBase iRobot) {
    super(iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return BCBoardNBT.REGISTRY.get("bomber");
  }

  @Override
  public final void update() {
    boolean containItems = robot.containsItems();

    if (!containItems) {
      startDelegateAI(new AIRobotGotoStationAndLoad(robot, stack -> stack.is(Items.TNT), AIRobotLoad.ANY_QUANTITY));
    } else {
      startDelegateAI(new AIRobotSearchRandomGroundBlock(robot, 100, (world, pos) -> {
        return pos.getY() < world.getMaxBuildHeight() - flyingHeight && !world.isEmptyBlock(pos);
      }, robot.getZoneToWork()));
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStationAndLoad) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotSearchRandomGroundBlock searchAI) {
      if (ai.success()) {
        startDelegateAI(new AIRobotGotoBlock(robot, searchAI.blockFound.x,
          searchAI.blockFound.y + flyingHeight,
          searchAI.blockFound.z));
      } else {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotGotoBlock) {
      if (ai.success()) {
        // Removal from inventory and spawning TNT should be handled carefully
        // For now simplified logic
        ItemStack tntStack = ItemStack.EMPTY;
        for (int i = 0; i < robot.getContainerSize(); i++) {
          ItemStack s = robot.getItem(i);
          if (!s.isEmpty() && s.is(Items.TNT)) {
            tntStack = robot.removeItem(i, 1);
            break;
          }
        }

        if (!tntStack.isEmpty()) {
          PrimedTnt tnt = new PrimedTnt(robot.level(), robot.getX() + 0.25,
            robot.getY() - 1,
            robot.getZ() + 0.25,
            null);
          tnt.setFuse(37);
          robot.level().addFreshEntity(tnt);
          // Sound handled by fresh entity or manual call if needed
        }
      } else {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    }
  }
}
