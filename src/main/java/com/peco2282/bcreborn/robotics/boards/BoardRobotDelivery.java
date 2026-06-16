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
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.ai.AIRobotDisposeItems;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.station.StackRequest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class BoardRobotDelivery extends RedstoneBoardRobot<BoardRobotDelivery> {

  private final ArrayList<ItemStack> deliveryBlacklist = new ArrayList<>();

  private final StackRequest currentRequest = null; // TODO: Implement StackRequest

  public BoardRobotDelivery(RobotEntityBase iRobot) {
    super(RoboticsAIType.DELIVERY, iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return BCBoardNBT.REGISTRY.get("delivery");
  }

  @Override
  public void update() {
    if (robot.containsItems()) {
      startDelegateAI(new AIRobotDisposeItems(robot));
      return;
    }

    // Simplified for now without StackRequest
    startDelegateAI(new AIRobotGotoSleep(robot));
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotDisposeItems) {
      update();
    } else {
      startDelegateAI(new AIRobotGotoSleep(robot));
    }
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);
  }
}
