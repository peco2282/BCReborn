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


import com.peco2282.bcreborn.api.transport.IItemPipe;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.factory.block.entity.AutoWorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class AutoWorkbenchBlock extends BuildCraftBlock {
  public AutoWorkbenchBlock() {
    super(Properties.of().sound(SoundType.WOOD).strength(3.0F));
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
    InteractionResult result = super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
    if (result.consumesAction()) {
      return result;
    }
    if (p_60506_.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }

    ItemStack held = p_60506_.getItemInHand(p_60507_);
    if (!held.isEmpty()) {
      Item heldItem = held.getItem();
      if (heldItem instanceof IItemPipe) {
        return InteractionResult.PASS;
      }
    }

    if (!p_60504_.isClientSide) {
      BlockEntity entity = p_60504_.getBlockEntity(p_60505_);
      if (entity instanceof AutoWorkbenchBlockEntity provider)
        NetworkHooks.openScreen((ServerPlayer) p_60506_, provider);
    }

    return InteractionResult.sidedSuccess(p_60504_.isClientSide);
  }

  @Override
  public @Nullable AutoWorkbenchBlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return new AutoWorkbenchBlockEntity(p_153215_, p_153216_);
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    p_49915_.add(FACING);
  }
}
