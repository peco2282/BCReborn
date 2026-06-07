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
package com.peco2282.bcreborn.transport.stripes;

import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class StripesHandlerRightClick implements IStripesHandler {
  public static final List<Item> items = new ArrayList<>();

  @Override
  public StripesHandlerType getType() {
    return StripesHandlerType.ITEM_USE;
  }

  @Override
  public boolean shouldHandle(ItemStack stack) {
    return stack.getItem() instanceof ThrowablePotionItem || items.contains(stack.getItem());
  }

  @Override
  public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
    ItemStack remainingStack = stack.use(world, player, InteractionHand.MAIN_HAND).getObject();
    activator.sendItem(remainingStack, direction.getOpposite());
    return true;
  }

}
