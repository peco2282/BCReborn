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

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class AIRobotUseToolOnBlock extends AIRobot<AIRobotUseToolOnBlock> {

  private BlockPos useToBlock;
  private int useCycles = 0;

  public AIRobotUseToolOnBlock(RobotEntityBase iRobot) {
    super(RoboticsAIType.USE_TOOL_ON_BLOCK, iRobot);
  }

  public AIRobotUseToolOnBlock(RobotEntityBase iRobot, BlockPos index) {
    this(iRobot);

    useToBlock = index;
  }

  @Override
  public void start() {
    robot.aimItemAt(useToBlock.getX(), useToBlock.getY(), useToBlock.getZ());
    robot.setItemActive(true);
  }

  @Override
  public void update() {
    useCycles++;

    if (useCycles > 40) {
      ItemStack stack = robot.getMainHandItem();

      BlockPos pos = useToBlock;
      if (BlockUtils.harvestBlock((ServerLevel) robot.level(), pos, stack)) {
        if (stack.isDamageableItem()) {
          stack.hurtAndBreak(1, robot, r -> {
          });

          if (stack.getDamageValue() >= stack.getMaxDamage()) {
            robot.setItemInUse(ItemStack.EMPTY);
          }
        } else {
          robot.setItemInUse(ItemStack.EMPTY);
        }
      } else {
        setSuccess(false);
        if (!stack.isDamageableItem()) {
          BlockUtils.dropItem((ServerLevel) robot.level(), pos, 6000, stack);
          robot.setItemInUse(ItemStack.EMPTY);
        }
      }

      terminate();
    }
  }

  @Override
  public void end() {
    robot.setItemActive(false);
  }

  @Override
  public int getEnergyCost() {
    return 8;
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    if (useToBlock != null) {
      nbt.putLong("blockFound", useToBlock.asLong());
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("blockFound")) {
      useToBlock = BlockPos.of(nbt.getLong("blockFound"));
    }
  }
}
