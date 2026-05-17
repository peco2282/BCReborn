package com.peco2282.bcreborn.transport.pipe.debug;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import net.minecraft.core.Direction;
import org.slf4j.Logger;

/**
 * 開発用パイプルーティングデバッグロガー。
 * <p>
 * DEBUG_ROUTING = false の間は production logic に一切影響を与えない。
 * Diamond pipe / Iron pipe 実装時のルーティング検証に使用する。
 */
public final class PipeDebugLogger {

  public static final boolean DEBUG_ROUTING = false;

  private static final Logger LOGGER = BCReborn.createLogger();

  private PipeDebugLogger() {
  }

  /**
   * ルーティング決定をログ出力する。
   * {@link #DEBUG_ROUTING} が false の場合は即座にリターンする。
   *
   * @param pipe      ルーティングを行ったパイプ
   * @param item      輸送中のアイテム
   * @param direction 決定された次の方向
   */
  public static void logRouting(
      PipeBlockEntity pipe,
      TravelingItem item,
      Direction direction
  ) {
    if (!DEBUG_ROUTING) return;

    LOGGER.info(
        "[PipeRouting] {} -> {} item={}",
        pipe.getBlockPos(),
        direction,
        item.getStack()
    );
  }
}
