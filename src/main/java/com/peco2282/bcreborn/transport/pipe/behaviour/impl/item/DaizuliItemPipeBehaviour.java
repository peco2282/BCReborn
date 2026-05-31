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

/**
 * ダイゾリアイテムパイプの振る舞い。
 * ラピスパイプで色付けされたアイテムを特定方向へ振り分ける。
 * ラピスパイプと組み合わせて色ルーティングに使う。
 */
public class DaizuliItemPipeBehaviour implements ItemPipeBehaviour {

  public static final DaizuliItemPipeBehaviour INSTANCE = new DaizuliItemPipeBehaviour();

  private DaizuliItemPipeBehaviour() {
  }


  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    ItemStack stack = item.getStack();
    if (stack.isEmpty() || !stack.hasTag()) {
      return null;
    }

    var tag = stack.getTag();
    if (tag == null || !tag.contains("BCPipeColor")) {
      return null; // 色タグがなければデフォルトルーティング
    }

    int color = tag.getInt("BCPipeColor");

    // 色に対応する方向へ振り分ける（各面に色フィルターが設定されている想定）
    for (Direction dir : Direction.values()) {
      if (dir == item.getEntryDirection()) continue; // 来た方向には戻らない
      var filter = pipe.getFilter(dir);
      for (int i = 0; i < filter.getContainerSize(); i++) {
        ItemStack filterStack = filter.getItem(i);
        if (!filterStack.isEmpty() && filterStack.hasTag()) {
          var filterTag = filterStack.getTag();
          if (filterTag != null && filterTag.contains("BCPipeColor")
            && filterTag.getInt("BCPipeColor") == color) {
            return dir;
          }
        }
      }
    }

    return null; // 一致しない場合はデフォルトルーティング
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    float speed = item.getSpeed();
    float minSpeed = 0.01f;
    float maxSpeed = 0.15f;
    item.setSpeed(Math.max(minSpeed, Math.min(maxSpeed, speed - 0.001f)));
  }
}
