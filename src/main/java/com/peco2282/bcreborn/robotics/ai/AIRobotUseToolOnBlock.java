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
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AIRobotUseToolOnBlock extends AIRobot {

  private BlockIndex useToBlock;
  private int useCycles = 0;

  public AIRobotUseToolOnBlock(RobotEntityBase iRobot) {
    super(iRobot);
  }

  public AIRobotUseToolOnBlock(RobotEntityBase iRobot, BlockIndex index) {
    this(iRobot);

    useToBlock = index;
  }

  @Override
  public void start() {
    robot.aimItemAt(useToBlock.x, useToBlock.y, useToBlock.z);
    robot.setItemActive(true);
  }

  @Override
  public void update() {
    useCycles++;

    if (useCycles > 40) {
      ItemStack stack = robot.getMainHandItem();

      Player player = BuildCraftAPI.proxy.getBuildCraftPlayer((ServerLevel) robot.level())
        .get();
      BlockPos pos = useToBlock.toBlockPos();
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
      CompoundTag sub = new CompoundTag();
      useToBlock.writeTo(sub);
      nbt.put("blockFound", sub);
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("blockFound")) {
      useToBlock = new BlockIndex(nbt.getCompound("blockFound"));
    }
  }
}
