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

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.block.BlockEntityBuffer;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.core.ConfigCore;
import com.peco2282.bcreborn.energy.fluids.SingleUseTank;
import com.peco2282.bcreborn.energy.fluids.Tank;
import com.peco2282.bcreborn.energy.fluids.TankUtils;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PumpBlockEntity extends BuildCraftBlockEntity implements IHasWork, IFluidHandler, IRedstoneEngineReceiver, ILEDProvider, IEnergyStorage {

  public static final int REBUID_DELAY = 512;
  public static int MAX_LIQUID = 16000;
  public Tank tank = new SingleUseTank("tank", MAX_LIQUID);

//  private EntityBlock tube;
  private final TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues = new TreeMap<>();
  private double tubeY = Double.NaN;
  private int aimY = -1;

  private SafeTimeTracker timer = new SafeTimeTracker(REBUID_DELAY);
  private int tick = Utils.RANDOM.nextInt(32);
  private int tickPumped = tick - 20;
  private int numFluidBlocksFound = 0;
  private boolean powered = false;

  private int ledState;
  // tick % 16 => min. 16 ticks per network update
  private final SafeTimeTracker updateTracker = new SafeTimeTracker(Math.max(16, ConfigCore.getUpdateFactor()));


  public PumpBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.PUMP.get(), pos, state);
    this.setBattery(new EnergyStorage(1000, 150, 0));
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (powered) {
      pumpLayerQueues.clear();
      tubeY = Double.NaN;
      aimY = -1;
      if (updateTracker.markTimeIfDelay(level)) {
        setChanged();
      }
      return;
    }

    if (Double.isNaN(tubeY)) {
      tubeY = pos.getY();
      aimY = pos.getY();
      setChanged();
    }

    pushToConsumers();

    // Update tube height
    double targetTubeY = aimY;
    if (tubeY > targetTubeY + 0.01) {
      tubeY -= 0.05;
      if (tubeY < targetTubeY) {
        tubeY = targetTubeY;
      }
    } else if (tubeY < targetTubeY - 0.01) {
      tubeY += 0.05;
      if (tubeY > targetTubeY) {
        tubeY = targetTubeY;
      }
    }

    tick++;

    if (tick % 16 != 0) {
      return;
    }
    BlockIndex index = getNextIndexToPump(false);

    FluidStack fluidToPump = (index != null && index.getY() != -1) ? BlockUtils.drainBlock(level, index, false) : null;
    if (fluidToPump != null) {
      if (tank.fill(fluidToPump, FluidAction.SIMULATE) == fluidToPump.getAmount()) {
        if (getBattery().useEnergy(100, 100, false) > 0) {
          if (fluidToPump.getFluid() != Fluids.WATER || numFluidBlocksFound < 9) {
            index = getNextIndexToPump(true);
            BlockUtils.drainBlock(level, index, true);
          }

          tank.fill(fluidToPump, FluidAction.EXECUTE);
          tickPumped = tick;
        }
      }
    } else {
      if (tick % 128 == 0) {
        rebuildQueue();

        BlockIndex next = getNextIndexToPump(false);
        if (next == null || next.getY() == -1) {
          for (int y = aimY - 1; y > level.getMinBuildHeight(); --y) {
            BlockPos p = pos.atY(y);
            if (isPumpableFluid(p)) {
              aimY = y;
              setChanged();
              return;
            } else if (isBlocked(p)) {
              return;
            }
          }
        }
      }
    }
  }


  public void onNeighborBlockChange(Block block) {
    boolean p = level.hasNeighborSignal(getBlockPos());

    if (powered != p) {
      powered = p;
      setChanged();
    }
  }

  private boolean isBlocked(BlockPos pos) {
    BlockState state = level.getBlockState(pos);

    return state.blocksMotion();
  }

  private void pushToConsumers() {
    if (cache == null) {
      cache = BlockEntityBuffer.makeBuffer(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), false);
    }

    TankUtils.pushFluidToConsumers(tank, 400, cache);
  }

  @Nullable
  private BlockIndex getNextIndexToPump(boolean remove) {
    System.out.println("getNextIndexToPump");
    if (pumpLayerQueues.isEmpty()) {
      System.out.println("pumpLayerQueues.isEmpty() start");
      if (timer.markTimeIfDelay(level)) {
        rebuildQueue();
      }
      System.out.println("pumpLayerQueues.isEmpty()");

      return null;
    }

    Deque<BlockIndex> topLayer = pumpLayerQueues.lastEntry().getValue();

    System.out.println("topLayer: " + topLayer);
    if (topLayer != null) {
      if (topLayer.isEmpty()) {
        pumpLayerQueues.pollLastEntry();
        return getNextIndexToPump(remove);
      }

      if (remove) {
        return topLayer.pollLast();
      } else {
        return topLayer.peekLast();
      }
    } else {
      return null;
    }
  }

  private Deque<BlockIndex> getLayerQueue(int layer) {
    Deque<BlockIndex> pumpQueue = pumpLayerQueues.get(layer);

    if (pumpQueue == null) {
      pumpQueue = new LinkedList<BlockIndex>();
      pumpLayerQueues.put(layer, pumpQueue);
    }

    return pumpQueue;
  }

  public void rebuildQueue() {
    numFluidBlocksFound = 0;
    pumpLayerQueues.clear();

    if (aimY < 0) {
      return;
    }

    BlockPos pos = getBlockPos().atY(aimY);
    Fluid pumpingFluid = BlockUtils.getFluid(level.getBlockState(pos).getBlock());

    if (pumpingFluid == null) {
      return;
    }

    if (tank instanceof SingleUseTank sut) {
      if (pumpingFluid != sut.getAcceptedFluid() && sut.getAcceptedFluid() != null) {
        return;
      }
    }

    Set<BlockIndex> visitedBlocks = new HashSet<>();
    Deque<BlockIndex> fluidsFound = new LinkedList<>();

    queueForPumping(pos, visitedBlocks, fluidsFound, pumpingFluid);

    while (!fluidsFound.isEmpty()) {
      Deque<BlockIndex> fluidsToExpand = fluidsFound;
      fluidsFound = new LinkedList<>();

      for (BlockIndex index : fluidsToExpand) {
        queueForPumping(index.above(), visitedBlocks, fluidsFound, pumpingFluid);
        queueForPumping(index.east(), visitedBlocks, fluidsFound, pumpingFluid);
        queueForPumping(index.west(), visitedBlocks, fluidsFound, pumpingFluid);
        queueForPumping(index.south(), visitedBlocks, fluidsFound, pumpingFluid);
        queueForPumping(index.north(), visitedBlocks, fluidsFound, pumpingFluid);

        if (pumpingFluid == Fluids.WATER
          && numFluidBlocksFound >= 9) {
          return;
        }
      }
    }
  }

  public void queueForPumping(BlockPos pos, Set<BlockIndex> visitedBlocks, Deque<BlockIndex> fluidsFound, Fluid pumpingFluid) {
    BlockIndex index = new BlockIndex(pos);
    if (visitedBlocks.add(index)) {
      if (pos.distSqr(getBlockPos().atY(pos.getY())) > 64 * 64) {
        return;
      }

      Block block = level.getBlockState(pos).getBlock();

      if (BlockUtils.getFluid(block) == pumpingFluid) {
        fluidsFound.add(index);
      }

      if (canDrainBlock(block, pos, pumpingFluid)) {
        getLayerQueue(pos.getY()).add(index);
        numFluidBlocksFound++;
      }
    }
  }

  private boolean isPumpableFluid(BlockPos pos) {
    Fluid fluid = BlockUtils.getFluid(BlockUtils.getBlock(getLevel(), pos));

    if (fluid == null) {
      return false;
    } else {
      if (tank instanceof SingleUseTank sut) {
        return !(sut.getAcceptedFluid() != null && sut.getAcceptedFluid() != fluid);
      }
      return true;
    }
  }

  private boolean canDrainBlock(Block block, BlockPos pos, Fluid fluid) {
    FluidStack fluidStack = BlockUtils.drainBlock(block, level, pos, false);

    if (fluidStack.isEmpty() || fluidStack.getAmount() <= 0) {
      return false;
    } else {
      return fluidStack.getFluid() == fluid;
    }
  }

  private boolean isFluidAllowed(Fluid fluid) {
    return true;
  }


  @Override
  public void load(CompoundTag data) {
    super.load(data);
    tank.readFromNBT(data.getCompound("tank"));
    powered = data.getBoolean("powered");
    aimY = data.getInt("aimY");
    tubeY = data.getDouble("tubeY");
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    CompoundTag tankNbt = new CompoundTag();
    tank.writeToNBT(tankNbt);
    data.put("tank", tankNbt);
    data.putBoolean("powered", powered);
    data.putInt("aimY", aimY);
    data.putDouble("tubeY", tubeY);
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return getBattery().receiveEnergy(maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return getBattery().extractEnergy(maxExtract, simulate);
  }

  @Override
  public int getEnergyStored() {
    return getBattery().getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return getBattery().getMaxEnergyStored();
  }

  @Override
  public boolean canExtract() {
    return getBattery().canExtract();
  }

  @Override
  public boolean canReceive() {
    return getBattery().canReceive();
  }

  @Override
  public boolean hasWork() {
    return true; // Simplified
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
    return 0; // Pump doesn't accept fluid
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return tank.drain(resource, action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return tank.drain(maxDrain, action);
  }

  @Override
  public boolean canConnectRedstoneEngine(Direction side) {
    return true;
  }

  @Override
  public int getLEDLevel(int led) {
    if (led == 0) { // Red LED
      return ledState & 15;
    } else { // Green LED
      return (ledState >> 4) > 0 ? 15 : 0;
    }
  }

  public double getTubeHeight() {
    if (Double.isNaN(tubeY)) {
      return 0.0;
    }
    return Math.max(0.0, getBlockPos().getY() - tubeY);
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    super.writeData(stream);
    stream.writeDouble(tubeY);
    stream.writeShort(aimY);
    stream.writeBoolean(powered);
    ledState = ((tick - tickPumped) < 48 ? 16 : 0) | (getBattery().getEnergyStored() * 15 / getBattery().getMaxEnergyStored());
    stream.writeByte(ledState);
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    super.readData(stream);
    tubeY = stream.readDouble();
    aimY = stream.readShort();
    powered = stream.readBoolean();
    ledState = stream.readByte();
  }

}
