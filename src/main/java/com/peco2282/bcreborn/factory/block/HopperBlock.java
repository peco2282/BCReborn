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

import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.factory.block.entity.HopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class HopperBlock extends BuildCraftBlock {
  public HopperBlock() {
    super(Properties.of()
      .mapColor(MapColor.METAL)
      .sound(SoundType.METAL)
      .strength(3.0F)
      .noOcclusion());
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new HopperBlockEntity(pos, state);
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
    if (!level.isClientSide) {
      BlockEntity entity = level.getBlockEntity(pos);
      if (entity instanceof HopperBlockEntity provider) {
        NetworkHooks.openScreen((ServerPlayer) player, provider, pos);
      }
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public RenderShape getRenderShape(BlockState p_49232_) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }
}
