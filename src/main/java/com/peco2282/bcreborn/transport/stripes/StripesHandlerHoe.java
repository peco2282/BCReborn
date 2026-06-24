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
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class StripesHandlerHoe implements IStripesHandler {
  public static final StripesHandlerHoe INSTANCE = new StripesHandlerHoe();

  @Override
  public StripesHandlerType getType() {
    return StripesHandlerType.ITEM_USE;
  }

  @Override
  public boolean shouldHandle(ItemStack stack) {
    return stack.getItem() instanceof HoeItem;
  }

  @Override
  public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
    BlockPos target = pos.below();
    if (!world.isEmptyBlock(target)) {
      BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(target), Direction.UP, target, false);
      if (stack.useOn(new UseOnContext(world, player, InteractionHand.MAIN_HAND, stack, hitResult)).consumesAction()) {
        if (!stack.isEmpty()) {
          activator.sendItem(stack, direction.getOpposite());
        }
        return true;
      }
    }
    return false;
  }

}
