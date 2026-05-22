package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

/**
 * Diamond Pipe: フィルターに一致するアイテムを対応する方向へ振り分ける。
 * フィルターに一致しない場合は null を返してデフォルトルーティングに委ねる。
 * <p>
 * originalの PipeItemsDiamond と同様に usedFilters bitmask でラウンドロビン追跡を行う。
 * 各方向9スロット × 6方向 = 54ビット。long で管理する。
 */
public class DiamondItemPipeBehaviour implements ItemPipeBehaviour {

  public static final DiamondItemPipeBehaviour INSTANCE = new DiamondItemPipeBehaviour();

  // 使用済みフィルタースロットのbitmask（方向ordinal * 9 + slotIndex）
  // PipeBlockEntityごとに状態を持つ必要があるため、インスタンスではなくPipeBlockEntityに委譲する
  // ここではステートレスに実装し、PipeBlockEntityのusedFiltersフィールドを参照する

  private DiamondItemPipeBehaviour() {
  }

  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    ItemStack stack = item.getStack();

    // Pass 1: usedFiltersを考慮してフィルター一致方向を探す（originalのfindDest相当）
    Direction found = findMatchingDirection(pipe, item, stack);
    if (found != null) return found;

    // Pass 2: originalのclearDest→findDestと同様に、一致スロットのみusedFiltersをクリアして再探索
    if (pipe.getUsedFilters() != 0) {
      clearUsedFilters(pipe, item, stack);
      found = findMatchingDirection(pipe, item, stack);
      if (found != null) return found;
    }

    // フィルターに一致しない場合、フィルターが設定されていない接続方向（catch-all）を除いて
    // フィルター設定済み方向を除外する（originalのIterator<Direction> i.remove()相当）
    for (Direction dir : Direction.values()) {
      if (dir == item.getEntryDirection()) continue;
      if (!pipe.getBlockState().getValue(PipeBlock.PROPERTY_MAP.get(dir))) continue;
      if (!hasFilter(pipe, dir)) return dir;
    }

    // フィルター未設定方向もない場合はデフォルトルーティングに委ねる
    return null;
  }

  /**
   * originalのfindDest()に相当。
   * usedFiltersを考慮してフィルター一致方向を探す。
   */
  private Direction findMatchingDirection(PipeBlockEntity pipe, TravelingItem item, ItemStack stack) {
    long usedFilters = pipe.getUsedFilters();
    for (Direction dir : Direction.values()) {
      if (dir == item.getEntryDirection()) continue;
      if (!pipe.getBlockState().getValue(PipeBlock.PROPERTY_MAP.get(dir))) continue;
      if (!hasFilter(pipe, dir)) continue;

      var filter = pipe.getFilter(dir);
      int dirOrdinal = dir.get3DDataValue();
      for (int slot = 0; slot < filter.getContainerSize(); slot++) {
        int bitIndex = dirOrdinal * 9 + slot;
        if ((usedFilters & (1L << bitIndex)) != 0) continue;
        ItemStack filterStack = filter.getItem(slot);
        if (!filterStack.isEmpty() && ItemStack.isSameItemSameTags(stack, filterStack)) {
          pipe.setUsedFilters(usedFilters | (1L << bitIndex));
          return dir;
        }
      }
    }
    return null;
  }

  /**
   * originalのclearDest()に相当。
   * 一致するスロットのみusedFiltersをXORでクリアする（全リセットではない）。
   */
  private void clearUsedFilters(PipeBlockEntity pipe, TravelingItem item, ItemStack stack) {
    long usedFilters = pipe.getUsedFilters();
    for (Direction dir : Direction.values()) {
      if (dir == item.getEntryDirection()) continue;
      if (!pipe.getBlockState().getValue(PipeBlock.PROPERTY_MAP.get(dir))) continue;
      if (!hasFilter(pipe, dir)) continue;

      var filter = pipe.getFilter(dir);
      int dirOrdinal = dir.get3DDataValue();
      for (int slot = 0; slot < filter.getContainerSize(); slot++) {
        int bitIndex = dirOrdinal * 9 + slot;
        if ((usedFilters & (1L << bitIndex)) == 0) continue;
        ItemStack filterStack = filter.getItem(slot);
        if (!filterStack.isEmpty() && ItemStack.isSameItemSameTags(stack, filterStack)) {
          usedFilters ^= (1L << bitIndex);
        }
      }
    }
    pipe.setUsedFilters(usedFilters);
  }

  /**
   * 指定方向にフィルターが1つ以上設定されているか確認する。
   * originalのfilterCounts[dir.ordinal()] > 0 に相当。
   */
  private boolean hasFilter(PipeBlockEntity pipe, Direction dir) {
    var filter = pipe.getFilter(dir);
    for (int i = 0; i < filter.getContainerSize(); i++) {
      if (!filter.getItem(i).isEmpty()) return true;
    }
    return false;
  }
}
