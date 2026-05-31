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
package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IRequestProvider;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.IInjectable;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collections;

public class DockingStationPipe extends DockingStation implements IRequestProvider {

  private final IInjectable injectablePipe = new IInjectable() {
    @Override
    public boolean canInjectItems(Direction from) {
      return true;
    }

    @Override
    public int injectItem(ItemStack stack, boolean doAdd, Direction from, Integer color) {
      return 0;
    }
  };

  private IPipeTile pipe;

  public DockingStationPipe() {
  }

  public DockingStationPipe(IPipeTile iPipe, Direction side) {
    super(new BlockIndex(iPipe.getPos().getX(), iPipe.getPos().getY(), iPipe.getPos().getZ()), side);
    pipe = iPipe;
    world = iPipe.getWorld();
  }

  public IPipeTile getPipe() {
    if (pipe == null) {
      BlockEntity tile = world.getBlockEntity(index().toBlockPos());
      if (tile instanceof IPipeTile) {
        pipe = (IPipeTile) tile;
      }
    }

    if (pipe == null || ((BlockEntity) pipe).isRemoved()) {
      if (RobotManager.registryProvider != null) {
        RobotManager.registryProvider.getRegistry(world).removeStation(this);
      }
      pipe = null;
    }

    return pipe;
  }

  @Override
  public Iterable<StatementSlot> getActiveActions() {
    return Collections.emptyList();
  }

  @Override
  public IInjectable getItemOutput() {
    IPipeTile p = getPipe();
    if (p == null || p.getPipeType() != IPipeTile.PipeType.ITEM) {
      return null;
    }

    return injectablePipe;
  }

  @Override
  public Direction getItemOutputSide() {
    return side != null ? side.getOpposite() : Direction.UP;
  }

  @Override
  public Container getItemInput() {
    return null;
  }

  @Override
  public Direction getItemInputSide() {
    return side != null ? side.getOpposite() : Direction.UP;
  }

  @Override
  public boolean isInitialized() {
    return getPipe() != null;
  }

  @Override
  public int getRequestsCount() {
    return 0;
  }

  @Override
  public ItemStack getRequest(int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack offerItem(int slot, ItemStack stack) {
    return stack;
  }
}
