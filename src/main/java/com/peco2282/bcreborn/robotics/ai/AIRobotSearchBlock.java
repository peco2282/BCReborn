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

import com.peco2282.bcreborn.common.utils.BlockScannerZoneRandom;
import com.peco2282.bcreborn.common.utils.BlockScannerRandom;
import com.peco2282.bcreborn.common.utils.BlockScannerExpanding;
import com.peco2282.bcreborn.common.utils.PathFindingSearch;
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

/**
 * Port of the original {@code AIRobotSearchBlock}: looks for the nearest block that matches the filter (or a
 * random one) and a path to it, inside the robot's work area if it has one.
 * <p>
 * The original ran {@link PathFindingSearch} in its own thread. Reading the world from another thread is not safe
 * here, so the search runs on the server thread, a limited number of steps per tick.
 */
public class AIRobotSearchBlock extends AIRobot<AIRobotSearchBlock> {
  /** Candidate blocks looked at per tick. */
  private static final int SEARCH_BUDGET = 2000;
  /** Path finding steps per tick, shared between the candidates. */
  private static final int PATH_BUDGET = 400;
  /** Original {@code IterableAlgorithmRunner}: give up after this many steps. */
  private static final int MAX_STEPS = 1000;

  public BlockPos blockFound;
  public LinkedList<BlockPos> path;
  private PathFindingSearch blockScanner;
  private IBlockFilter pathFound;
  private Iterator<BlockPos> blockIter;
  private double maxDistanceToEnd;
  private IZone zone;
  private int steps;

  public AIRobotSearchBlock(RobotEntityBase iRobot) {
    super(RoboticsAIType.SEARCH_BLOCK, iRobot);
  }

  public AIRobotSearchBlock(RobotEntityBase iRobot, boolean random, IBlockFilter iFilter,
                            double iMaxDistanceToEnd) {
    super(RoboticsAIType.SEARCH_BLOCK, iRobot);

    pathFound = iFilter;
    zone = iRobot.getZoneToWork();
    if (!random) {
      blockIter = new BlockScannerExpanding().iterator();
    } else if (zone != null) {
      BlockPos pos = iRobot.blockPosition();
      blockIter = new BlockScannerZoneRandom(pos.getX(), pos.getY(), pos.getZ(), iRobot.level().random, zone).iterator();
    } else {
      blockIter = new BlockScannerRandom(iRobot.level().random, 64).iterator();
    }
    blockFound = null;
    path = null;
    maxDistanceToEnd = iMaxDistanceToEnd;
  }

  @Override
  public void start() {
    if (blockIter == null || pathFound == null) return;
    blockScanner = new PathFindingSearch(robot.level(), robot.blockPosition(), blockIter, pathFound, maxDistanceToEnd, 96, zone);
  }

  @Override
  public void update() {
    if (blockScanner == null) {
      // This is probably due to starting this AI from a load.
      abort();
      return;
    }

    if (!blockScanner.isDone() && steps++ < MAX_STEPS) {
      blockScanner.iterate(SEARCH_BUDGET, PATH_BUDGET);
    }

    if (blockScanner.isDone() || steps >= MAX_STEPS) {
      path = blockScanner.getResult();
      if (path != null && !path.isEmpty()) {
        // The path may end next to the target (maxDistanceToEnd), so the target is asked for, not taken from
        // the path; the robot stops one step before the end.
        path.removeLast();
        blockFound = blockScanner.getResultTarget();
      } else {
        path = null;
      }
      terminate();
    }
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

  /** Takes the found block in the robot registry and gives up the search's own reservation. */
  public boolean takeResource() {
    if (blockFound == null) return false;
    boolean taken = robot.getRegistry().take(new ResourceIdBlock(blockFound), robot);
    unreserve();
    return taken;
  }

  public void unreserve() {
    if (blockScanner != null && blockFound != null) blockScanner.unreserve(blockFound);
  }

  @Override
  public int getEnergyCost() {
    return 2;
  }

}