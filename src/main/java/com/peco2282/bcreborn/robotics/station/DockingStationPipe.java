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

import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.api.gates.IGate;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.robotics.statements.RoboticsStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.ActionIterator;
import net.minecraft.world.Container;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import java.util.List;
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

    /** Original: the item enters the pipe from the station's side and the whole stack is always accepted. */
    @Override
    public int injectItem(ItemStack stack, boolean doAdd, @Nullable Direction from, @Nullable Integer color) {
      IPipeBlockEntity p = getPipe();
      if (p == null) {
        return 0;
      }
      return p.injectItem(stack, doAdd, from, color);
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
    IPipeBlockEntity p = getPipe();
    if (p == null || p.getPipe() == null) {
      return Collections.emptyList();
    }
    return new ActionIterator(p.getPipe());
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
    return side().getOpposite();
  }

  /** Original: a wooden pipe is this station's source; {@code null} for every other pipe. */
  @Nullable
  private Direction sourceSide(PipeType type) {
    IPipeBlockEntity p = getPipe();
    if (!(p instanceof PipeBlockEntity pipeEntity) || p.getPipeType() != type
      || pipeEntity.getPipeMaterial() != PipeMaterial.WOOD) {
      return null;
    }
    return pipeEntity.getExtractionSide();
  }

  /** Original getItemInput(): the inventory a wooden item pipe extracts from. */
  @Nullable
  @Override
  public Container getItemInput() {
    Direction dir = sourceSide(PipeType.ITEM);
    if (dir == null) {
      return null;
    }
    BlockEntity connected = world.getBlockEntity(pos().relative(dir));
    return connected instanceof Container container ? container : null;
  }

  @Override
  public Direction getItemInputSide() {
    Direction dir = sourceSide(PipeType.ITEM);
    return dir != null ? dir.getOpposite() : Direction.UP;
  }

  /** Original getFluidInput(): the tank a wooden fluid pipe extracts from. */
  @Nullable
  @Override
  public IFluidHandler getFluidInput() {
    Direction dir = sourceSide(PipeType.FLUID);
    if (dir == null) {
      return null;
    }
    BlockEntity connected = world.getBlockEntity(pos().relative(dir));
    if (connected == null) {
      return null;
    }
    return connected.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).orElse(null);
  }

  @Override
  public Direction getFluidInputSide() {
    Direction dir = sourceSide(PipeType.FLUID);
    return dir != null ? dir.getOpposite() : Direction.UP;
  }

  /** Original getFluidOutput(): the fluid pipe itself. */
  @Nullable
  @Override
  public IFluidHandler getFluidOutput() {
    IPipeBlockEntity p = getPipe();
    if (!(p instanceof BlockEntity entity) || p.getPipeType() != PipeType.FLUID) {
      return null;
    }
    return entity.getCapability(ForgeCapabilities.FLUID_HANDLER, side()).orElse(null);
  }

  @Override
  public boolean providesPower() {
    IPipeBlockEntity p = getPipe();
    return p != null && p.getPipeType() == PipeType.ENERGY;
  }

  /** Original: a machine next to the pipe that takes requests, otherwise this station's own gate requests. */
  @Override
  public IRequestProvider getRequestProvider() {
    if (getPipe() == null) {
      return this;
    }
    for (Direction dir : Direction.values()) {
      BlockEntity nearby = world.getBlockEntity(pos().relative(dir));
      if (nearby instanceof IRequestProvider provider) {
        return provider;
      }
    }
    return this;
  }

  @Override
  public boolean isInitialized() {
    return getPipe() != null;
  }

  @Override
  public boolean take(RobotEntityBase robot) {
    if (getPipe() == null) {
      return false;
    }
    boolean result = super.take(robot);
    if (result) {
      pipe.scheduleRenderUpdate();
    }
    return result;
  }

  @Override
  public boolean takeAsMain(RobotEntityBase robot) {
    if (getPipe() == null) {
      return false;
    }
    boolean result = super.takeAsMain(robot);
    if (result) {
      pipe.scheduleRenderUpdate();
    }
    return result;
  }

  @Override
  public void unsafeRelease(RobotEntityBase robot) {
    super.unsafeRelease(robot);
    if (robotTaking() == null && pipe != null && !((BlockEntity) pipe).isRemoved()) {
      pipe.scheduleRenderUpdate();
    }
  }

  @Override
  public void onChunkUnload() {
    pipe = null;
  }

  @Override
  public int getRequestsCount() {
    return 127;
  }

  /** Original: the slot encodes the gate side (bits 4-6), the action (bits 2-3) and the parameter (bits 0-1). */
  @Override
  public ItemStack getRequest(int slot) {
    int sideIndex = (slot & 0x70) >> 4;
    int action = (slot & 0xc) >> 2;
    int param = slot & 0x3;
    IPipeBlockEntity p = getPipe();
    if (p == null || p.getPipe() == null || sideIndex >= Direction.values().length) {
      return ItemStack.EMPTY;
    }
    IGate gate = p.getPipe().getGate(Direction.from3DDataValue(sideIndex));
    if (gate == null) {
      return ItemStack.EMPTY;
    }

    List<IStatement> actions = gate.getActions();
    if (actions.size() <= action || actions.get(action) != RoboticsStatements.actionStationRequestItems) {
      return ItemStack.EMPTY;
    }

    StatementSlot slotStmt = null;
    for (StatementSlot stmt : gate.getActiveActions()) {
      if (stmt.statement == actions.get(action)) {
        slotStmt = stmt;
        break;
      }
    }
    if (slotStmt == null || slotStmt.parameters.length <= param || slotStmt.parameters[param] == null) {
      return ItemStack.EMPTY;
    }
    ItemStack request = slotStmt.parameters[param].getItemStack();
    return request == null ? ItemStack.EMPTY : request;
  }

  @Nullable
  @Override
  public ItemStack offerItem(int slot, ItemStack stack) {
    int consumed = injectablePipe.injectItem(stack, true, side().getOpposite(), null);
    if (stack.getCount() > consumed) {
      ItemStack newStack = stack.copy();
      newStack.shrink(consumed);
      return newStack;
    }
    return ItemStack.EMPTY;
  }
}
