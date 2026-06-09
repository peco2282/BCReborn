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


import com.peco2282.bcreborn.api.blocks.IColorRemovable;
import com.peco2282.bcreborn.api.blocks.IColoredBlock;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class PaintbrushItem extends BuildCraftItem {
  private final DyeColor color;

  public PaintbrushItem() {
    super(new Properties().stacksTo(1).durability(63));
    this.color = DyeColor.WHITE;
  }

  public PaintbrushItem(DyeColor color) {
    super(new Properties().stacksTo(1).durability(63));
    this.color = color;
  }

  @Override
  public Component getName(ItemStack stack) {
    return ((MutableComponent)super.getName(stack)).append(" (" + color.getSerializedName() + ")");
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(Component.translatable("color.minecraft." + color.getSerializedName()));
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    ItemStack stack = context.getItemInHand();
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Direction side = context.getClickedFace();

//    int color = getColor(stack);
    BlockState state = level.getBlockState(pos);
    Block block = state.getBlock();

    DyeColor dyeColor = color;
    if (block instanceof IColoredBlock colored) {

      if (colored.recolorBlock(state, level, pos, side, dyeColor)) {
        if (!level.isClientSide) {
          stack.hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));
        }

        if (context.getPlayer() != null) {
          context.getPlayer().swing(context.getHand());
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }

    return InteractionResult.PASS;
  }

  private boolean tryRecolorBlock(Level level, BlockPos pos, BlockState state, DyeColor dyeColor) {
    // Custom recoloring logic for blocks that support color properties
    // This is a placeholder - implement actual recoloring based on your block types
    return false;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
    return true;
  }
}