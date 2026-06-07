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
package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.builder.AbstractBuilderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class QuarryBlockEntity extends AbstractBuilderBlockEntity {
  private final SimpleInventory inv = new SimpleInventory(0, "Quarry", 64);

  public QuarryBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.QUARRY.get(), pos, state);
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    // TODO: Implement quarry logic
  }

  public Container getInventory() {
    return inv;
  }

  @Override
  public List<ItemStack> getInventoryList() {
    return new ArrayList<>();
  }
}
