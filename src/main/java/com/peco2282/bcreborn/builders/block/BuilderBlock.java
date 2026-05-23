package com.peco2282.bcreborn.builders.block;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuilderBlock extends BuildCraftBlock {
  public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());

  public BuilderBlock() {
    super(Properties.of().noOcclusion().lightLevel(state -> 1));
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
    return BlockEntityTypesBuilders.BUILDER.get().create(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, BlockEntityTypesBuilders.BUILDER.get(), BuildCraftBlockEntity.ticker());
  }

  @Override
  public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
    if (!level.isClientSide) {
      BlockEntity entity = level.getBlockEntity(pos);
      if (entity instanceof BuilderBlockEntity builder) {
        NetworkHooks.openScreen((ServerPlayer) player, builder, buf -> buf.writeBlockPos(pos));
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }
}
