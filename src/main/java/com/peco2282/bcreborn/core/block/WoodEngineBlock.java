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
package com.peco2282.bcreborn.core.block;

import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.core.CoreBlockEntityTypes;
import com.peco2282.bcreborn.core.block.entity.WoodEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoodEngineBlock extends EngineBlock {
  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return CoreBlockEntityTypes.WOODEN_ENGINE.get().create(p_153215_, p_153216_);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
    return createTickerHelper(p_153214_, CoreBlockEntityTypes.WOODEN_ENGINE.get(), WoodEngineBlockEntity.ticker());
  }
}
