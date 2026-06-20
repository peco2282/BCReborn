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
package com.peco2282.bcreborn.core.item;

import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.api.blocks.IRotatable;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends BuildCraftItem implements IToolWrench {
  public WrenchItem(Item.Properties properties) {
    super(properties);
  }

  @Override
  public boolean canWrench(Player player, BlockPos pos) {
    return true;
  }

  @Override
  public InteractionResult useOn(UseOnContext p_41427_) {
    BlockState state = p_41427_.getLevel().getBlockState(p_41427_.getHitResult().getBlockPos());
    if (state.getBlock() instanceof IRotatable rotatable && (rotatable.isHorizontalRotatable() || rotatable.isRotatable())) {
      var prop = rotatable.getDirectionProperty();
      if (prop != null) {
        p_41427_.getLevel().setBlock(p_41427_.getHitResult().getBlockPos(), state.cycle(prop), 3);
      }
      return InteractionResult.sidedSuccess(p_41427_.getLevel().isClientSide);
    }
    return super.useOn(p_41427_);
  }

  @Override
  public void wrenchUsed(Player player, BlockPos pos) {
  }
}
