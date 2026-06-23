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

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.AIRobotType;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchAndGotoBlock;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;

public abstract class BoardRobotGenericSearchBlock<T extends BoardRobotGenericSearchBlock<T>> extends RedstoneBoardRobot<T> {

  private final ArrayList<Block> blockFilter = new ArrayList<>();
  private BlockPos blockFound;

  public BoardRobotGenericSearchBlock(AIRobotType<T> type, RobotEntityBase iRobot) {
    super(type, iRobot);
  }

  /**
   * This function has to be derived in a thread safe manner, as it may be
   * called from parallel jobs. In particular, world should not be directly
   * used, only through WorldProperty class and subclasses.
   */
  public abstract boolean isExpectedBlock(Level world, int x, int y, int z);

  @Override
  public void update() {
    updateFilter();

    startDelegateAI(new AIRobotSearchAndGotoBlock(robot, false, (world, pos) -> {
      if (isExpectedBlock(world, pos.getX(), pos.getY(), pos.getZ())
        && !robot.getRegistry().isTaken(new ResourceIdBlock(pos))) {
        return matchesGateFilter(world, pos);
      } else {
        return false;
      }
    }));
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotSearchAndGotoBlock searchAI) {
      if (ai.success()) {
        blockFound = searchAI.getBlockFound();
      } else {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    }
  }

  @Override
  public void end() {
    releaseBlockFound(true);
  }

  protected BlockPos blockFound() {
    return blockFound;
  }

  protected void releaseBlockFound(boolean success) {
    if (blockFound != null) {
      // TODO: if !ai.success() -> can't break block, blacklist it
      robot.getRegistry().release(new ResourceIdBlock(blockFound));
      blockFound = null;
    }
  }

  public final void updateFilter() {
    blockFilter.clear();

    if (robot.getLinkedStation() == null) {
      return;
    }

    for (StatementSlot slot : robot.getLinkedStation().getActiveActions()) {
      if (slot.statement instanceof ActionRobotFilter) {
        for (IStatementParameter p : slot.parameters) {
          if (p instanceof StatementParameterItemStack param) {
            ItemStack stack = param.getItemStack();

            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
              blockFilter.add(((BlockItem) stack.getItem()).getBlock());
            }
          }
        }
      }
    }
  }

  protected boolean matchesGateFilter(Level world, BlockPos pos) {
    if (blockFilter.isEmpty()) {
      return true;
    }

    Block block = world.getBlockState(pos).getBlock();

    for (Block value : blockFilter) {
      if (value == block) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    if (blockFound != null) {
      nbt.putLong("indexStored", blockFound.asLong());
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("indexStored")) {
      blockFound = BlockPos.of(nbt.getLong("indexStored"));
    }
  }

}