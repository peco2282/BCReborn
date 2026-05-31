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
import net.minecraft.world.item.ItemStack;

public class ClayItemPipeBehaviour implements ItemPipeBehaviour {

  public static final ClayItemPipeBehaviour INSTANCE = new ClayItemPipeBehaviour();

  private ClayItemPipeBehaviour() {
  }

  @Override
  public boolean canAcceptItem(PipeBlockEntity pipe, ItemStack stack, Direction from) {
    return ItemPipeBehaviour.super.canAcceptItem(pipe, stack, from);
  }

  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    // 粘土パイプは接続するインベントリがある場合、それを優先する
    for (Direction dir : Direction.values()) {
      if (dir == item.getEntryDirection()) continue; // 来た方向には戻らない
      var be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be != null && !(be instanceof PipeBlockEntity)) {
        return dir;
      }
    }
    return null; // なければデフォルトルーティング
  }
}
