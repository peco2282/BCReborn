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
package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.api.RegistryUtil;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.recipes.IProgrammingRecipe;
import com.peco2282.bcreborn.robotics.item.RedstoneBoardItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class BoardProgrammingRecipe implements IProgrammingRecipe {
  @Override
  public String getId() {
    return "buildcraft:redstone_board";
  }

  @Override
  public List<ItemStack> getOptions(int width, int height) {
    List<ItemStack> options = new ArrayList<>(width * height);
    for (RedstoneBoardNBT<?> nbt : RegistryUtil.getRedstoneBoardsList()) {
      ItemStack stack = new ItemStack(RoboticsItems.REDSTONE_BOARDS.get().get());
      nbt.createBoard(stack.getOrCreateTag());
      options.add(stack);
    }
    options.sort(new BoardSorter(this));
    return options;
  }

  @Override
  public int getEnergyCost(ItemStack option) {
    return RegistryUtil.getRedstoneBoard(option.getOrCreateTag()).getEnergyCost();
  }

  @Override
  public boolean canCraft(ItemStack input) {
    return input.getItem() instanceof RedstoneBoardItem;
  }

  @Override
  public ItemStack craft(ItemStack input, ItemStack option) {
    return option.copy();
  }

  private record BoardSorter(BoardProgrammingRecipe recipe) implements Comparator<ItemStack> {

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
      int i = (recipe.getEnergyCost(o1) - recipe.getEnergyCost(o2)) * 200;
      return i != 0 ? i : RedstoneBoardItem.getBoardNBT(o1).getID().compareTo(RedstoneBoardItem.getBoardNBT(o2).getID());
    }
  }
}
