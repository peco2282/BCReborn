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

/**
 * エネルギーパイプ固有の振る舞いを定義するインターフェース。
 * エネルギー転送ロジックは EnergyTransportModule が一元管理する。
 * Behaviour はエネルギー吸い出し（Wood）や通過フック（onEnergyPassed）のみ担当する。
 */
public interface EnergyPipeBehaviour extends PipeBehaviour {
  /**
   * エネルギーがパイプを通過する時の処理（フック）。
   * デフォルトは何もしない。
   */
  default void onEnergyPassed(PipeBlockEntity pipe, int amount) {
  }

  /**
   * エネルギー源からエネルギーを吸い出す処理。
   * Wood パイプのみオーバーライドする。それ以外は何もしない。
   * 吸い出したエネルギーは EnergyTransportModule.receiveEnergy() で注入すること。
   */
  default void extractEnergy(PipeBlockEntity pipe) {
  }
}
