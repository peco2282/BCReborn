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
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BoardRobotLumberjack extends BoardRobotGenericBreakBlock {

  public BoardRobotLumberjack(RobotEntityBase iRobot) {
    super(iRobot);
  }

  public BoardRobotLumberjack(RobotEntityBase iRobot, CompoundTag nbt) {
    super(iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return BCBoardNBT.REGISTRY.get("lumberjack");
  }

  @Override
  public boolean isExpectedTool(ItemStack stack) {
    return !stack.isEmpty() && stack.is(ItemTags.AXES);
  }

  @Override
  public boolean isExpectedBlock(Level world, int x, int y, int z) {
    return BuildCraftAPI.getWorldProperty("wood").get(world, new BlockPos(x, y, z));
  }
}
