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

/**
 * アイテム転送の結果を表すenum。
 * {@link MovementHelper#moveItemToNext} の戻り値として使用する。
 */
public enum MovementResult {
  /**
   * 次のパイプ/インベントリへ転送成功
   */
  SUCCESS,
  /**
   * 接続先なし・方向未決定
   */
  NO_TARGET,
  /**
   * インベントリが満杯で受け取れない
   */
  BLOCKED
  // TODO:
  // JAMMED は将来的な congestion system 実装時に追加予定。
  // 現時点では BLOCKED に統合して扱う。
}
