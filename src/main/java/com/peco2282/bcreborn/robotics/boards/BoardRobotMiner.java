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
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchAndEquipItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;

public class BoardRobotMiner extends BoardRobotGenericBreakBlock<BoardRobotMiner> {
  private static final int MAX_HARVEST_LEVEL = 3;
  private int harvestLevel = 0;

  public BoardRobotMiner(RobotEntityBase iRobot) {
    super(RoboticsAIType.MINER, iRobot);
    detectHarvestLevel();
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    super.delegateAIEnded(ai);

    if (ai instanceof AIRobotFetchAndEquipItemStack) {
      if (ai.success()) {
        detectHarvestLevel();
      }
    }
  }

  private void detectHarvestLevel() {
    ItemStack stack = robot.getMainHandItem();

    if (!stack.isEmpty() && stack.is(ItemTags.PICKAXES)) {
      if (stack.getItem() instanceof TieredItem tieredItem) {
        Tier tier = tieredItem.getTier();
        harvestLevel = tier.getLevel();
      }
    }
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return RoboticsRedstoneRobots.ROBOT_MINER.get();
  }

  @Override
  public boolean isExpectedTool(ItemStack stack) {
    return !stack.isEmpty() && stack.is(ItemTags.PICKAXES);
  }

  @Override
  public boolean isExpectedBlock(Level world, int x, int y, int z) {
    return BuildCraftAPI.getWorldProperty("ore@hardness=" + Math.min(MAX_HARVEST_LEVEL, harvestLevel))
      .get(world, new BlockPos(x, y, z));
  }
}
