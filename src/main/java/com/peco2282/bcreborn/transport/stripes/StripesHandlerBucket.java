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
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class StripesHandlerBucket implements IStripesHandler {
  private static final ItemStack emptyBucket = new ItemStack(Items.BUCKET, 1);

  @Override
  public StripesHandlerType getType() {
    return StripesHandlerType.ITEM_USE;
  }

  @Override
  public boolean shouldHandle(ItemStack stack) {
    return stack.getItem() instanceof BucketItem;
  }

  @Override
  public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player,
                        IStripesActivator activator) {
    if (world.isEmptyBlock(pos)) {
      BucketItem bucket = (BucketItem) stack.getItem();
      if (bucket.emptyContents(player, world, pos, null)) {
        activator.sendItem(emptyBucket.copy(), direction.getOpposite());
        stack.shrink(1);
        if (!stack.isEmpty()) {
          activator.sendItem(stack, direction.getOpposite());
        }

        return true;
      }
    }

    if (FluidUtil.getFluidContained(stack).isPresent()) {
      activator.sendItem(stack, direction.getOpposite());
      return true;
    }

    BlockPos targetPos = pos;
    FluidStack fluidStack = BlockUtils.drainBlock(world, targetPos, true);

    if (fluidStack.isEmpty()) {
      targetPos = pos.below();
      fluidStack = BlockUtils.drainBlock(world, targetPos, true);
    }

    if (fluidStack.isEmpty()) {
      activator.sendItem(stack, direction.getOpposite());
      return true;
    }

    ItemStack filledBucket = FluidUtil.getFilledBucket(fluidStack);

    if (filledBucket.isEmpty()) {
      activator.sendItem(stack, direction.getOpposite());
      return true;
    }

    activator.sendItem(filledBucket, direction.getOpposite());
    stack.shrink(1);
    if (!stack.isEmpty()) {
      activator.sendItem(stack, direction.getOpposite());
    }

    return true;
  }

}
