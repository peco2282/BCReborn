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
package com.peco2282.bcreborn.common.utils;

import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.core.IZone;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

public class PathFindingSearch implements IIterableAlgorithm {

  public static final int PATH_ITERATIONS = 1000;
  private static final HashMap<ResourceKey<Level>, HashSet<BlockPos>> reservations = new HashMap<>();

  private final Level world;
  private final BlockPos start;
  private final List<PathFinding> pathFinders;
  private final IBlockFilter pathFound;
  private final IZone zone;
  private final float maxDistance;
  private final Iterator<BlockPos> blockIter;

  private final double maxDistanceToEnd;

  public PathFindingSearch(Level iWorld, BlockPos iStart,
                           Iterator<BlockPos> iBlockIter, IBlockFilter iPathFound,
                           double iMaxDistanceToEnd, float iMaxDistance, IZone iZone) {
    world = iWorld;
    start = iStart;
    pathFound = iPathFound;

    maxDistance = iMaxDistance;
    maxDistanceToEnd = iMaxDistanceToEnd;
    zone = iZone;
    blockIter = iBlockIter;

    pathFinders = new LinkedList<>();
  }

  @Override
  public void iterate() {
    if (pathFinders.size() < 5 && blockIter.hasNext()) {
      iterateSearch(PATH_ITERATIONS * 10);
    }
    iteratePathFind(PATH_ITERATIONS);
  }

  private void iterateSearch(int itNumber) {
    for (int i = 0; i < itNumber; ++i) {
      if (!blockIter.hasNext()) {
        return;
      }

      BlockPos delta = blockIter.next();
      BlockPos block = new BlockPos(start.getX() + delta.getX(),
        Math.max((start.getY() + delta.getY()), 0),
        start.getZ() + delta.getZ());
      if (isLoadedChunk(block.getX(), block.getZ())) {
        if (isTarget(block)) {
          pathFinders.add(new PathFinding(world, start, block, maxDistanceToEnd, maxDistance));
        }
      }

      if (pathFinders.size() >= 5) {
        return;
      }
    }
  }

  private boolean isTarget(BlockPos block) {
    if (!zone.contains(block.getX(), block.getY(), block.getZ())) {
      return false;
    }
    if (!pathFound.matches(world, block)) {
      return false;
    }
    synchronized (reservations) {
      if (reservations.containsKey(world.dimension())) {
        HashSet<BlockPos> dimReservations = reservations
          .get(world.dimension());
        if (dimReservations.contains(block)) {
          return false;
        }
      }
    }
    return BuildCraftAPI.isSoftBlock(world, block.getX() - 1, block.getY(), block.getZ())
      || BuildCraftAPI.isSoftBlock(world, block.getX() + 1, block.getY(), block.getZ())
      || BuildCraftAPI.isSoftBlock(world, block.getX(), block.getY(), block.getZ() - 1)
      || BuildCraftAPI.isSoftBlock(world, block.getX(), block.getY(), block.getZ() + 1)
      || BuildCraftAPI.isSoftBlock(world, block.getX(), block.getY() - 1, block.getZ())
      || BuildCraftAPI.isSoftBlock(world, block.getX(), block.getY() + 1, block.getZ());
  }

  private boolean isLoadedChunk(int x, int z) {
    return world.getChunkSource().hasChunk(x >> 4, z >> 4);
  }

  public void iteratePathFind(int itNumber) {
    for (PathFinding pathFinding : new ArrayList<>(pathFinders)) {
      pathFinding.iterate(itNumber / pathFinders.size());
      if (pathFinding.isDone()) {
        LinkedList<BlockPos> path = pathFinding.getResult();
        if (!path.isEmpty()) {
          if (reserve(pathFinding.end())) {
            return;
          }
        }
        pathFinders.remove(pathFinding);
      }
    }
  }

  @Override
  public boolean isDone() {
    for (PathFinding pathFinding : pathFinders) {
      if (pathFinding.isDone()) {
        return true;
      }
    }
    return !blockIter.hasNext();
  }

  public LinkedList<BlockPos> getResult() {
    for (PathFinding pathFinding : pathFinders) {
      if (pathFinding.isDone()) {
        return pathFinding.getResult();
      }
    }
    return new LinkedList<>();
  }

  public BlockPos getResultTarget() {
    for (PathFinding pathFinding : pathFinders) {
      if (pathFinding.isDone()) {
        return pathFinding.end();
      }
    }
    return BlockPos.ZERO;
  }

  private boolean reserve(BlockPos block) {
    synchronized (reservations) {
      if (!reservations.containsKey(world.dimension())) {
        reservations.put(world.dimension(),
          new HashSet<>());
      }
      HashSet<BlockPos> dimReservations = reservations
        .get(world.dimension());
      if (dimReservations.contains(block)) {
        return false;
      }
      dimReservations.add(block);
      return true;
    }
  }

  public void unreserve(BlockPos block) {
    synchronized (reservations) {
      if (reservations.containsKey(world.dimension())) {
        reservations.get(world.dimension()).remove(block);
      }
    }
  }
}
