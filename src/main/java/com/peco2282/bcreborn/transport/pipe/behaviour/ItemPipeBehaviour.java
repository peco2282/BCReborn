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

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * アイテムパイプ固有の振る舞いを定義するインターフェース
 */
public interface ItemPipeBehaviour extends PipeBehaviour {
  /**
   * アイテムがパイプに注入された時の処理
   */
  default void onInjectItem(PipeBlockEntity pipe, ItemStack stack, Direction from, float speed) {
  }

  /**
   * アイテムがパイプ中央に到達した時の処理（progress >= 0.5）。
   * Voidパイプ等でアイテムを中央で消滅させる場合に使用する。
   * stack.setCount(0) でアイテムを消滅させると ItemTransportModule がリストから除去する。
   */
  default void onReachedCenter(PipeBlockEntity pipe, TravelingItem item) {
  }

  /**
   * アイテムの移動速度を調整する
   */
  default void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    item.setSpeed(pipe.getPipeMaterial().getItemSpeed());
  }

  /**
   * 次の移動方向を選択する（nullを返すとデフォルトのルーティングを使用）
   */
  @Nullable
  default Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    return null;
  }

  /**
   * アイテムをパイプが受け入れられるかどうか
   */
  default boolean canAcceptItem(PipeBlockEntity pipe, ItemStack stack, Direction from) {
    return true;
  }
}
