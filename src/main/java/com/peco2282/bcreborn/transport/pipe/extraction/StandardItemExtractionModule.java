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
import com.peco2282.bcreborn.transport.pipe.transport.ItemTransportModule;
import java.util.function.Predicate;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Item extraction of the wooden pipe family (original {@code PipeItemsWood.updateEntity()} / {@code shouldTick()} /
 * {@code checkExtractGeneric()}): engines charge the pipe's battery, and a pull takes as many items of one stack as
 * the stored energy pays for at {@link #ENERGY_PER_ITEM} RF each. The battery is emptied after every pull.
 */
public class StandardItemExtractionModule implements ExtractionModule {
  public static final StandardItemExtractionModule INSTANCE = new StandardItemExtractionModule();
  /** Original: 10 RF per extracted item. */
  public static final int ENERGY_PER_ITEM = 10;
  /** Original: no pull before 8 ticks; from 16 ticks on, any 10 RF are enough. */
  public static final int MIN_PULL_TICKS = 8;
  public static final int MAX_PULL_TICKS = 16;

  /** What one pull may take: a slot of the inventory and at most how many items of it. */
  public record Pick(int slot, int limit) {
  }

  /** Chooses the stack a pull takes from (original {@code checkExtract()}), or {@code null} for none. */
  @FunctionalInterface
  public interface Selector {
    @Nullable
    Pick select(IItemHandler inventory);
  }

  private StandardItemExtractionModule() {
  }

  @Override
  public void extract(PipeBlockEntity pipe) {
    pull(pipe, inventory -> firstMatching(inventory, stack -> true), null);
  }

  /** The first non-empty slot whose stack {@code filter} accepts, all of it. */
  @Nullable
  public static Pick firstMatching(IItemHandler inventory, Predicate<ItemStack> filter) {
    for (int i = 0; i < inventory.getSlots(); i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (!stack.isEmpty() && filter.test(stack) && !inventory.extractItem(i, 1, true).isEmpty()) {
        return new Pick(i, stack.getCount());
      }
    }
    return null;
  }

  /** The inventory on the pipe's extraction side, or {@code null}. */
  @Nullable
  public static IItemHandler inventory(PipeBlockEntity pipe) {
    Direction side = pipe.getExtractionSide();
    BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(side));
    if (be == null || be instanceof PipeBlockEntity) return null;
    return be.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).orElse(null);
  }

  /**
   * One tick of the original {@code updateEntity()}: when {@code shouldTick()} holds, take what {@code selector}
   * picks (unless the pipe is full) and paint it with {@code paint}, then empty the battery and restart the count.
   *
   * @return true if this tick was a pull, whether or not an item came out (the Emzuli pipe moves on after each pull)
   */
  public boolean pull(PipeBlockEntity pipe, Selector selector, @Nullable DyeColor paint) {
    int ticks = pipe.getTicksSincePull();
    if (ticks < MIN_PULL_TICKS) return false;
    int energy = pipe.getExtractionEnergy();
    IItemHandler inventory = inventory(pipe);
    Pick pick = inventory == null ? null : selector.select(inventory);
    // Original shouldTick(): before 16 ticks only if the energy pays for the whole next stack.
    boolean early = pick != null && energy >= pick.limit() * ENERGY_PER_ITEM;
    if (!(ticks < MAX_PULL_TICKS ? early : energy >= ENERGY_PER_ITEM)) return false;

    if (pick != null && pipe.getTravelingItems().size() < ItemTransportModule.MAX_PIPE_STACKS) {
      int count = Math.min(pick.limit(), energy / ENERGY_PER_ITEM);
      ItemStack extracted = count > 0 ? inventory.extractItem(pick.slot(), count, false) : ItemStack.EMPTY;
      if (!extracted.isEmpty()) {
        Direction side = pipe.getExtractionSide();
        pipe.injectItemWithSpeed(extracted, side, pipe.getPipeMaterial().getItemSpeed(), paint);
      }
    }
    pipe.setExtractionEnergy(0);
    pipe.resetTicksSincePull();
    return true;
  }
}
