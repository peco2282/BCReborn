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
package com.peco2282.bcreborn.robotics.recipe;

import com.peco2282.bcreborn.core.recipes.IntegrationRecipeBC;
import com.peco2282.bcreborn.silicon.block.entity.IntegrationTableBlockEntity;
import org.jetbrains.annotations.Nullable;
import com.peco2282.bcreborn.api.RegistryUtil;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.item.RedstoneBoardItem;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;


/** Installs a programmed redstone board in a robot (original {@code RobotIntegrationRecipe}). */
public class RobotIntegrationRecipe extends IntegrationRecipeBC {
  public RobotIntegrationRecipe() {
    super(IntegrationTableBlockEntity.ROBOT_INTEGRATION_ENERGY, 1);
  }


  @Override
  public List<ItemStack> generateExampleInput() {
    ArrayList<ItemStack> example = new ArrayList<>();
    example.add(RobotItem.createRobotStack(RoboticsRedstoneRobots.EMPTY.get(), 0));
    return example;
  }

  @Override
  public List<List<ItemStack>> generateExampleExpansions() {
    ArrayList<List<ItemStack>> list = new ArrayList<>();
    ArrayList<ItemStack> example = new ArrayList<>();
    for (RedstoneBoardNBT<?> nbt : RegistryUtil.getRedstoneBoardsList()) {
      ItemStack stack = new ItemStack(RoboticsItems.REDSTONE_BOARDS.get().get());
      nbt.createBoard(stack.getOrCreateTag());
      example.add(stack);
    }
    list.add(example);
    return list;
  }

  @Override
  public List<ItemStack> generateExampleOutput() {
    ArrayList<ItemStack> example = new ArrayList<>();
    for (RedstoneBoardNBT<?> nbt : RegistryUtil.getRedstoneBoardsList()) {
      example.add(RobotItem.createRobotStack((RedstoneBoardRobotNBT) nbt, 0));
    }
    return example;
  }

  @Override
  public boolean isValidInput(ItemStack input) {
    return input.getItem() instanceof RobotItem;
  }

  @Override
  public boolean isValidExpansion(ItemStack input, ItemStack expansion) {
    return expansion.getItem() instanceof RedstoneBoardItem;
  }

  @Override
  public @Nullable ItemStack craft(ItemStack input, List<ItemStack> expansions, boolean preview) {
    // Original: the board is the first expansion. Other items in the expansion slots are ignored.
    ItemStack board = null;
    for (ItemStack expansion : expansions) {
      if (expansion.getItem() instanceof RedstoneBoardItem
        && RedstoneBoardItem.getBoardNBT(expansion) instanceof RedstoneBoardRobotNBT) {
        board = expansion;
        break;
      }
    }
    if (board == null) {
      return null;
    }

    RedstoneBoardRobotNBT boardNBT = (RedstoneBoardRobotNBT) RedstoneBoardItem.getBoardNBT(board);
    if (!preview) {
      board.shrink(1);
    }
    int energy = RobotItem.getEnergy(input);
    if (energy == 0) {
      energy = RobotEntityBase.SAFETY_ENERGY;
    }
    return RobotItem.createRobotStack(boardNBT, energy);
  }
}
