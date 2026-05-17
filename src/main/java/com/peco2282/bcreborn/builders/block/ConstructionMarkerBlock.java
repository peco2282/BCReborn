package com.peco2282.bcreborn.builders.block;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.entity.ConstructionMarkerBlockEntity;
import com.peco2282.bcreborn.common.block.MarkerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ConstructionMarkerBlock extends MarkerBlock {
  public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());

  public ConstructionMarkerBlock() {
    this(Properties.of().noOcclusion().lightLevel(state -> 1));
  }

  public ConstructionMarkerBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public boolean isRotatable() {
    return true;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return BlockEntityTypesBuilders.CONSTRUCTION_MARKER.get().create(pos, state);
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

    // スニーク + 空手 or スニーク + レンチ: Blueprintを取り出す
    if (player.isShiftKeyDown() && heldItem.isEmpty()) {
      if (marker.hasBlueprint()) {
        ItemStack blueprint = marker.removeBlueprint();
        player.getInventory().add(blueprint);
        return InteractionResult.SUCCESS;
      }
      return InteractionResult.PASS;
    }

    // 手にアイテムを持っている場合: Blueprintとして挿入
    if (!heldItem.isEmpty() && !marker.hasBlueprint()) {
      ItemStack toInsert = heldItem.copy();
      toInsert.setCount(1);
      marker.setBlueprint(toInsert);
      if (!player.isCreative()) {
        heldItem.shrink(1);
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
