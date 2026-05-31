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
package com.peco2282.bcreborn.transport.pipe.behaviour;

import com.peco2282.bcreborn.core.ItemsCore;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface PipeBlockEvent {
  default InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                        Player player, InteractionHand hand, BlockHitResult hit) {
    if (player.getItemInHand(hand).is(ItemsCore.WRENCH.get())) {
      return pipe.getBehaviour().onUse(pipe, level, pos, player, hand, hit);
    }
    return InteractionResult.PASS;
  }

  default void updateShape(PipeBlockEntity entity, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos neighborPos) {
  }

  default void onPlace(PipeBlockEntity entity, Level level, BlockState oldState, boolean isMoving) {
  }
}
