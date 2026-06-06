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
    int remaining = item.getBoostedBlocksRemaining();
    if (remaining > 0) {
      item.setBoostedBlocksRemaining(remaining - 1);
    }
    SpeedHelper.readjustSpeed(item, pipe.getPipeMaterial().getItemSpeed());
  }
}
