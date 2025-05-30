/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.builder.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.api.enums.EnumFillerType;
import peco2282.bcreborn.builder.block.entity.BCBuilderBlockEntityTypes;
import peco2282.bcreborn.builder.block.entity.FillerBlockEntity;
import peco2282.bcreborn.builder.block.menu.FillerMenu;
import peco2282.bcreborn.lib.block.BCBaseEntityBlock;
import peco2282.bcreborn.utils.PropertyBuilder;

public class FillerBlock extends BCBaseEntityBlock implements RotatableFacing {
  public FillerBlock(Properties properties, @NotNull String id) {
    super(
        properties,
        id,
        PropertyBuilder.builder()
            .add(BCProperties.BLOCK_FACING, Direction.NORTH)
            .add(BCProperties.FILLER_TYPE, EnumFillerType.NONE)
            .add(BCProperties.ACTIVE, false));
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FillerBlockEntity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<FillerBlock> codec() {
    return codecInstance(FillerBlock::new);
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BCProperties.FILLER_TYPE, BCProperties.BLOCK_FACING, BCProperties.ACTIVE);
  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(
      BlockEntityType<E> type) {
    return createTickerHelper(
        type, BCBuilderBlockEntityTypes.FILLER.get(), FillerBlockEntity::tick);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState p_60503_,
      Level p_60504_,
      BlockPos p_60505_,
      Player p_60506_,
      BlockHitResult p_60508_) {
    if (p_60504_.isClientSide()) {
      return super.useWithoutItem(p_60503_, p_60504_, p_60505_, p_60506_, p_60508_);
    } else {
      p_60506_.openMenu(getMenuProvider(p_60503_, p_60504_, p_60505_));
      return InteractionResult.CONSUME;
    }
  }

  @Override
  protected @Nullable MenuProvider getMenuProvider(
      BlockState p_60563_, Level p_60564_, BlockPos p_60565_) {
    return new MenuProvider() {
      @Override
      public @NotNull Component getDisplayName() {
        if (p_60563_.getBlock() instanceof FillerBlock filler)
          return Component.literal(filler.getId());
        return Component.empty();
      }

      @Override
      public @NotNull AbstractContainerMenu createMenu(
          int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new FillerMenu(p_39954_, p_39955_, null);
      }
    };
  }
}
