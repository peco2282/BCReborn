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

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.IBlockFilter;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.nbt.CompoundTag;

public class AIRobotSearchAndGotoBlock extends AIRobot<AIRobotSearchAndGotoBlock> {

  private BlockIndex blockFound;

  private IBlockFilter filter;
  private boolean random;
  private double maxDistanceToEnd;

  public AIRobotSearchAndGotoBlock(RobotEntityBase iRobot) {
    super(RoboticsAIType.SEARCH_AND_GOTO_BLOCK, iRobot);

    blockFound = null;

    random = false;
    filter = null;
  }

  public AIRobotSearchAndGotoBlock(RobotEntityBase iRobot, boolean iRandom,
                                   IBlockFilter iFilter) {
    this(iRobot, iRandom, iFilter, 0);
  }

  public AIRobotSearchAndGotoBlock(RobotEntityBase iRobot, boolean iRandom,
                                   IBlockFilter iFilter, double iMaxDistanceToEnd) {
    this(iRobot);

    random = iRandom;
    filter = iFilter;
    maxDistanceToEnd = iMaxDistanceToEnd;
  }

  @Override
  public void start() {
    startDelegateAI(new AIRobotSearchBlock(robot, random, filter, maxDistanceToEnd));
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotSearchBlock searchAI) {
      if (ai.success()) {
        if (searchAI.takeResource()) {
          blockFound = searchAI.blockFound;
          startDelegateAI(new AIRobotGotoBlock(robot, searchAI.path));
        } else {
          terminate();
        }
      } else {
        terminate();
      }
    } else if (ai instanceof AIRobotGotoBlock) {
      if (!ai.success()) {
        releaseBlockFound();
      }
      terminate();
    }
  }

  @Override
  public boolean success() {
    return blockFound != null;
  }

  private void releaseBlockFound() {
    if (blockFound != null) {
      robot.getRegistry().release(new ResourceIdBlock(blockFound.toBlockPos()));
      blockFound = null;
    }
  }

  public BlockIndex getBlockFound() {
    return blockFound;
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    if (blockFound != null) {
      CompoundTag sub = new CompoundTag();
      blockFound.writeTo(sub);
      nbt.put("indexStored", sub);
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("indexStored")) {
      blockFound = new BlockIndex(nbt.getCompound("indexStored"));
    }
  }
}
