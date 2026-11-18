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

import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.ai.*;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BoardRobotBomber extends RedstoneBoardRobot<BoardRobotBomber> {

  private final int flyingHeight = 20;

  public BoardRobotBomber(RobotEntityBase iRobot) {
    super(RoboticsAIType.BOMBER, iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return RoboticsRedstoneRobots.ROBOT_BOMBER.get();
  }

  @Override
  public final void update() {
    boolean containItems = robot.containsItems();

    if (!containItems) {
      startDelegateAI(new AIRobotGotoStationAndLoad(robot, stack -> stack.is(Items.TNT), AIRobotLoad.ANY_QUANTITY));
    } else {
      startDelegateAI(new AIRobotSearchRandomGroundBlock(robot, 100, (world, pos) -> pos.getY() < world.getMaxBuildHeight() - flyingHeight && !world.isEmptyBlock(pos), robot.getZoneToWork()));
    }
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotGotoStationAndLoad) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotSearchRandomGroundBlock searchAI) {
      if (ai.success()) {
        startDelegateAI(new AIRobotGotoBlock(robot, searchAI.blockFound.getX(),
          searchAI.blockFound.getY() + flyingHeight,
          searchAI.blockFound.getZ()));
      } else {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotGotoBlock) {
      if (ai.success()) {
        // Original: take one TNT from the robot and drop it primed (fuse 37) one block below, 0.25 off centre.
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
            robot);
          tnt.setFuse(37);
          robot.level().addFreshEntity(tnt);
          robot.level().playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
      } else {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    }
  }
}
