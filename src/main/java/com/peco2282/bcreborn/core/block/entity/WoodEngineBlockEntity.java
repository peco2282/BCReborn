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
package com.peco2282.bcreborn.core.block.entity;

import com.peco2282.bcreborn.api.power.IRedstoneEngine;
import com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.core.CoreBlockEntityTypes;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class WoodEngineBlockEntity extends EngineBlockEntity<WoodEngineBlockEntity> implements IRedstoneEngine {
  private boolean hasSent;

  public WoodEngineBlockEntity(BlockPos pos, BlockState state) {
    super(CoreBlockEntityTypes.WOODEN_ENGINE.get(), pos, state);
    configureEnergy(1000, 10);
  }

  @Override
  protected ResourceBuilder getEngineResource() {
    return ResourceBuilder.core().addPath("wood_engine");
  }

  @Override
  public boolean isFuelable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isBurning() {
    return isRedstonePowered;
  }

  @Override
  protected void engineUpdate() {
    super.engineUpdate();
    // BuildCraft 7.1.27 の内部 RF 値をそのまま使用する。
    if (isRedstonePowered && level.getGameTime() % 16 == 0) {
      energyStorage.generateEnergy(10, false);
    }
    heat = MIN_HEAT + (MAX_HEAT - MIN_HEAT) * (float) getEnergyLevel();
    EnergyStage stage = computeStageFromHeat(heat);
    if (stage != energyStage) {
      energyStage = stage;
      setChanged();
    }
  }

  @Override
  public void burning() {
    // 発電は engineUpdate、送電はピストンの伸長時に行う。
  }

  @Override
  public void updateProgress() {
    if (progressPart != 0 && progress >= 0.5f && !hasSent && isRedstonePowered) {
      hasSent = true;
      pushEnergyToNeighbor();
      setChanged();
    }
  }

  @Override
  protected void onPistonCycled() {
    hasSent = false;
  }

  @Nullable
  private IEnergyStorage getReceiver() {
    if (level == null) return null;
    BlockEntity target = level.getBlockEntity(worldPosition.relative(orientation));
    Direction face = orientation.getOpposite();
    if (target instanceof IRedstoneEngineReceiver receiver && receiver.canConnectRedstoneEngine(face)) {
      return receiver;
    }
    // TileGenericPipe の木エンジン受け入れ条件。電力パイプは対象外。
    if (target instanceof PipeBlockEntity pipe && pipe.getTransportType() != PipeType.ENERGY) {
      return pipe.getCapability(ForgeCapabilities.ENERGY, face).orElse(null);
    }
    return null;
  }

  @Override
  protected boolean canPushEnergy() {
    IEnergyStorage receiver = getReceiver();
    return receiver != null && receiver.canReceive()
      && receiver.receiveEnergy(Math.min(10, getEnergyStored()), true) > 0;
  }

  @Override
  protected void pushEnergyToNeighbor() {
    if (level == null || level.isClientSide) return;
    IEnergyStorage receiver = getReceiver();
    if (receiver == null) return;
    int available = energyStorage.extractEnergy(10, true);
    int accepted = receiver.receiveEnergy(available, false);
    energyStorage.extractEnergy(accepted, false);
  }

  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> cap, @Nullable Direction side) {
    // 外部のFE機械・木エネルギーパイプによる吸い出しを許可しない。
    if (cap == ForgeCapabilities.ENERGY) return LazyOptional.empty();
    return super.getCapability(cap, side);
  }

  @Override
  protected EnergyStage computeStageFromHeat(float h) {
    double energy = getEnergyLevel();
    if (energy < 0.33) return EnergyStage.BLUE;
    if (energy < 0.66) return EnergyStage.GREEN;
    if (energy < 0.75) return EnergyStage.YELLOW;
    return EnergyStage.RED;
  }

  @Override
  public float getHeatLevel() {
    return (heat - MIN_HEAT) / (MAX_HEAT - MIN_HEAT);
  }

  @Override
  protected float getPistonSpeed() {
    if (!isActive()) return 0;
    if (level != null && !level.isClientSide) return Math.max(0.08f * getHeatLevel(), 0.01f);
    return super.getPistonSpeed();
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    configureEnergy(1000, 10);
    hasSent = tag.getBoolean("woodHasSent");
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putBoolean("woodHasSent", hasSent);
  }
}