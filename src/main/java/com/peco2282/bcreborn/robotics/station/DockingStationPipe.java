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
package com.peco2282.bcreborn.robotics.station;

import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IRequestProvider;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.IInjectable;
import com.peco2282.bcreborn.api.transport.IPipeBlockEntity;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class DockingStationPipe extends DockingStation<DockingStationPipe> implements IRequestProvider {

  private final IInjectable injectablePipe = new IInjectable() {
    @Override
    public boolean canInjectItems(@Nullable Direction from) {
      return true;
    }

    @Override
    public int injectItem(ItemStack stack, boolean doAdd, @Nullable Direction from, @Nullable Integer color) {
      return 0;
    }
  };

  private IPipeBlockEntity pipe;

  public DockingStationPipe() {
    super(RoboticsAIType.PIPE);
  }

  public DockingStationPipe(IPipeBlockEntity iPipe, Direction side) {
    super(RoboticsAIType.PIPE, iPipe.getPos(), side);
    pipe = iPipe;
    world = iPipe.getWorld();
  }

  @Nullable
  public IPipeBlockEntity getPipe() {
    if (pipe == null) {
      BlockEntity tile = world.getBlockEntity(pos());
      if (tile instanceof IPipeBlockEntity) {
        pipe = (IPipeBlockEntity) tile;
      }
    }

    if (pipe == null || ((BlockEntity) pipe).isRemoved()) {
      RobotManager.registry().getRegistry(world).removeStation(this);
      pipe = null;
    }

    return pipe;
  }

  @Override
  public Iterable<StatementSlot> getActiveActions() {
    return Collections.emptyList();
  }

  @Nullable
  @Override
  public IInjectable getItemOutput() {
    IPipeBlockEntity p = getPipe();
    if (p == null || p.getPipeType() != PipeType.ITEM) {
      return null;
    }

    return injectablePipe;
  }

  @Override
  public Direction getItemOutputSide() {
    return side != null ? side.getOpposite() : Direction.UP;
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

  @Nullable
  @Override
  public ItemStack offerItem(int slot, ItemStack stack) {
    return stack;
  }
}
