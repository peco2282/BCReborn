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
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import com.peco2282.bcreborn.factory.block.entity.TankBlockEntity;
import com.peco2282.bcreborn.energy.fluids.TankUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TankBlock extends BuildCraftBlock {
  public static final BooleanProperty IS_STACKED = BooleanProperty.create("is_stacked");

  public TankBlock() {
    super(Properties.of()
      .mapColor(MapColor.NONE)
      .sound(SoundType.GLASS)
      .strength(0.5F)
      .noOcclusion());
    this.registerDefaultState(this.getStateDefinition().any().setValue(IS_STACKED, false));
  }

  @Override
  public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
    return box(2, 0, 2, 14, 16, 14);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TankBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, FactoryBlockEntityTypes.TANK.get(), BuildCraftBlockEntity.ticker());
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(IS_STACKED);
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof TankBlockEntity tank) {
      if (TankUtils.handleRightClick(tank, hit.getDirection(), player, true, true)) {
        return InteractionResult.CONSUME;
      }
    }
    return super.use(state, level, pos, player, hand, hit);
  }
}
