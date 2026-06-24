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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;

import java.util.List;

public class StripesHandlerShears implements IStripesHandler {
  public static final StripesHandlerShears INSTANCE = new StripesHandlerShears();

  @Override
  public StripesHandlerType getType() {
    return StripesHandlerType.ITEM_USE;
  }

  @Override
  public boolean shouldHandle(ItemStack stack) {
    return stack.getItem() instanceof ShearsItem;
  }

  @Override
  public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
    BlockState state = world.getBlockState(pos);
    Block block = state.getBlock();

    if (block instanceof IForgeShearable shearableBlock) {
      if (shearableBlock.isShearable(stack, world, pos)) {
        world.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1, 1);
        List<ItemStack> drops = shearableBlock.onSheared(player, stack, world, pos, 0);
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        if (stack.hurt(1, world.random, null)) {
          stack.shrink(1);
        }
        if (!stack.isEmpty()) {
          activator.sendItem(stack, direction.getOpposite());
        }
        for (ItemStack dropStack : drops) {
          activator.sendItem(dropStack, direction.getOpposite());
        }
        return true;
      }
    }

    return false;
  }

}
