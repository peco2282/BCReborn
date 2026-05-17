package com.peco2282.bcreborn.transport.pipe.transport;

import com.peco2282.bcreborn.transport.pipe.TravelingItem;

/**
 * アイテムの移動速度を調整する責務を担うヘルパー。
 * <p>
 * 状態を持たない static utility クラス。
 * speed calculation 系へ分離するための退避クラス。
 */
final class SpeedHelper {

  private static final float MIN_SPEED = 0.01f;
  private static final float MAX_SPEED = 0.15f;
  private static final float SLOWDOWN = 0.001f;

  private SpeedHelper() {
  }

  /**
   * TravelingItem の速度を規定範囲内に収まるよう再調整する。
   * <p>
   * 将来パイプ素材ごとに MIN/MAX を変えたい場合はここを拡張する。
   */
  static void readjustSpeed(TravelingItem item) {
    float speed = item.getSpeed();
    speed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, speed - SLOWDOWN));
    item.setSpeed(speed);
  }
}
