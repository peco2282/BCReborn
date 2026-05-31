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
package com.peco2282.bcreborn.factory.block;

import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FloodGateBlock extends BuildCraftBlock {
  public FloodGateBlock() {
    super(Properties.of()
      .mapColor(MapColor.METAL)
      .sound(SoundType.METAL)
      .strength(3.0F));
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return null;
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    InteractionResult result = super.use(state, level, pos, player, hand, hit);
    if (result.consumesAction()) {
      return result;
    }
    if (player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    ItemStack held = player.getItemInHand(hand);
    if (!held.isEmpty()) {
      Item equipped = held.getItem();
      if (equipped instanceof IToolWrench wrench && wrench.canWrench(player, pos)) {
        wrench.wrenchUsed(player, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, level, pos, block, fromPos, isMoving);
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
  }

  @Override
  public boolean isRotatable() {
    return false;
  }
}
