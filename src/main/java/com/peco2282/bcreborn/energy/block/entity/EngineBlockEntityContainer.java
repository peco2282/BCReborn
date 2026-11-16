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
package com.peco2282.bcreborn.energy.block.entity;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import com.peco2282.bcreborn.common.IBlockEntityContainer;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class EngineBlockEntityContainer<E extends EngineBlockEntityContainer<E>> extends EngineBlockEntity<E>
  implements WorldlyContainer, Container, IBlockEntityContainer, MenuProvider {
  protected final SimpleInventory inv;
  protected final int[] defaultSlotArray;

  public EngineBlockEntityContainer(
    BlockEntityType<E> p_155228_,
    BlockPos p_155229_,
    BlockState p_155230_,
    int invSize
  ) {
    super(p_155228_, p_155229_, p_155230_);
    inv = new SimpleInventory(invSize, "Engine", 64);
    defaultSlotArray = new int[invSize];
    for (int i = 0; i < invSize; i++) {
      defaultSlotArray[i] = i;
    }
  }

  @Override
  public Component getName() {
    return Component.literal(inv.getName());
  }

  // Container
  @Override
  public int getContainerSize() {
    return inv.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return inv.isEmpty();
  }

  @Override
  public ItemStack getItem(int p_18941_) {
    return inv.getItem(p_18941_);
  }

  @Override
  public ItemStack removeItem(int p_18942_, int p_18943_) {
    return inv.removeItem(p_18942_, p_18943_);
  }

  @Override
  public ItemStack removeItemNoUpdate(int p_18951_) {
    return inv.removeItemNoUpdate(p_18951_);
  }

  @Override
  public void setItem(int p_18944_, ItemStack p_18945_) {
    inv.setItem(p_18944_, p_18945_);
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public boolean stillValid(Player p_18946_) {
    return level.getBlockEntity(worldPosition).getClass().equals(getClass());
  }

  // WorldlyContainer
  @Override
  public int[] getSlotsForFace(Direction p_19238_) {
    // Original getAccessibleSlotsFromSide(): every side except the one the engine outputs to.
    return p_19238_ == getOrientation() ? new int[0] : defaultSlotArray;
  }

  @Override
  public boolean canPlaceItemThroughFace(int p_19235_, ItemStack p_19236_, @Nullable Direction p_19237_) {
    return p_19237_ != getOrientation();
  }

  @Override
  public boolean canTakeItemThroughFace(int p_19233_, ItemStack p_19234_, Direction p_19235_) {
    return p_19235_ != getOrientation();
  }

  // Pipes and other Forge automation reach the inventory through the item handler capability; vanilla hoppers use
  // the WorldlyContainer methods above. Both follow the same sides.
  private LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(this, Direction.values());

  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER && side != null && !isRemoved()) {
      return itemHandlers[side.ordinal()].cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    for (LazyOptional<? extends IItemHandler> handler : itemHandlers) {
      handler.invalidate();
    }
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    itemHandlers = SidedInvWrapper.create(this, Direction.values());
  }
}
