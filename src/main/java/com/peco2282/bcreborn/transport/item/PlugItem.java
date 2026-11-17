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
package com.peco2282.bcreborn.transport.item;

import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableItem;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.pipe.pluggable.PlugPluggable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Port of the original {@code ItemPlug}. Installing and removing is handled by {@code PipeBlock}. */
public class PlugItem extends Item implements IPipePluggableItem {

  public PlugItem() {
    super(new Item.Properties());
  }

  @Override
  public PipePluggable<?> createPipePluggable(IPipe pipe, Direction side, ItemStack stack) {
    return new PlugPluggable();
  }
}
