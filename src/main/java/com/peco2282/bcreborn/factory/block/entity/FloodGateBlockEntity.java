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
package com.peco2282.bcreborn.factory.block.entity;

import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.*;

public class FloodGateBlockEntity extends BuildCraftBlockEntity implements IFluidHandler {
  public static final int[] REBUILD_DELAY = new int[8];
  public static final int MAX_LIQUID = 2000;

  static {
    REBUILD_DELAY[0] = 128;
    REBUILD_DELAY[1] = 256;
    REBUILD_DELAY[2] = 512;
    REBUILD_DELAY[3] = 1024;
    REBUILD_DELAY[4] = 2048;
    REBUILD_DELAY[5] = 4096;
    REBUILD_DELAY[6] = 8192;
    REBUILD_DELAY[7] = 16384;
  }

  private final TreeMap<Integer, Deque<BlockPos>> pumpLayerQueues = new TreeMap<>();
  private final Set<BlockPos> visitedBlocks = new HashSet<>();
  private final FluidTank tank = new FluidTank(MAX_LIQUID);
  private final Deque<BlockPos> fluidsFound = new LinkedList<>();
  private final boolean[] blockedSides = new boolean[6];
  private int rebuildDelay;
  private int tick = Utils.RANDOM.nextInt();
  private boolean powered = false;

  public FloodGateBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.FLOOD_GATE.get(), pos, state);
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (powered) {
      return;
    }

    tick++;
    if (tick % 16 == 0) {
      // Placeholder logic for placing fluids
    }
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);
    tank.readFromNBT(data.getCompound("tank"));
    rebuildDelay = data.getByte("rebuildDelay");
    powered = data.getBoolean("powered");
    for (int i = 0; i < 6; i++) {
      blockedSides[i] = data.getBoolean("blocked[" + i + "]");
    }
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    CompoundTag tankNbt = new CompoundTag();
    tank.writeToNBT(tankNbt);
    data.put("tank", tankNbt);
    data.putByte("rebuildDelay", (byte) rebuildDelay);
    data.putBoolean("powered", powered);
    for (int i = 0; i < 6; i++) {
      if (blockedSides[i]) {
        data.putBoolean("blocked[" + i + "]", true);
      }
    }
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return this.tank.getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return this.tank.getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return true;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return tank.fill(resource, action);
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return FluidStack.EMPTY;
  }

  public boolean isSideBlocked(int side) {
    return blockedSides[side];
  }
}
