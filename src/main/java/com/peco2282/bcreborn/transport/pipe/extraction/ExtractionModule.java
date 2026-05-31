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
package com.peco2282.bcreborn.transport.pipe.extraction;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;

/**
 * パイプの抽出ロジックを定義するインターフェース。
 * 新しい抽出方式を追加する際はこのインターフェースを実装し、
 * PipeBehaviourから呼び出すことでPipeBlockEntityの編集を不要にする。
 */
public interface ExtractionModule {
  /**
   * 毎tickの抽出処理を行う。
   *
   * @param pipe 対象のパイプBlockEntity
   */
  void extract(PipeBlockEntity pipe);
}
