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
package com.peco2282.bcreborn.transport.pipe.transport;

import com.peco2282.bcreborn.transport.pipe.TravelingItem;

/**
 * アイテムの移動速度を調整する責務を担うヘルパー。
 * <p>
 * 状態を持たない static utility クラス。
 * speed calculation 系へ分離するための退避クラス。
 */
public final class SpeedHelper {

  private static final float MIN_SPEED = 0.01f;
  private static final float MAX_SPEED = 0.15f;
  public static final float SLOWDOWN = 0.01f;

  private SpeedHelper() {
  }

  /**
   * TravelingItem の速度を規定範囲内に収まるよう再調整する。
   * <p>
   * 基本的な減衰ロジックを提供。
   */
  public static void readjustSpeed(TravelingItem item, float slowdown) {
    item.setSpeed(Math.max(MIN_SPEED, Math.min(MAX_SPEED, item.getSpeed()) - slowdown));
  }

  public static void boost(TravelingItem item) {
    item.setSpeed(Math.max(MIN_SPEED * 4, Math.min(MAX_SPEED, item.getSpeed() * 4)));
  }
}