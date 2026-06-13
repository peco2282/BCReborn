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


import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.blueprints.BuilderAPI;
import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.api.transport.IPipeConnection;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.builders.BuildersConfig;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.utils.BlockMiner;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MiningWellBlockEntity extends BuildCraftBlockEntity implements IHasWork, IPipeConnection, IControllable, ILEDProvider {
  private final SafeTimeTracker updateTracker = new SafeTimeTracker(BCRebornCore.updateFactor);
  private boolean isDigging = true;
  private BlockMiner miner;
  private int ledState;
  private int ticksSinceAction = 9001;

  public MiningWellBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.MINING_WELL.get(), pos, state);
    this.setBattery(new EnergyStorage(2 * 64 * BuilderAPI.BREAK_ENERGY, BuilderAPI.BREAK_ENERGY * 4 + BuilderAPI.BUILD_ENERGY, 0));
  }

  /**
   * Dig the next available piece of land if not done. As soon as it reaches
   * bedrock, lava or goes below 0, it's considered done.
   */
  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (updateTracker.markTimeIfDelay(level)) {
      sendNetworkUpdate();
    }

    ticksSinceAction++;

    if (mode == Mode.Off) {
      if (miner != null) {
        miner.invalidate();
        miner = null;
      }
      isDigging = false;
      return;
    }

    if (getBattery().getEnergyStored() == 0) {
      return;
    }

    if (miner == null) {

      int depth = getBlockPos().getY() - 1;

      while (level.getBlockState(getBlockPos().offset(0, depth - getBlockPos().getY(), 0)).getBlock() == FactoryBlocks.PLAIN_PIPE.get()) {
        depth = depth - 1;
      }

      if (depth < level.getMinBuildHeight() || depth < getBlockPos().getY() - BuildersConfig.getMiningDepth() || !BlockUtils.canChangeBlock(level, getBlockPos().atY(depth))) {
        isDigging = false;
        // Drain energy, because at 0 energy this will stop doing calculations.
        getBattery().useEnergy(0, 10, false);
        return;
      }

      if (level.getBlockState(getBlockPos().atY(depth)).isAir() || level.getBlockState(getBlockPos().atY(depth)).canBeReplaced()) {
        ticksSinceAction = 0;
        level.setBlock(getBlockPos().atY(depth), FactoryBlocks.PLAIN_PIPE.get().defaultBlockState(), 3);
      } else {
        miner = new BlockMiner(level, this, getBlockPos().getX(), depth, getBlockPos().getZ());
      }
    }

    if (miner != null) {
      isDigging = true;
      ticksSinceAction = 0;

      int usedEnergy = miner.acceptEnergy(getBattery().getEnergyStored());
      getBattery().useEnergy(usedEnergy, usedEnergy, false);

      if (miner.hasFailed()) {
        isDigging = false;
      }

      if (miner.hasFailed() || miner.hasMined()) {
        miner = null;
      }
    }
  }

  protected void sendNetworkUpdate() {
    // Implementation for network update
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    if (miner != null) {
      miner.invalidate();
    }
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    super.writeData(stream);

    ledState = (ticksSinceAction < 2 ? 16 : 0) | (getBattery().getEnergyStored() * 15 / getBattery().getMaxEnergyStored());
    stream.writeByte(ledState);
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    super.readData(stream);

    int newLedState = stream.readUnsignedByte();
    if (newLedState != ledState) {
      ledState = newLedState;
      if (level != null) {
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
      }
    }
  }

  @Override
  public boolean hasWork() {
    return isDigging;
  }

  @Override
  public IPipeConnection.ConnectOverride overridePipeConnection(IPipeTile.PipeType type,
                                                                Direction with) {
    return type == IPipeTile.PipeType.ITEM ? IPipeConnection.ConnectOverride.CONNECT : IPipeConnection.ConnectOverride.DEFAULT;
  }

  @Override
  public Mode getControlMode() {
    return mode;
  }

  @Override
  public void setControlMode(Mode mode) {
    this.mode = mode;
  }

  @Override
  public boolean acceptsControlMode(Mode mode) {
    return mode == Mode.Off || mode == Mode.On;
  }

  @Override
  public int getLEDLevel(int led) {
    if (led == 0) { // Red LED
      return ledState & 15;
    } else { // Green LED
      return (ledState >> 4) > 0 ? 15 : 0;
    }
  }
}
