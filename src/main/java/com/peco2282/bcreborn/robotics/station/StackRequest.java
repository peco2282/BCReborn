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

import com.peco2282.bcreborn.api.robots.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class StackRequest {
  private final int slot;
  private final ItemStack stack;
  private IRequestProvider requester;
  private DockingStation<?> station;
  private BlockPos stationIndex;
  private Direction stationSide;

  public StackRequest(IRequestProvider requester, int slot, ItemStack stack) {
    this.requester = requester;
    this.slot = slot;
    this.stack = stack;
    this.station = null;
  }

  private StackRequest(int slot, ItemStack stack, BlockPos stationIndex, Direction stationSide) {
    requester = null;
    this.slot = slot;
    this.stack = stack;
    station = null;
    this.stationIndex = stationIndex;
    this.stationSide = stationSide;
  }

  public static StackRequest loadFromNBT(CompoundTag nbt) {
    if (nbt.contains("stationIndex")) {
      int slot = nbt.getInt("slot");

      ItemStack stack = ItemStack.of(nbt.getCompound("stack"));

      BlockPos stationIndex = BlockPos.of(nbt.getLong("stationIndex"));
      Direction stationSide = Direction.from3DDataValue(nbt.getByte("stationSide"));

      return new StackRequest(slot, stack, stationIndex, stationSide);
    } else {
      return null;
    }
  }

  public IRequestProvider getRequester(Level world) {
    if (requester == null) {
      DockingStation<?> dockingStation = getStation(world);
      if (dockingStation != null) {
        requester = dockingStation.getRequestProvider();
      }
    }
    return requester;
  }

  public int getSlot() {
    return slot;
  }

  public ItemStack getStack() {
    return stack;
  }

  @Nullable
  public DockingStation<?> getStation(Level world) {
    if (station == null) {
      IRobotRegistry robotRegistry = RobotManager.registry().getRegistry(world);
      station = robotRegistry.getStation(stationIndex, stationSide);
    }
    return station;
  }

  public void setStation(DockingStation<?> station) {
    this.station = station;
    this.stationIndex = station.index();
    this.stationSide = station.side();
  }

  public void writeToNBT(CompoundTag nbt) {
    nbt.putInt("slot", slot);

    CompoundTag stackNBT = new CompoundTag();
    stack.save(stackNBT);
    nbt.put("stack", stackNBT);

    if (station != null) {
      nbt.putLong("stationIndex", station.index().asLong());
      nbt.putByte("stationSide", (byte) station.side().get3DDataValue());
    }
  }

  public ResourceId<?> getResourceId(Level world) {
    return getStation(world) != null ? new ResourceIdRequest(getStation(world), slot) : null;
  }
}
