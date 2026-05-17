package com.peco2282.bcreborn.transport.pipe.transport;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * アイテムを次のパイプまたはインベントリへ移動させる責務を担うヘルパー。
 * pipe-to-pipe / pipe-to-inventory の転送処理を集約する。
 * <p>
 * 状態を持たない static utility クラス。
 * 将来的に transfer layer へ分離しやすい構造にするための退避クラス。
 */
final class MovementHelper {

  private MovementHelper() {
  }

  /**
   * TravelingItem を次の接続先（パイプまたはインベントリ）へ移動させる。
   * <p>
   * 意図的に即時 inject（同一 tick 内転送）を採用している。
   * queue / async transport は現段階では過剰設計のため導入しない。
   * 将来 congestion 対応が必要になった場合はここを起点に拡張する。
   *
   * @return {@link MovementResult#SUCCESS} 移動成功、
   * {@link MovementResult#BLOCKED} インベントリ満杯、
   * {@link MovementResult#NO_TARGET} 接続先なし
   */
  static MovementResult moveItemToNext(PipeBlockEntity pipe, TravelingItem item) {
    Direction dir = item.getNextDirection();
    if (dir == null) return MovementResult.NO_TARGET;

    BlockPos pos = pipe.getBlockPos();
    BlockPos nextPos = pos.relative(dir);
    Level level = pipe.getLevel();

    BlockEntity be = level.getBlockEntity(nextPos);

    if (be instanceof PipeBlockEntity nextPipe) {
      nextPipe.injectItemWithSpeed(item.getStack(), dir.getOpposite(), item.getSpeed());
      return MovementResult.SUCCESS;
    } else if (be != null) {
      LazyOptional<IItemHandler> capability = be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite());
      if (capability.isPresent()) {
        ItemStack remaining = ItemHandlerHelper.insertItemStacked(
            capability.orElseThrow(IllegalStateException::new),
            item.getStack(),
            false
        );
        if (remaining.isEmpty()) {
          return MovementResult.SUCCESS;
        } else {
          // Inventory full は現時点では BLOCKED として扱う。
          // 将来的には JAMMED へ分離可能。
          item.getStack().setCount(remaining.getCount());
          return MovementResult.BLOCKED;
        }
      }
    }

    return MovementResult.NO_TARGET;
  }
}
