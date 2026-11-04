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
package com.peco2282.bcreborn.builders.block;

import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.BuildersConfig;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class QuarryBlock extends BuildCraftBlock {
  public QuarryBlock() {
    super(Properties.of().noOcclusion().lightLevel(state -> 1));
    this.registerDefaultState(this.getStateDefinition().any().setValue(HORIZONTAL_FACING, Direction.NORTH));
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, BuildersBlockEntityTypes.QUARRY.get(), BuildCraftBlockEntity.ticker());
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    p_49915_.add(HORIZONTAL_FACING);
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public boolean isHorizontalRotatable() {
    return true;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new QuarryBlockEntity(pos, state);
  }

  /**
   * Original {@code onBlockPlacedBy()}: remembers the player for the "area too small" message.
   */
  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    if (!level.isClientSide && placer instanceof Player player && level.getBlockEntity(pos) instanceof QuarryBlockEntity quarry) {
      quarry.setPlacedBy(player);
    }
  }

  /**
   * Original {@code onBlockActivated()}: a wrench restarts the quarry (rebuilds the frame).
   */
  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    if (player.getItemInHand(hand).getItem() instanceof IToolWrench wrench && wrench.canWrench(player, pos)) {
      if (!level.isClientSide && level.getBlockEntity(pos) instanceof QuarryBlockEntity quarry) {
        quarry.reinitialize();
        wrench.wrenchUsed(player, pos);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return super.use(state, level, pos, player, hand, hit);
  }

  /**
   * Original {@code breakBlock()}: breaking the quarry also removes its connected frame.
   */
  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock()) && !level.isClientSide) {
      if (level.getBlockEntity(pos) instanceof QuarryBlockEntity quarry) {
        quarry.destroy();
      }
      FrameBlock.removeNeighboringFrames(level, pos);
    }
    super.onRemove(state, level, pos, newState, isMoving);
  }

  /**
   * Original {@code getDrops()}: nothing drops when {@code quarryOneTimeUse} is enabled.
   */
  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    if (BuildersConfig.isQuarryOneTimeUse()) {
      return new ArrayList<>();
    }
    return super.getDrops(state, builder);
  }
}
