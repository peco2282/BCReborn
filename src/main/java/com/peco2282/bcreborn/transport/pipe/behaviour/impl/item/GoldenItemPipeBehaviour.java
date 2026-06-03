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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 金アイテムパイプの振る舞い（高速輸送）
 * 通常パイプより常に高速でアイテムを移動させる。
 */
public class GoldenItemPipeBehaviour implements ItemPipeBehaviour {

  public static final GoldenItemPipeBehaviour INSTANCE = new GoldenItemPipeBehaviour();
  // 金パイプ通過後の加速距離（ブロック数）。丸石/焼石パイプが減衰に使用する。
  public static final int BOOST_DISTANCE = 32;
  private static final float MAX_SPEED = 0.6f;

  private GoldenItemPipeBehaviour() {
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    if (!pipe.getLevel().hasNeighborSignal(pipe.getBlockPos())) return;
    float speed = item.getSpeed();
    if (speed < 0.3f) {
      speed *= 2.0f;
    } else {
      speed += 0.075f;
    }
    item.setSpeed(Math.min(speed, MAX_SPEED));
    // 加速後の残り距離カウンタをセット（丸石/焼石パイプが減衰に使用）
    item.setBoostedBlocksRemaining(BOOST_DISTANCE);
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    return true;
  }
}
