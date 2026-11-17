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

import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.PathFinding;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;

public class AIRobotGotoBlock extends AIRobotGoto<AIRobotGotoBlock> {

  private PathFinding pathSearch;
  /** Original {@code IterableAlgorithmRunner(pathSearch, 50)}: give up after this many steps. */
  private static final int MAX_SEARCH_STEPS = 50;
  /** Path finding steps per tick; the original ran 1000 per step in a thread of its own. */
  private static final int SEARCH_BUDGET = 400;
  private int searchSteps;
  /** Whether this AI looked for a path itself (original: {@code pathSearchJob != null}). */
  private boolean searched;
  private LinkedList<BlockPos> path;
  private double prevDistance = Double.MAX_VALUE;
  private float finalX, finalY, finalZ;
  private double maxDistance = 0;
  private BlockPos lastBlockInPath;
  private boolean loadedFromNBT;

  public AIRobotGotoBlock(RobotEntityBase iRobot) {
    super(RoboticsAIType.GOTO_BLOCK, iRobot);
  }

  public AIRobotGotoBlock(RobotEntityBase robot, int x, int y, int z) {
    this(robot);
    finalX = x;
    finalY = y;
    finalZ = z;
  }

  public AIRobotGotoBlock(RobotEntityBase robot, int x, int y, int z, double iMaxDistance) {
    this(robot, x, y, z);

    maxDistance = iMaxDistance;
  }

  public AIRobotGotoBlock(RobotEntityBase robot, LinkedList<BlockPos> iPath) {
    this(robot);
    path = iPath;
    finalX = path.getLast().getX();
    finalY = path.getLast().getY();
    finalZ = path.getLast().getZ();
    setNextInPath();
  }

  @Override
  public void start() {
    robot.undock();
  }

  @Override
  public void update() {
    if (loadedFromNBT) {
      // Prevent a race condition with terminate() being called in
      // setNextInPath.
      setNextInPath();
      loadedFromNBT = false;
    }

    if (path == null && pathSearch == null) {
      pathSearch = new PathFinding(robot.level(), new BlockPos((int) Math.floor(robot.getX()),
        (int) Math.floor(robot.getY()), (int) Math.floor(robot.getZ())), new BlockPos(
        (int) Math.floor(finalX), (int) Math.floor(finalY), (int) Math.floor(finalZ)), maxDistance, 96);
      searchSteps = 0;
      searched = true;
    } else if (path != null) {
      double distance = robot.distanceToSqr(nextX, nextY, nextZ);

      if (!robot.isMoving() || distance > prevDistance) {
        if (!path.isEmpty()) {
          path.removeFirst();
        }

        setNextInPath();
      } else {
        prevDistance = robot.distanceToSqr(nextX, nextY, nextZ);
      }
    } else {
      // The search runs on the server thread, a few steps per tick: reading the world from a thread is not safe.
      if (!pathSearch.isDone() && searchSteps++ < MAX_SEARCH_STEPS) {
        pathSearch.iterate(SEARCH_BUDGET);
      }
      if (pathSearch.isDone() || searchSteps >= MAX_SEARCH_STEPS) {
        path = pathSearch.getResult();
        pathSearch = null;

        if (path.isEmpty()) {
          setSuccess(false);
          terminate();
          return;
        }

        lastBlockInPath = path.getLast();

        setNextInPath();
      }
    }

    if (path != null && path.isEmpty()) {
      robot.setDeltaMovement(0, 0, 0);

      if (lastBlockInPath != null) {
        robot.setPos(lastBlockInPath.getX() + 0.5, lastBlockInPath.getY() + 0.5, lastBlockInPath.getZ() + 0.5);
      }
      terminate();
    }
  }

  private void setNextInPath() {
    if (!path.isEmpty()) {
      boolean isFirst = prevDistance == Double.MAX_VALUE;

      BlockPos next = path.getFirst();
      prevDistance = Double.MAX_VALUE;

      if (isFirst || BuildCraftAPI.isSoftBlock(robot.level(), next.getX(), next.getY(), next.getZ())) {
        setDestination(robot, next.getX() + 0.5F, next.getY() + 0.5F, next.getZ() + 0.5F);
        robot.aimItemAt(next.getX(), next.getY(), next.getZ());
      } else {
        // Path invalid! A new one is searched on the next update.
        path = null;
        pathSearch = null;
        robot.setDeltaMovement(0, 0, 0);
      }
    }
  }

  @Override
  public void end() {
    pathSearch = null;
    if (searched) {
      // Original: a robot that searched its own path stops when the AI ends (also when it is aborted on the way).
      robot.setDeltaMovement(0, 0, 0);
    }
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    nbt.putFloat("finalX", finalX);
    nbt.putFloat("finalY", finalY);
    nbt.putFloat("finalZ", finalZ);
    nbt.putDouble("maxDistance", maxDistance);

    if (path != null) {
      long[] sub = path.stream().mapToLong(BlockPos::asLong).toArray();

      nbt.putLongArray("path", sub);
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    finalX = nbt.getFloat("finalX");
    finalY = nbt.getFloat("finalY");
    finalZ = nbt.getFloat("finalZ");
    maxDistance = nbt.getDouble("maxDistance");

    if (nbt.contains("path")) {
      long[] pathList = nbt.getLongArray("path");

      path = new LinkedList<>();

      for (long l : pathList) {
        path.add(BlockPos.of(l));
      }
    }

    loadedFromNBT = true;
  }
}
