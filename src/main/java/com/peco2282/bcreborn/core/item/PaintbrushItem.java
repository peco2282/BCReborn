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


import com.google.common.collect.ImmutableMap;
import com.peco2282.bcreborn.api.blocks.IColoredBlock;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PaintbrushItem extends BuildCraftItem {
  private @Nullable final DyeColor color;
  public static final Map<DyeColor, Item> COLORED = ImmutableMap.<DyeColor, Item>builder()
    .put(DyeColor.WHITE, Items.WHITE_DYE)
    .put(DyeColor.ORANGE, Items.ORANGE_DYE)
    .put(DyeColor.MAGENTA, Items.MAGENTA_DYE)
    .put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_DYE)
    .put(DyeColor.YELLOW, Items.YELLOW_DYE)
    .put(DyeColor.LIME, Items.LIME_DYE)
    .put(DyeColor.PINK, Items.PINK_DYE)
    .put(DyeColor.GRAY, Items.GRAY_DYE)
    .put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_DYE)
    .put(DyeColor.CYAN, Items.CYAN_DYE)
    .put(DyeColor.PURPLE, Items.PURPLE_DYE)
    .put(DyeColor.BLUE, Items.BLUE_DYE)
    .put(DyeColor.BROWN, Items.BROWN_DYE)
    .put(DyeColor.GREEN, Items.GREEN_DYE)
    .put(DyeColor.RED, Items.RED_DYE)
    .put(DyeColor.BLACK, Items.BLACK_DYE)
    .build();

  public PaintbrushItem(@Nullable DyeColor color) {
    super(new Properties().stacksTo(1).durability(63));
    this.color = color;
  }

  public @Nullable DyeColor getColor() {
    return color;
  }

  public @Nullable Item getColoredItem() {
    return color == null ? null : COLORED.get(color);
  }

  @Override
  public Component getName(ItemStack stack) {
    if (color == null) return super.getName(stack);
    return ((MutableComponent)super.getName(stack)).append(" (" + color.getSerializedName() + ")");
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    if (color == null) {
      tooltip.add(Component.translatable("item.bcreborncore.paintbrush.no_color"));
      return;
    }
    tooltip.add(Component.translatable("item.bcreborncore.paintbrush." + color.getSerializedName()));
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

    if (block instanceof IColoredBlock colored) {
      if (color == null) {
        return InteractionResult.PASS;
      }

      if (colored.recolorBlock(state, level, pos, side, color)) {
        if (!level.isClientSide) {
          stack.hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));
        }

        if (context.getPlayer() != null) {
          context.getPlayer().swing(context.getHand());
        }

      }
      return InteractionResult.sidedSuccess(level.isClientSide);
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