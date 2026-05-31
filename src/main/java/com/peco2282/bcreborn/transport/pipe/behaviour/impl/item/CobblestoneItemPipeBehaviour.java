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

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 丸石アイテムパイプの振る舞い
 * 石の輸送パイプとは接続しない。
 */
public class CobblestoneItemPipeBehaviour implements ItemPipeBehaviour {

  public static final CobblestoneItemPipeBehaviour INSTANCE = new CobblestoneItemPipeBehaviour();
  // 丸石パイプは金パイプ加速後16ブロックで元の速度に戻る
  private static final int COBBLESTONE_DECAY_DISTANCE = 16;

  private CobblestoneItemPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      // 石パイプとは接続しない
      return otherPipe.getPipeMaterial() != PipeMaterial.STONE;
    }
    return true;
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    float speed = item.getSpeed();
    float minSpeed = 0.01f;
    float maxSpeed = 0.15f;
    int remaining = item.getBoostedBlocksRemaining();
    if (remaining > 0) {
      // 加速後の残り距離に応じて減衰係数を調整（16ブロックで完全減衰）
      float decayRate = 0.001f * ((float) GoldenItemPipeBehaviour.BOOST_DISTANCE / COBBLESTONE_DECAY_DISTANCE);
      item.setSpeed(Math.max(minSpeed, Math.min(maxSpeed, speed - decayRate)));
      item.setBoostedBlocksRemaining(remaining - 1);
    } else {
      item.setSpeed(Math.max(minSpeed, Math.min(maxSpeed, speed - 0.001f)));
    }
  }
}
