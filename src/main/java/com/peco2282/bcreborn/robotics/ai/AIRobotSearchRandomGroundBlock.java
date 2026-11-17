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
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.IBlockFilter;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class AIRobotSearchRandomGroundBlock extends AIRobot<AIRobotSearchRandomGroundBlock> {

  private static final int MAX_ATTEMPTS = 4096;

  public BlockPos blockFound;

  private int range;
  private IBlockFilter filter;
  private IZone zone;
  private int attempts = 0;

  public AIRobotSearchRandomGroundBlock(RobotEntityBase iRobot) {
    super(RoboticsAIType.SEARCH_RANDOM_GROUND_BLOCK, iRobot);
  }

  public AIRobotSearchRandomGroundBlock(RobotEntityBase iRobot, int iRange, IBlockFilter iFilter, IZone iZone) {
    this(iRobot);

    range = iRange;
    filter = iFilter;
    zone = iZone;
  }

  @Override
  public void update() {
    if (filter == null) {
      terminate();
      return;
    }

    attempts++;

    if (attempts > MAX_ATTEMPTS) {
      terminate();
      return;
    }

    int x, z;

    if (zone == null) {
      double r = robot.level().random.nextFloat() * range;
      float a = robot.level().random.nextFloat() * 2.0F * (float) Math.PI;

      x = (int) (Mth.cos(a) * r + Math.floor(robot.getX()));
      z = (int) (Mth.sin(a) * r + Math.floor(robot.getZ()));
    } else {
      BlockPos b = zone.getRandomBlockIndex(robot.level().random);
      x = b.getX();
      z = b.getZ();
    }

    // From the top of the world down to the first block (the world no longer starts at y = 0).
    for (int y = robot.level().getMaxBuildHeight() - 1; y >= robot.level().getMinBuildHeight(); --y) {
      var pos = new BlockPos(x, y, z);
      if (filter.matches(robot.level(), pos)) {
        blockFound = pos;
        terminate();
        return;
      } else if (!robot.level().getBlockState(pos).isAir()) {
        return;
      }
    }
  }

  @Override
  public boolean success() {
    return blockFound != null;
  }

  @Override
  public int getEnergyCost() {
    return 2;
  }
}
