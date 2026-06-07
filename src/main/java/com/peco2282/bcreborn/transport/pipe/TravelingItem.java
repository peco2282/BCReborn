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
package com.peco2282.bcreborn.transport.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TravelingItem {
  private final ItemStack stack;

  /**
   * このアイテムが「現在のパイプに入ってきた方向」。
   * 例: 北から入ってきた場合は NORTH。
   * <p>
   * bounce back 時は entryDirection.getOpposite() を nextDirection に設定することで
   * 来た道を戻る挙動を実現する。
   * <p>
   * 注意: 「現在の進行方向」は nextDirection が担う。
   * entryDirection は「どこから来たか」の記録であり、routing 除外判定にも使用される。
   * <p>
   * 将来 Diamond pipe / Iron pipe でフィルタリングを実装する際も、
   * この責務分離（来た方向 vs 次の方向）を維持すること。
   */
  private final Direction entryDirection;

  private float progress; // 0.0 to 1.0
  private float prevProgress; // 前tickのprogress（partialTick補間用）
  private boolean centerReached; // 中央到達フラグ（Voidパイプ等で使用）
  private Direction nextDirection;
  private float speed = 0.05f;
  // bounceCount は将来の jam detection / congestion system 用。
  // 現時点では挙動変更には使用しない。
  private int bounceCount;
  // 金パイプ加速後の残り距離カウンタ（ブロック数）。丸石=16、焼石=32 で減衰完了。
  private int boostedBlocksRemaining;

  public TravelingItem(ItemStack stack, Direction from) {
    this.stack = stack;
    // from はアイテムが「入ってきた側の面」(例: 北の隣パイプから来た → from=NORTH)
    // entryDirection はその方向をそのまま保持する（来た方向 = NORTH）
    this.entryDirection = from;
    this.progress = 0;
    this.prevProgress = 0;
    this.nextDirection = null;
  }

  public TravelingItem(ItemStack stack, Direction from, float speed) {
    this(stack, from);
    this.speed = speed;
  }

  public static TravelingItem load(CompoundTag tag) {
    ItemStack stack = ItemStack.of(tag.getCompound("Stack"));
    // "LastDir" は旧 NBT キー名。後方互換のため両方を読む。
    int dirValue = tag.contains("EntryDir") ? tag.getInt("EntryDir") : tag.getInt("LastDir");
    Direction entryDir = Direction.from3DDataValue(dirValue);
    TravelingItem item = new TravelingItem(stack, entryDir);
    item.progress = tag.getFloat("Progress");
    item.prevProgress = item.progress; // ロード時は前回の状態が不明なので現在の値で初期化
    if (tag.contains("Speed")) {
      item.speed = tag.getFloat("Speed");
    }
    if (tag.contains("NextDir")) {
      item.nextDirection = Direction.from3DDataValue(tag.getInt("NextDir"));
    }
    if (tag.contains("BoostedBlocks")) {
      item.boostedBlocksRemaining = tag.getInt("BoostedBlocks");
    }
    return item;
  }

  public Direction getNextDirection() {
    return nextDirection;
  }

  public void setNextDirection(Direction nextDirection) {
    this.nextDirection = nextDirection;
  }

  public ItemStack getStack() {
    return stack;
  }

  /**
   * このアイテムが現在のパイプに入ってきた方向を返す。
   * routing 時の除外判定（来た方向には戻らない）に使用する。
   * bounce back 時は getEntryDirection().getOpposite() を nextDirection に設定する。
   */
  public Direction getEntryDirection() {
    return entryDirection;
  }

  public float getProgress() {
    return progress;
  }

  public void setProgress(float progress) {
    this.progress = progress;
  }

  public int getBoostedBlocksRemaining() {
    return boostedBlocksRemaining;
  }

  public void setBoostedBlocksRemaining(int blocks) {
    this.boostedBlocksRemaining = blocks;
  }

  public int getBounceCount() {
    return bounceCount;
  }

  public void incrementBounceCount() {
    bounceCount++;
  }

  public void resetBounceCount() {
    bounceCount = 0;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public void tick(Level level, BlockPos pos) {
    // 毎 tick speed 分だけ進捗を加算する。progress >= 1.0 で到達判定。
    prevProgress = progress;
    progress += speed;
  }

  /**
   * アイテムがパイプ中央（progress >= 0.5）に到達したかどうか。
   * 一度 true になったら次のパイプへ移動するまで true を返し続ける。
   * Voidパイプ等の「中央到達時処理」に使用する。
   */
  public boolean isAtCenter() {
    return progress >= 0.5f;
  }

  public boolean isCenterReached() {
    return centerReached;
  }

  public void setCenterReached(boolean centerReached) {
    this.centerReached = centerReached;
  }

  public float getPrevProgress() {
    return prevProgress;
  }

  public void setPrevProgress(float prevProgress) {
    this.prevProgress = prevProgress;
  }

  public boolean isReached() {
    return progress >= 1.0f;
  }

  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();
    tag.put("Stack", stack.save(new CompoundTag()));
    tag.putInt("EntryDir", entryDirection.get3DDataValue());
    tag.putFloat("Progress", progress);
    tag.putFloat("Speed", speed);
    if (nextDirection != null) {
      tag.putInt("NextDir", nextDirection.get3DDataValue());
    }
    if (boostedBlocksRemaining > 0) {
      tag.putInt("BoostedBlocks", boostedBlocksRemaining);
    }
    return tag;
  }
}
