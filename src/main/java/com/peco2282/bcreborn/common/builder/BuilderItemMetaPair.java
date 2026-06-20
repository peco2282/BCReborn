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
package com.peco2282.bcreborn.common.builder;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BuilderItemMetaPair {
  public Item item;
  public int meta;
  public int position = 0;

  public BuilderItemMetaPair(ItemStack stack) {
    this.item = stack.getItem();
    this.meta = stack.getDamageValue();
  }

  public BuilderItemMetaPair(IBuilderContext context, BuildingSlotBlock block) {
    this(findStack(context, block));
  }

  private static ItemStack findStack(IBuilderContext context, BuildingSlotBlock block) {
    List<ItemStack> s = block.getRequirements(context);
    return !s.isEmpty() ? s.get(0) : null;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BuilderItemMetaPair imp) {
      return imp.item == item && imp.meta == meta;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Item.getId(item) * 17 + meta;
  }
}
