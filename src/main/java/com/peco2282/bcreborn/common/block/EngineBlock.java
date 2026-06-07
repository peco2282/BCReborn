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
package com.peco2282.bcreborn.common.block;

import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class EngineBlock extends BuildCraftBlock {
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
  public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());

  public static final VoxelShape SHAPE_UP =
    Shapes.or(box(0, 0, 0, 16, 4, 16), box(4, 4, 4, 12, 16, 12));
  public static final VoxelShape SHAPE_DOWN =
    Shapes.or(box(0, 12, 0, 16, 16, 16), box(4, 0, 4, 12, 12, 12));
  public static final VoxelShape SHAPE_SOUTH =
    Shapes.or(box(0, 0, 0, 16, 16, 4), box(4, 4, 4, 12, 12, 16));
  public static final VoxelShape SHAPE_NORTH =
    Shapes.or(box(4, 4, 0, 12, 12, 12), box(0, 0, 12, 16, 16, 16));
  public static final VoxelShape SHAPE_EAST =
    Shapes.or(box(0, 0, 0, 4, 16, 16), box(4, 4, 4, 16, 12, 12));
  public static final VoxelShape SHAPE_WEST =
    Shapes.or(box(0, 4, 4, 12, 12, 12), box(12, 0, 0, 16, 16, 16));

  public EngineBlock(Properties p_49795_) {
    super(p_49795_);
    this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false).setValue(FACING, Direction.UP));
  }

  public EngineBlock() {
    this(Properties.of().noOcclusion());
  }

  @Override
  public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
    Direction direction = p_60555_.getValue(FACING);
    return switch (direction) {
      case DOWN -> SHAPE_DOWN;
      case UP -> SHAPE_UP;
      case NORTH -> SHAPE_NORTH;
      case SOUTH -> SHAPE_SOUTH;
      case WEST -> SHAPE_WEST;
      case EAST -> SHAPE_EAST;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState p_49232_) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction direction = context.getClickedFace();
    if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
      direction = direction.getOpposite();
    }
    return this.defaultBlockState().setValue(FACING, direction);
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack stack = player.getItemInHand(hand);
    if (stack.getItem() instanceof IToolWrench wrench) {
      if (wrench.canWrench(player, pos)) {
        Direction current = state.getValue(FACING);
        Direction next = Direction.values()[(current.ordinal() + 1) % Direction.values().length];
        level.setBlock(pos, state.setValue(FACING, next), 3);
        wrench.wrenchUsed(player, pos);
        return InteractionResult.SUCCESS;
      }
    }
    return super.use(state, level, pos, player, hand, hit);
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    p_49915_.add(ACTIVE, FACING);
  }

  @Override
  public boolean isRotatable() {
    return true;
  }

  @Override
  public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
    return Shapes.block();
  }
}
