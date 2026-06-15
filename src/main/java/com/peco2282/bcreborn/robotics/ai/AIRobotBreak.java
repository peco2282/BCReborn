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
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class AIRobotBreak extends AIRobot<AIRobotBreak> {
  private BlockIndex blockToBreak;
  private float blockDamage = 0;

  private BlockState blockState;
  private float hardness;
  private float speed;

  public AIRobotBreak(RobotEntityBase iRobot) {
    super(RoboticsAIType.BREAK, iRobot);
  }

  public AIRobotBreak(RobotEntityBase iRobot, BlockIndex iBlockToBreak) {
    this(iRobot);

    blockToBreak = iBlockToBreak;
  }

  @Override
  public void start() {
    robot.aimItemAt(blockToBreak.x, blockToBreak.y, blockToBreak.z);

    robot.setItemActive(true);
    BlockPos pos = new BlockPos(blockToBreak.x, blockToBreak.y, blockToBreak.z);
    blockState = robot.level().getBlockState(pos);
    hardness = blockState.getDestroySpeed(robot.level(), pos);
    // speed = getBreakSpeed(robot, robot.getMainHandItem(), blockState); // TODO
    speed = 1.0f;
  }

  @Override
  public void update() {
    BlockPos pos = new BlockPos(blockToBreak.x, blockToBreak.y, blockToBreak.z);
    if (blockState == null) {
      blockState = robot.level().getBlockState(pos);
      hardness = blockState.getDestroySpeed(robot.level(), pos);
      speed = 1.0f;
    }

    if (blockState.isAir() || hardness < 0) {
      setSuccess(false);
      terminate();
      return;
    }

    if (hardness != 0) {
      blockDamage += speed / hardness / 30F;
    } else {
      // Instantly break the block
      blockDamage = 1.1F;
    }

    if (blockDamage > 1.0F) {
      robot.level().destroyBlockProgress(robot.getId(), pos, -1);
      blockDamage = 0;

      if (robot.level() instanceof ServerLevel serverLevel) {
        // robot.level().destroyBlock(pos, true, robot); // Simplified harvest
        serverLevel.destroyBlock(pos, true, robot);
      }

      terminate();
    } else {
      robot.level().destroyBlockProgress(robot.getId(), pos, (int) (blockDamage * 10.0F) - 1);
    }
  }

  @Override
  public void end() {
    robot.setItemActive(false);
    robot.level().destroyBlockProgress(robot.getId(), new BlockPos(blockToBreak.x, blockToBreak.y, blockToBreak.z), -1);
  }

  @Override
  public int getEnergyCost() {
    return 10;
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    if (blockToBreak != null) {
      CompoundTag sub = new CompoundTag();
      blockToBreak.writeTo(sub);
      nbt.put("blockToBreak", sub);
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("blockToBreak")) {
      blockToBreak = new BlockIndex(nbt.getCompound("blockToBreak"));
    }
  }
}
