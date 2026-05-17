package com.peco2282.bcreborn.transport.pipe.transport;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns TravelingItem lifecycle and transport ticking.
 * <p>
 * This module is intentionally isolated from BlockEntity lifecycle logic.
 * <p>
 * 内部ロジックは以下のヘルパーへ委譲している:
 * - RoutingHelper  : 次進行方向の決定
 * - MovementHelper : パイプ/インベントリへの転送
 * - SpeedHelper    : 速度調整
 */
public class ItemTransportModule {

  private final PipeBlockEntity pipe;
  private final List<TravelingItem> travelingItems = new ArrayList<>();

  private final RoutingHelper routingHelper = new RoutingHelper();
  // MovementHelper / SpeedHelper は状態を持たない static utility のためインスタンス不要

  public ItemTransportModule(PipeBlockEntity pipe) {
    this.pipe = pipe;
  }

  // ---- 公開API ----

  public void tick(Level level, BlockPos pos) {
    // tick 中の inject による ConcurrentModificationException を防ぐため snapshot を使用する。
    // remove は元リスト (travelingItems) に対して行う。
    List<TravelingItem> snapshot = List.copyOf(travelingItems);
    PipeBehaviour behaviour = pipe.getBehaviour();

    for (TravelingItem item : snapshot) {

      if (behaviour instanceof ItemPipeBehaviour ib) {
        ib.adjustSpeed(pipe, item);
      } else {
        SpeedHelper.readjustSpeed(item);
      }

      item.tick(level, pos);

      // 中央到達時コールバック（Voidパイプ等）
      if (item.isAtCenter() && !item.isCenterReached()) {
        item.setCenterReached(true);
        if (behaviour instanceof ItemPipeBehaviour ib) {
          ib.onReachedCenter(pipe, item);
          if (item.getStack().isEmpty()) {
            travelingItems.remove(item);
            pipe.setChanged();
            continue;
          }
        }
      }

      if (item.isReached()) {
        if (item.getNextDirection() == null) {
          Direction next;
          if (behaviour instanceof ItemPipeBehaviour ib) {
            next = ib.chooseNextDirection(pipe, item);
            if (next == null) {
              next = routingHelper.chooseNextDirection(pipe, item);
            }
          } else {
            next = routingHelper.chooseNextDirection(pipe, item);
          }
          item.setNextDirection(next);
        }

        MovementResult result = MovementHelper.moveItemToNext(pipe, item);
        switch (result) {
          case SUCCESS -> {
            item.resetBounceCount();
            travelingItems.remove(item);
            pipe.setChanged();
          }
          case BLOCKED, NO_TARGET -> {
            // 行き場がない・満杯の場合は来た方向へ折り返す（bounce back）。
            // 意図的に簡易実装: jam system / queue は導入しない。
            // 2pipe ping-pong（無限折り返し）は BuildCraft 的に許容される挙動。
            // 将来 congestion / jam 対応が必要になった場合はここを起点に拡張する。
            Direction back = item.getEntryDirection().getOpposite();
            item.setNextDirection(back);
            item.setProgress(0.0f);
            item.incrementBounceCount();
            pipe.setChanged();
          }
        }
      }
    }
  }

  /**
   * 外部からアイテムをパイプに注入する。
   * <p>
   * 意図的に即時追加（同一 tick 内）を採用している。
   * PendingInsertionQueue は現段階では過剰設計のため導入しない。
   * recursive inject（パイプ連鎖）も現段階では許容する。
   * 将来 rate limiting が必要になった場合はここを起点に拡張する。
   *
   * @param from アイテムが入ってきた方向（TravelingItem.entryDirection に直接格納される）
   */
  public void injectItem(ItemStack stack, Direction from, float speed) {
    if (pipe.getTransportType() != PipeType.ITEM) return;

    PipeBehaviour behaviour = pipe.getBehaviour();
    if (behaviour instanceof ItemPipeBehaviour ib) {
      if (!ib.canAcceptItem(pipe, stack, from)) return;
      ib.onInjectItem(pipe, stack, from, speed);
      if (stack.isEmpty()) return;
    }

    TravelingItem travelingItem = new TravelingItem(stack.copy(), from, speed);
    travelingItems.add(travelingItem);
    pipe.setChanged();

    Level level = pipe.getLevel();
    BlockPos pos = pipe.getBlockPos();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(pos, pipe.getBlockState(), pipe.getBlockState(), 3);
    }
  }

  public void dropItems() {
    Level level = pipe.getLevel();
    BlockPos pos = pipe.getBlockPos();
    for (TravelingItem item : travelingItems) {
      Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item.getStack());
    }
    travelingItems.clear();
  }

  public List<TravelingItem> getTravelingItems() {
    return Collections.unmodifiableList(travelingItems);
  }

  // ---- NBT ----

  public void save(CompoundTag tag) {
    ListTag travelingTag = new ListTag();
    for (TravelingItem item : travelingItems) {
      travelingTag.add(item.save());
    }
    tag.put("TravelingItems", travelingTag);
  }

  public void load(CompoundTag tag) {
    if (tag.contains("TravelingItems")) {
      travelingItems.clear();
      ListTag list = tag.getList("TravelingItems", Tag.TAG_COMPOUND);
      for (int i = 0; i < list.size(); i++) {
        travelingItems.add(TravelingItem.load(list.getCompound(i)));
      }
    }
  }
}
