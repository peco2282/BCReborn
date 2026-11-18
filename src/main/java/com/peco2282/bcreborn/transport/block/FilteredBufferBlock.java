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
package com.peco2282.bcreborn.transport.block;

import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.utils.ComparatorHelper;
import com.peco2282.bcreborn.transport.TransportBlockEntityTypes;
import com.peco2282.bcreborn.transport.block.entity.FilteredBufferBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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

/** Original {@code BlockFilteredBuffer}: opens its GUI unless the player sneaks or holds a pipe. */
public class FilteredBufferBlock extends BuildCraftBlock {
  public FilteredBufferBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FilteredBufferBlockEntity(pos, state);
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack held = player.getItemInHand(hand);
    if (player.isShiftKeyDown() || (held.getItem() instanceof BlockItem item && item.getBlock() instanceof PipeBlock)) {
      return InteractionResult.PASS;
    }
    if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof FilteredBufferBlockEntity buffer) {
      NetworkHooks.openScreen(serverPlayer, buffer, pos);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /** Original {@code IComparatorInventory}: the comparator counts the slots that have a filter. */
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    if (!(level.getBlockEntity(pos) instanceof FilteredBufferBlockEntity buffer)) return 0;
    return ComparatorHelper.fromContainer(buffer, slot -> !buffer.getFilters().getItem(slot).isEmpty());
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof FilteredBufferBlockEntity buffer) {
      Containers.dropContents(level, pos, buffer);
    }
    super.onRemove(state, level, pos, newState, isMoving);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, TransportBlockEntityTypes.FILTERED_BUFFER.get(), BuildCraftBlockEntity.ticker());
  }
}
