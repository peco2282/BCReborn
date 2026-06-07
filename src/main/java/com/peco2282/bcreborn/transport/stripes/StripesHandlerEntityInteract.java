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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StripesHandlerEntityInteract implements IStripesHandler {

  @Override
  public StripesHandlerType getType() {
    return StripesHandlerType.ITEM_USE;
  }

  @Override
  public boolean shouldHandle(ItemStack stack) {
    return true;
  }

  @Override
  public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player,
                        IStripesActivator activator) {

    AABB box = new AABB(pos);
    List<LivingEntity> livingEntities = world.getEntitiesOfClass(LivingEntity.class, box);
    if (livingEntities.isEmpty()) {
      return false;
    }

    player.setItemInHand(InteractionHand.MAIN_HAND, stack);

    boolean successful = false;
    Collections.shuffle(livingEntities);
    List<LivingEntity> targets = new LinkedList<>(livingEntities);
    while (!targets.isEmpty()) {
      LivingEntity entity = targets.remove(0);

      if (player.interactOn(entity, InteractionHand.MAIN_HAND).consumesAction()) {
        successful = true;
        dropItemsExcept(stack, player, activator, direction);
      }
    }
    if (!stack.isEmpty() && successful) {
      activator.sendItem(stack, direction.getOpposite());
    }

    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

    return successful;
  }

  private void dropItemsExcept(ItemStack stack, Player player, IStripesActivator activator, Direction direction) {
    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
      ItemStack invStack = player.getInventory().getItem(i);
      if (!invStack.isEmpty() && invStack != stack) {
        player.getInventory().setItem(i, ItemStack.EMPTY);
        activator.sendItem(invStack, direction.getOpposite());
      }
    }
  }

}
