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
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.core.IWorldProperty;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchAndEquipItemStack;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchAndGotoBlock;
import com.peco2282.bcreborn.robotics.ai.AIRobotUseToolOnBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;

public class BoardRobotFarmer extends RedstoneBoardRobot<BoardRobotFarmer> {

  private BlockPos blockFound;

  public BoardRobotFarmer(RobotEntityBase iRobot) {
    super(RoboticsAIType.FARMER, iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return BCBoardNBT.REGISTRY.get("farmer");
  }

  @Override
  public void update() {
    final IWorldProperty isDirt = BuildCraftAPI.getWorldProperty("dirt");
    if (robot.getMainHandItem().isEmpty()) {
      startDelegateAI(new AIRobotFetchAndEquipItemStack(robot, stack -> !stack.isEmpty() && stack.is(ItemTags.HOES)));
    } else {
      startDelegateAI(new AIRobotSearchAndGotoBlock(robot, false, (world, pos) -> isDirt.get(world, pos)
        && !robot.getRegistry().isTaken(new ResourceIdBlock(pos))
        && world.isEmptyBlock(pos.above())));
    }
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotSearchAndGotoBlock) {
      if (ai.success()) {
        blockFound = ((AIRobotSearchAndGotoBlock) ai).getBlockFound();
        startDelegateAI(new AIRobotUseToolOnBlock(robot, blockFound));
      } else {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotFetchAndEquipItemStack) {
      if (!ai.success()) {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotUseToolOnBlock) {
      releaseBlockFound();
    }
  }

  private void releaseBlockFound() {
    if (blockFound != null) {
      robot.getRegistry().release(new ResourceIdBlock(blockFound));
      blockFound = null;
    }
  }

  @Override
  public void end() {
    releaseBlockFound();
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    if (blockFound != null) {
      nbt.putLong("blockFound", blockFound.asLong());
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("blockFound")) {
      blockFound = BlockPos.of(nbt.getLong("blockFound"));
    }
  }
}
