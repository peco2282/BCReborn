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
package com.peco2282.bcreborn.robotics.block.entity;

import com.peco2282.bcreborn.api.StackHelper;
import com.peco2282.bcreborn.api.robots.IRequestProvider;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.robotics.RoboticsBlockEntityTypes;
import com.peco2282.bcreborn.robotics.menu.RequesterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RequesterBlockEntity extends BuildCraftBlockEntity implements MenuProvider, Container, IRequestProvider {
  public static final int NB_ITEMS = 20;

  private final SimpleInventory inv = new SimpleInventory(NB_ITEMS, "items", 64);
  private final SimpleInventory requests = new SimpleInventory(NB_ITEMS, "requests", 64);

  public RequesterBlockEntity(BlockPos pos, BlockState state) {
    super(RoboticsBlockEntityTypes.REQUESTER.get(), pos, state);
  }

  public void setRequest(int index, ItemStack stack) {
    requests.setItem(index, stack);
  }

  public ItemStack getRequestTemplate(int index) {
    return requests.getItem(index);
  }

  @Override
  public int getContainerSize() {
    return inv.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return inv.isEmpty();
  }

  @Override
  public ItemStack getItem(int slotId) {
    return inv.getItem(slotId);
  }

  @Override
  public ItemStack removeItem(int slotId, int count) {
    return inv.removeItem(slotId, count);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slotId) {
    return inv.removeItemNoUpdate(slotId);
  }

  @Override
  public void setItem(int slotId, ItemStack itemStack) {
    inv.setItem(slotId, itemStack);
  }

  @Override
  public int getMaxStackSize() {
    return inv.getMaxStackSize();
  }

  @Override
  public boolean stillValid(Player player) {
    return super.stillValid(player);
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    CompoundTag invNBT = new CompoundTag();
    inv.writeTag(invNBT);
    nbt.put("inv", invNBT);
    CompoundTag reqNBT = new CompoundTag();
    requests.writeTag(reqNBT);
    nbt.put("req", reqNBT);
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readTag(nbt.getCompound("inv"));
    requests.readTag(nbt.getCompound("req"));
  }

  public boolean isFulfilled(int i) {
    ItemStack req = requests.getItem(i);
    if (req.isEmpty()) {
      return true;
    }
    ItemStack existing = inv.getItem(i);
    if (existing.isEmpty()) {
      return false;
    }
    return StackHelper.isMatchingItemOrList(req, existing) && existing.getCount() >= req.getCount();
  }

  @Override
  public int getRequestsCount() {
    return NB_ITEMS;
  }

  @Override
  public ItemStack getRequest(int i) {
    ItemStack req = requests.getItem(i);
    if (req.isEmpty() || isFulfilled(i)) {
      return ItemStack.EMPTY;
    }
    ItemStack request = req.copy();
    ItemStack existing = inv.getItem(i);
    if (existing.isEmpty()) {
      return request;
    }
    if (!StackHelper.isMatchingItemOrList(request, existing)) {
      return ItemStack.EMPTY;
    }
    request.shrink(existing.getCount());
    return request.isEmpty() ? ItemStack.EMPTY : request;
  }

  @Override
  public ItemStack offerItem(int i, ItemStack stack) {
    ItemStack req = requests.getItem(i);
    if (req.isEmpty()) return stack;

    ItemStack existing = inv.getItem(i);
    if (existing.isEmpty()) {
      if (!StackHelper.isMatchingItemOrList(stack, req)) return stack;
      int maxQty = req.getCount();
      if (stack.getCount() <= maxQty) {
        inv.setItem(i, stack.copy());
        stack.setCount(0);
        return ItemStack.EMPTY;
      } else {
        ItemStack newStack = stack.copy();
        newStack.setCount(maxQty);
        stack.shrink(maxQty);
        inv.setItem(i, newStack);
        return stack;
      }
    } else {
      if (!StackHelper.isMatchingItemOrList(stack, existing) || !StackHelper.isMatchingItemOrList(stack, req))
        return stack;
      int maxQty = req.getCount();
      if (existing.getCount() + stack.getCount() <= maxQty) {
        existing.grow(stack.getCount());
        stack.setCount(0);
        return ItemStack.EMPTY;
      } else {
        int toAdd = maxQty - existing.getCount();
        existing.setCount(maxQty);
        stack.shrink(toAdd);
        return stack;
      }
    }
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornrobotics.requester");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new RequesterMenu(p_39954_, p_39955_, this);
  }
}
