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
import com.peco2282.bcreborn.builders.block.entity.ConstructionMarkerBlockEntity;
import com.peco2282.bcreborn.builders.item.BlueprintItem;
import com.peco2282.bcreborn.builders.item.ConstructionMarkerBlockItem;
import com.peco2282.bcreborn.common.block.MarkerBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ConstructionMarkerBlock extends MarkerBlock {

  public ConstructionMarkerBlock() {
    this(Properties.of().noOcclusion().lightLevel(state -> 1));
  }

  public ConstructionMarkerBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any().setValue(MarkerBlock.FACING, Direction.NORTH));
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(MarkerBlock.FACING);
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return BuildersBlockEntityTypes.CONSTRUCTION_MARKER.get().create(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, BuildersBlockEntityTypes.CONSTRUCTION_MARKER.get(), BuildCraftBlockEntity.ticker());
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    if (placer != null) {
      Direction facing = Direction.getNearest(
        (float) placer.getLookAngle().x,
        (float) placer.getLookAngle().y,
        (float) placer.getLookAngle().z
      ).getOpposite();
      level.setBlock(pos, state.setValue(FACING, facing), 3);
    }
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    BlockEntity be = level.getBlockEntity(pos);
    if (!(be instanceof ConstructionMarkerBlockEntity marker)) {
      return InteractionResult.PASS;
    }

    ItemStack heldItem = player.getItemInHand(hand);

    if (heldItem.getItem() instanceof BlueprintItem) {
      if (marker.blueprint.isEmpty()) {
        ItemStack stack = player.getItemInHand(hand).copy();
        stack.setCount(1);
        marker.setBlueprint(stack);
        stack = ItemStack.EMPTY;
        if (player.getItemInHand(hand).getCount() > 1) {
          stack = player.getItemInHand(hand).copy();
          stack.setCount(player.getItemInHand(hand).getCount() - 1);
        }
        player.setItemInHand(hand, stack);
        return InteractionResult.SUCCESS;
      }
    } else if (heldItem.getItem() instanceof ConstructionMarkerBlockItem) {
      if (ConstructionMarkerBlockItem.linkStarted(player.getItemInHand(hand))) {
        ConstructionMarkerBlockItem.link(player.getItemInHand(hand), level, pos);
        return InteractionResult.SUCCESS;
      }
    } else if ((heldItem.isEmpty() || heldItem.getItem() instanceof IToolWrench) && player.isShiftKeyDown()) {
      return dropMarkerIfPresent(level, pos, false);
    }
    return InteractionResult.PASS;
  }

  private InteractionResult dropMarkerIfPresent(Level level, BlockPos pos, boolean onBreak) {
    ConstructionMarkerBlockEntity marker = (ConstructionMarkerBlockEntity) level.getBlockEntity(pos);
    if (marker != null && marker.blueprint != ItemStack.EMPTY && !level.isClientSide) {
      BlockUtils.dropItem((ServerLevel) level, pos, 6000, marker.blueprint);
      marker.blueprint = ItemStack.EMPTY;
      if (!onBreak) {
        marker.bluePrintBuilder = null;
        marker.bptContext = null;
        marker.setChanged();
      }
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof ConstructionMarkerBlockEntity marker && !level.isClientSide) {
        if (marker.hasBlueprint()) {
          Block.popResource(level, pos, marker.removeBlueprint());
        }
      }
      super.onRemove(state, level, pos, newState, isMoving);
    }
  }
}
