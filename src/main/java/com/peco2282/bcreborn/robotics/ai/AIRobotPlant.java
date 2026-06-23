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

import com.peco2282.bcreborn.api.crops.CropManager;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.BCFakePlayer;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AIRobotPlant extends AIRobot<AIRobotPlant> {
  private BlockPos blockFound;
  private int delay = 0;

  public AIRobotPlant(RobotEntityBase iRobot) {
    super(RoboticsAIType.PLANT, iRobot);
  }

  public AIRobotPlant(RobotEntityBase iRobot, BlockPos iBlockFound) {
    this(iRobot);

    blockFound = iBlockFound;
  }

  @Override
  public void start() {
    robot.aimItemAt(blockFound.getX(), blockFound.getY(), blockFound.getZ());
    robot.setItemActive(true);
  }

  @Override
  public void update() {
    if (blockFound == null) {
      setSuccess(false);
      terminate();
    }

    if (delay++ > 40) {
      Player player = BCFakePlayer.getBuildCraftPlayer((ServerLevel) robot.level())
        .get();
      if (CropManager.plantCrop(robot.level(), player, robot.getMainHandItem(), blockFound)) {
      } else {
        setSuccess(false);
      }
      if (robot.getMainHandItem().getCount() > 0) {
        BlockUtils.dropItem((ServerLevel) robot.level(),
          new BlockPos(Mth.floor(robot.getX()), Mth.floor(robot.getY()),
            Mth.floor(robot.getZ())), 6000, robot.getMainHandItem());
      }
      robot.setItemInUse(ItemStack.EMPTY);
      terminate();
    }
  }

  @Override
  public void end() {
    robot.setItemActive(false);
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
}
