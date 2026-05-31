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
package com.peco2282.bcreborn.energy.block;

import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.energy.BlockEntityTypesEnergy;
import com.peco2282.bcreborn.energy.block.entity.StoneEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class StoneEngineBlock extends EngineBlock {
  @Override
  public StoneEngineBlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return BlockEntityTypesEnergy.STONE_ENGINE.get().create(p_153215_, p_153216_);
  }

  @Override
  public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
    if (!p_60504_.isClientSide) {
      BlockEntity entity = p_60504_.getBlockEntity(p_60505_);
      if (entity instanceof StoneEngineBlockEntity engine) {
        p_60506_.openMenu(engine);
        return InteractionResult.SUCCESS;
      }
    }
    return super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
  }
}
