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
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.IterableAlgorithmRunner;
import com.peco2282.bcreborn.common.utils.PathFinding;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.LinkedList;

public class AIRobotGotoBlock extends AIRobotGoto<AIRobotGotoBlock> {

  private PathFinding pathSearch;
  private IterableAlgorithmRunner pathSearchJob;
  private LinkedList<BlockIndex> path;
  private double prevDistance = Double.MAX_VALUE;
  private float finalX, finalY, finalZ;
  private double maxDistance = 0;
  private BlockIndex lastBlockInPath;
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

  public AIRobotGotoBlock(RobotEntityBase robot, LinkedList<BlockIndex> iPath) {
    this(robot);
    path = iPath;
    finalX = path.getLast().x;
    finalY = path.getLast().y;
    finalZ = path.getLast().z;
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
      pathSearch = new PathFinding(robot.level(), new BlockIndex((int) Math.floor(robot.getX()),
        (int) Math.floor(robot.getY()), (int) Math.floor(robot.getZ())), new BlockIndex(
        (int) Math.floor(finalX), (int) Math.floor(finalY), (int) Math.floor(finalZ)), maxDistance, 96);

      pathSearchJob = new IterableAlgorithmRunner(pathSearch, 50);
      pathSearchJob.start();
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
      if (pathSearchJob.isDone()) {
        path = pathSearch.getResult();

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
        robot.setPos(lastBlockInPath.x + 0.5, lastBlockInPath.y + 0.5, lastBlockInPath.z + 0.5);
      }
      terminate();
    }
  }

  private void setNextInPath() {
    if (!path.isEmpty()) {
      boolean isFirst = prevDistance == Double.MAX_VALUE;

      BlockIndex next = path.getFirst();
      prevDistance = Double.MAX_VALUE;

      if (isFirst || BuildCraftAPI.isSoftBlock(robot.level(), next.x, next.y, next.z)) {
        setDestination(robot, next.x + 0.5F, next.y + 0.5F, next.z + 0.5F);
        robot.aimItemAt(next.x, next.y, next.z);
      } else {
        // Path invalid!
        path = null;

        if (pathSearchJob != null) {
          pathSearchJob.terminate();
          robot.setDeltaMovement(0, 0, 0);
        }
      }
    }
  }

  @Override
  public void end() {
    if (pathSearchJob != null) {
      pathSearchJob.terminate();
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
      ListTag pathList = new ListTag();

      for (BlockIndex i : path) {
        CompoundTag subNBT = new CompoundTag();
        i.writeTo(subNBT);
        pathList.add(subNBT);
      }

      nbt.put("path", pathList);
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
      ListTag pathList = nbt.getList("path", Tag.TAG_COMPOUND);

      path = new LinkedList<>();

      for (int i = 0; i < pathList.size(); ++i) {
        path.add(new BlockIndex(pathList.getCompound(i)));
      }
    }

    loadedFromNBT = true;
  }
}
