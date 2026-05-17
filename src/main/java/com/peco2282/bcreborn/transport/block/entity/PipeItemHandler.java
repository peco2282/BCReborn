package com.peco2282.bcreborn.transport.block.entity;

import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * パイプのアイテムCapabilityハンドラー。
 * 外部からのアイテム挿入を受け付け、パイプ内に注入する。
 */
public class PipeItemHandler implements IItemHandler {
  private final PipeBlockEntity pipe;
  private final Direction side;

  public PipeItemHandler(PipeBlockEntity pipe, Direction side) {
    this.pipe = pipe;
    this.side = side;
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @NotNull
  @Override
  public ItemStack getStackInSlot(int slot) {
    return ItemStack.EMPTY;
  }

  @NotNull
  @Override
  public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
    if (pipe.getTransportType() != PipeType.ITEM) return stack;
    PipeMaterial mat = pipe.getPipeMaterial();
    if (mat == PipeMaterial.WOOD || mat == PipeMaterial.EMERALD) return stack;
    if (!simulate) {
      pipe.injectItem(stack.copy(), side != null ? side : Direction.UP);
    }
    return ItemStack.EMPTY;
  }

  @NotNull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    return ItemStack.EMPTY;
  }

  @Override
  public int getSlotLimit(int slot) {
    return 64;
  }

  @Override
  public boolean isItemValid(int slot, @NotNull ItemStack stack) {
    return pipe.getTransportType() == PipeType.ITEM;
  }
}
