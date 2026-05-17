package com.peco2282.bcreborn.builders.block;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlueprintLibraryBlock extends BuildCraftBlock {
  public BlueprintLibraryBlock() {
    super(Properties.of().noOcclusion().lightLevel(state -> 0).strength(5.0F));
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {

  }

  @Override
  public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
    if (p_60506_.isShiftKeyDown())
      return InteractionResult.PASS;
    BlockEntity entity = p_60504_.getBlockEntity(p_60505_);
    if (entity instanceof BlueprintLibraryBlockEntity library) {
      if (!p_60504_.isClientSide()) {
        p_60506_.openMenu( library);
      }
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return BlockEntityTypesBuilders.BLUEPRINT_LIBRARY.get().create(p_153215_, p_153216_);
  }
}
