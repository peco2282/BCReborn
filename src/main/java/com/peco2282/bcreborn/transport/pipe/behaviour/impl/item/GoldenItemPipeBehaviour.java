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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.transport.SpeedHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 金アイテムパイプの振る舞い（高速輸送）
 * 通常パイプより常に高速でアイテムを移動させる。
 */
public class GoldenItemPipeBehaviour implements ItemPipeBehaviour {

  public static final GoldenItemPipeBehaviour INSTANCE = new GoldenItemPipeBehaviour();
  private GoldenItemPipeBehaviour() {
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    SpeedHelper.boost(item);
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    return true;
  }
}
