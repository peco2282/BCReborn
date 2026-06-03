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

/**
 * クォーツアイテムパイプの振る舞い。
 * 通常の輸送パイプとして機能し、石・丸石パイプとは接続できる。
 * 配管の分離・交差に使える。
 */
public class QuartzItemPipeBehaviour implements ItemPipeBehaviour {

  public static final QuartzItemPipeBehaviour INSTANCE = new QuartzItemPipeBehaviour();

  private QuartzItemPipeBehaviour() {
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    // originalのAdjustSpeed eventHandler: slowdownAmount /= 2 に相当
    float speed = item.getSpeed();
    float slowdown = (speed - 0.01f) * 0.01f; // 通常の速度低下量
    float adjustedSlowdown = slowdown / 2f;    // 1/2に抑制
    item.setSpeed(Math.max(0.01f, speed - adjustedSlowdown));
  }
}
