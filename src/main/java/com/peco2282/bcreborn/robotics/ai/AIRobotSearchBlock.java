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

import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.IBlockFilter;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Iterator;
import java.util.LinkedList;

public class AIRobotSearchBlock extends AIRobot<AIRobotSearchBlock> {

  public BlockPos blockFound;
  public LinkedList<BlockPos> path;
  private Iterator<BlockPos> blockIter;

  public AIRobotSearchBlock(RobotEntityBase iRobot) {
    super(RoboticsAIType.SEARCH_BLOCK, iRobot);
  }

  public AIRobotSearchBlock(RobotEntityBase iRobot, boolean random, IBlockFilter iFilter,
                            double iMaxDistanceToEnd) {
    super(RoboticsAIType.SEARCH_BLOCK, iRobot);

    // private PathFindingSearch blockScanner = null; // TODO: Implement
    IZone zone = iRobot.getZoneToWork();
    blockFound = null;
    path = null;
  }

  @Override
  public void start() {
    // Implementation simplified for now
  }

  @Override
  public void update() {
    // Implementation simplified for now
    terminate();
  }

  @Override
  public void end() {
  }

  @Override
  public boolean success() {
    return blockFound != null;
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
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

  public boolean takeResource() {
    if (blockFound == null) return false;
    return robot.getRegistry().take(new ResourceIdBlock(blockFound), robot);
  }

  @Override
  public int getEnergyCost() {
    return 2;
  }

}