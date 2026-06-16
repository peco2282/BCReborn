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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class TravelingItem {
  public static final Codec<TravelingItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ItemStack.CODEC.fieldOf("stack").forGetter(TravelingItem::getStack),
      Direction.CODEC.fieldOf("entry_direction").forGetter(TravelingItem::getEntryDirection),
      Codec.FLOAT.optionalFieldOf("progress", 0.0f).forGetter(TravelingItem::getProgress),
      Direction.CODEC.optionalFieldOf("next_direction").forGetter(item -> Optional.ofNullable(item.getNextDirection())),
      Codec.FLOAT.optionalFieldOf("speed", 0.05f).forGetter(TravelingItem::getSpeed),
      Codec.INT.optionalFieldOf("bounce_count", 0).forGetter(TravelingItem::getBounceCount),
      Codec.INT.optionalFieldOf("boosted_blocks", 0).forGetter(TravelingItem::getBoostedBlocksRemaining)
  ).apply(instance, TravelingItem::new));
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

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private TravelingItem(ItemStack stack, Direction entryDirection, float progress, Optional<Direction> nextDirection, float speed, int bounceCount, int boostedBlocksRemaining) {
    this.stack = stack;
    this.entryDirection = entryDirection;
    this.progress = progress;
    this.prevProgress = progress;
    this.nextDirection = nextDirection.orElse(null);
    this.speed = speed;
    this.bounceCount = bounceCount;
    this.boostedBlocksRemaining = boostedBlocksRemaining;
  }

  public static TravelingItem load(CompoundTag tag) {
    return CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(System.err::println).orElseThrow();
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
    return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow(false, System.err::println);
  }
}
