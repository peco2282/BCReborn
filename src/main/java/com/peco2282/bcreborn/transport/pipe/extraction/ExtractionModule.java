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
