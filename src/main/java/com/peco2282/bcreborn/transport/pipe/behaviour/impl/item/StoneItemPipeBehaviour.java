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
import com.peco2282.bcreborn.transport.pipe.transport.SpeedHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 石アイテムパイプの振る舞い
 * 丸石の輸送パイプとは接続しない。
 */
public class StoneItemPipeBehaviour implements ItemPipeBehaviour {
  public static final StoneItemPipeBehaviour INSTANCE = new StoneItemPipeBehaviour();
  // 焼石パイプは金パイプ加速後32ブロックで元の速度に戻る
  private static final int STONE_DECAY_DISTANCE = 32;

  private StoneItemPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      // 丸石パイプとは接続しない
      return otherPipe.getPipeMaterial() != PipeMaterial.COBBLESTONE;
    }
    return true;
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    int remaining = item.getBoostedBlocksRemaining();
    if (remaining > 0) {
      item.setBoostedBlocksRemaining(remaining - 1);
      // 石パイプでは速度を維持する（readjustSpeedを呼ばない）
    } else {
      SpeedHelper.readjustSpeed(item, pipe.getPipeMaterial().getItemSpeed());
    }
  }
}
