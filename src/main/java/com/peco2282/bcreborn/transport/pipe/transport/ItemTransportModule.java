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

import com.mojang.logging.LogUtils;
import com.peco2282.bcreborn.core.CoreConfig;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;

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
  private static final Logger LOGGER = LogUtils.getLogger();

  private final PipeBlockEntity pipe;
  private final List<TravelingItem> travelingItems = new ArrayList<>();

  private final RoutingHelper routingHelper = new RoutingHelper();
  // MovementHelper / SpeedHelper は状態を持たない static utility のためインスタンス不要

  public ItemTransportModule(PipeBlockEntity pipe) {
    this.pipe = pipe;
  }

  // ---- 公開API ----

  public void tick(Level level, BlockPos pos) {
    if (level.isClientSide) {
      // 表示側は同期された経路を補間するだけで、搬送や経路選択を行わない。
      for (TravelingItem item : travelingItems) {
        item.tick(level, pos);
        item.setProgress(Math.min(item.getProgress(), item.getNextDirection() == null ? 0.5f : 1.0f));
      }
      return;
    }
    if (pipe.getTransportType() != PipeType.ITEM) {
      if (!travelingItems.isEmpty()) {
        dropItems();
      }
      return;
    }
    // tick 中の inject による ConcurrentModificationException を防ぐため snapshot を使用する。
    // remove は元リスト (travelingItems) に対して行う。
    List<TravelingItem> snapshot = List.copyOf(travelingItems);
    PipeBehaviour behaviour = pipe.getBehaviour();

    for (TravelingItem item : snapshot) {
      // 出口未決定のアイテムには、中央へ到達する前に経路を与える。
      if (item.getNextDirection() == null) {
        chooseDestination(item, behaviour);
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
      // Original: an item whose output is UNKNOWN (nowhere to go) is dropped when it reaches the centre.
      if (item.isCenterReached() && item.getNextDirection() == null && pipe.getPipeMaterial() != PipeMaterial.VOID) {
        dropItem(level, item);
        travelingItems.remove(item);
        pipe.setChanged();
        continue;
      }

      if (item.isReached()) {
        MovementResult result = MovementHelper.moveItemToNext(pipe, item);
        switch (result) {
          case SUCCESS -> {
            item.resetBounceCount();
            travelingItems.remove(item);
            pipe.setChanged();
          }
          case BLOCKED -> {
            // Original reverseItem(): the item re-enters this pipe from the side that refused it and picks a new
            // destination (which may be none, so it is dropped at the centre).
            item.reverse();
            item.setNextDirection(null);
            adjustSpeed(item, behaviour);
            chooseDestination(item, behaviour);
            pipe.setChanged();
          }
          case NO_TARGET -> {
            // Original handleTileReached(): neither a pipe nor an inventory at the end, so the item is dropped.
            dropItem(level, item);
            travelingItems.remove(item);
            pipe.setChanged();
          }
        }
      }
    }
    // 最後のアイテムが出たときも、空になった状態を描画側へ送る。
    if (!snapshot.isEmpty()) {
      level.sendBlockUpdated(pos, pipe.getBlockState(), pipe.getBlockState(), 3);
    }
  }

  private void chooseDestination(TravelingItem item, PipeBehaviour behaviour) {
    Direction next = null;
    if (behaviour instanceof ItemPipeBehaviour ib) {
      next = ib.chooseNextDirection(pipe, item);
    }
    // A material's own choice must still respect closed valves.
    if (next != null && next != item.getEntryDirection() && !RoutingHelper.isOpenTowards(pipe, next)) {
      next = null;
    }
    if (next == null) {
      next = routingHelper.chooseNextDirection(pipe, item);
    }
    item.setNextDirection(next);
  }

  private void adjustSpeed(TravelingItem item, PipeBehaviour behaviour) {
    if (behaviour instanceof ItemPipeBehaviour ib) {
      ib.adjustSpeed(pipe, item);
    } else {
      SpeedHelper.readjustSpeed(item, SpeedHelper.SLOWDOWN);
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
    injectItem(stack, from, speed, null);
  }

  /**
   * @param color the paint the item already carries (original {@code TravelingItem.color}), or null
   */
  public void injectItem(ItemStack stack, Direction from, float speed, @Nullable DyeColor color) {
    if (pipe.getLevel() == null || pipe.getLevel().isClientSide) return;
    if (pipe.getTransportType() != PipeType.ITEM) return;

    PipeBehaviour behaviour = pipe.getBehaviour();
    if (behaviour instanceof ItemPipeBehaviour ib) {
      if (!ib.canAcceptItem(pipe, stack, from)) return;
      ib.onInjectItem(pipe, stack, from, speed);
      if (stack.isEmpty()) return;
    }

    TravelingItem travelingItem = new TravelingItem(stack.copy(), from, speed);
    travelingItem.setColor(color);
    adjustSpeed(travelingItem, behaviour);
    if (!pipe.getLevel().isClientSide) {
      chooseDestination(travelingItem, behaviour);
    }
    travelingItems.add(travelingItem);
    pipe.setChanged();

    Level level = pipe.getLevel();
    BlockPos pos = pipe.getBlockPos();
    if (!level.isClientSide) {
      level.sendBlockUpdated(pos, pipe.getBlockState(), pipe.getBlockState(), 3);
      checkOverflow(level, pos);
    }
  }

  /** Original {@code PipeTransportItems.MAX_PIPE_STACKS}: more stacks than this make the pipe burst. */
  public static final int MAX_PIPE_STACKS = 64;
  /** Original {@code PipeTransportItems.MAX_PIPE_ITEMS}: more items than this make the pipe burst. */
  public static final int MAX_PIPE_ITEMS = 1024;

  /**
   * Original {@code injectItem()} overflow handling: from half of {@link #MAX_PIPE_STACKS} on, similar items are
   * merged ({@link #groupItems()}); above {@link #MAX_PIPE_STACKS} stacks or {@link #MAX_PIPE_ITEMS} items the
   * pipe bursts ({@code destroyPipe()}): it breaks with an explosion effect and drops itself and its contents.
   */
  private void checkOverflow(Level level, BlockPos pos) {
    if (travelingItems.size() >= MAX_PIPE_STACKS / 2) {
      groupItems();
    }
    int items = 0;
    for (TravelingItem item : travelingItems) items += item.getStack().getCount();
    if (travelingItems.size() > MAX_PIPE_STACKS || items > MAX_PIPE_ITEMS) {
      LOGGER.warn("Pipe exploded at {} because it had too many stacks ({}) or items ({})", pos, travelingItems.size(), items);
      if (level instanceof ServerLevel server) {
        // Original BlockUtils.explodeBlock(): explosion effect only, the pipe itself is the one block affected.
        server.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F,
          (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
      }
      level.destroyBlock(pos, true);
    }
  }

  /**
   * Original {@code groupEntities()} / {@code TravelingItem.tryMergeInto()}: an item merges into another one that
   * is on the same leg (before or after the centre), goes the same way, has the same paint and whose stack can take
   * the whole of it.
   */
  public void groupItems() {
    List<TravelingItem> merged = new ArrayList<>();
    for (TravelingItem item : travelingItems) {
      if (merged.contains(item)) continue;
      for (TravelingItem other : travelingItems) {
        if (other == item || merged.contains(other)) continue;
        if (item.isCenterReached() != other.isCenterReached()
          || item.getNextDirection() != other.getNextDirection()
          || item.getColor() != other.getColor()
          || !ItemStack.isSameItemSameTags(item.getStack(), other.getStack())
          || item.getStack().getCount() + other.getStack().getCount() > other.getStack().getMaxStackSize()) {
          continue;
        }
        other.getStack().grow(item.getStack().getCount());
        merged.add(item);
        break;
      }
    }
    if (!merged.isEmpty()) {
      travelingItems.removeAll(merged);
      pipe.setChanged();
    }
  }

  /** Original {@code dropItem()} / {@code TravelingItem.toEntityItem()}. */
  private void dropItem(Level level, TravelingItem item) {
    ItemStack stack = item.getStack();
    if (stack.isEmpty()) return;
    BlockPos pos = pipe.getBlockPos();
    ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
    entity.lifespan = CoreConfig.getItemLifespan() * 20;
    entity.setPickUpDelay(10);
    Direction dir = item.getNextDirection();
    double push = dir == null ? 0 : 0.1 + item.getSpeed() * 2F;
    float spread = level.random.nextFloat() * 0.04F - 0.02F;
    entity.setDeltaMovement(
      level.random.nextGaussian() * spread + (dir == null ? 0 : dir.getStepX() * push),
      level.random.nextGaussian() * spread + (dir == null ? 0 : dir.getStepY() * push),
      level.random.nextGaussian() * spread + (dir == null ? 0 : dir.getStepZ() * push));
    level.addFreshEntity(entity);
  }

  public void dropItems() {
    Level level = pipe.getLevel();
    if (level == null || level.isClientSide) {
      travelingItems.clear();
      return;
    }
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
    TravelingItem.CODEC.listOf().encodeStart(NbtOps.INSTANCE, travelingItems)
      .resultOrPartial(System.err::println)
      .ifPresent(t -> tag.put("TravelingItems", t));
  }

  public void load(CompoundTag tag) {
    if (tag.contains("TravelingItems")) {
      travelingItems.clear();
      TravelingItem.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("TravelingItems"))
        .resultOrPartial(System.err::println)
        .ifPresent(travelingItems::addAll);

      if (pipe.getTransportType() != PipeType.ITEM && !travelingItems.isEmpty()) {
        dropItems();
      }
    }
  }
}
