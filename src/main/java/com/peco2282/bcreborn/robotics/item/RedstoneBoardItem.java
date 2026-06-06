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
package com.peco2282.bcreborn.robotics.item;

import com.peco2282.bcreborn.api.RegistryUtil;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.boards.RedstoneBoardRobotEmptyNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedstoneBoardItem extends BuildCraftItem {
  public RedstoneBoardItem() {
    super(new Properties());
  }

  public static ItemStack createStack(RedstoneBoardNBT<?> boardNBT) {
    ItemStack stack = new ItemStack(RoboticsItems.REDSTONE_BOARDS.get().get());
    CompoundTag nbtData = stack.getOrCreateTag();
    boardNBT.createBoard(nbtData);
    return stack;
  }

  public static RedstoneBoardNBT<?> getBoardNBT(ItemStack stack) {
    return getBoardNBT(getNBT(stack));
  }

  private static CompoundTag getNBT(ItemStack stack) {
    CompoundTag cpt = stack.getOrCreateTag();
    if (!cpt.contains("id")) {
      RoboticsRedstoneRobots.EMPTY.get().createBoard(cpt);
    }
    return cpt;
  }

  private static RedstoneBoardNBT<?> getBoardNBT(CompoundTag cpt) {
    return RegistryUtil.getRedstoneBoard(cpt);
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return !(getBoardNBT(stack) instanceof RedstoneBoardRobotEmptyNBT) ? 1 : 16;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    RedstoneBoardNBT<?> board = getBoardNBT(stack);
    board.addInformation(stack, level, tooltip, flag);
  }
}
