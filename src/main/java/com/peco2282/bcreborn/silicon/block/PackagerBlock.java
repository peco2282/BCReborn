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
package com.peco2282.bcreborn.silicon.block;

import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.block.entity.PackagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class PackagerBlock extends BuildCraftBlock {
  public PackagerBlock() {
    super(Properties.of().strength(10F));
    this.registerDefaultState(this.getStateDefinition().any().setValue(HORIZONTAL_FACING, Direction.NORTH));
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    BlockEntity tile = level.getBlockEntity(pos);
    if (tile instanceof PackagerBlockEntity packager) {
      NetworkHooks.openScreen((ServerPlayer) player, packager, pos);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new PackagerBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, SiliconBlockEntityTypes.PACKAGER.get(), PackagerBlockEntity.ticker());
  }

  @Override
  public boolean isHorizontalRotatable() {
    return true;
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING);
  }
}
