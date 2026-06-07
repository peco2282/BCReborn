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
package com.peco2282.bcreborn.common.block.entity;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.IEngine;
import com.peco2282.bcreborn.api.IHeatable;
import com.peco2282.bcreborn.api.IPipeConnection;
import com.peco2282.bcreborn.api.IRedstoneEngine;
import com.peco2282.bcreborn.api.energy.IEnergyHandler;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.common.energy.EngineEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class EngineBlockEntity<T extends BlockEntity>
  extends BuildCraftBlockEntity
  implements IEngine, IHeatable, IEnergyHandler, IPipeConnection {
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
  public static final float MIN_HEAT = 20;
  public static final float IDEAL_HEAT = 100;
  public static final float MAX_HEAT = 250;
  public boolean isRedstonePowered = false;
  public float progress;
  public float heat = 0;
  public EnergyStage energyStage = EnergyStage.BLUE;
  public Direction orientation = Direction.UP;
  // ピストンアニメーション用
  public float pistonProgress = 0.0f;
  public float prevPistonProgress = 0.0f;
  // Forge Energy capability
  protected EngineEnergyStorage<?> energyStorage;
  protected LazyOptional<IEnergyStorage> energyCap;

  protected int progressPart = 0;
  protected boolean isActive = false;
  private final boolean checkOrientation = false;
  private boolean isPumping = false; // Used for SMP synch

  public EngineBlockEntity(BlockEntityType<T> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
    // デフォルト（サブクラスで上書き推奨）
    this.energyStorage = new EngineEnergyStorage<>(this).setMaxEnergy(20000).setMaxExtract(80);
    this.energyCap = LazyOptional.of(() -> this.energyStorage);
  }

  protected static int getBurningTime(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return 0;
    }
    return ForgeHooks.getBurnTime(stack, null);
  }

  public Direction getOrientation() {
    orientation = getBlockState().getValue(EngineBlock.FACING);
    return orientation;
  }

  public EnergyStage getEnergyStage() {
    return energyStage;
  }

  protected abstract ResourceBuilder getEngineResource();

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    if (isActive == active) return;
    isActive = active;
    setChanged();
    if (level != null && !level.isClientSide) {
      BlockState state = level.getBlockState(worldPosition);
      if (state.hasProperty(EngineBlock.ACTIVE)) {
        level.setBlock(worldPosition, state.setValue(EngineBlock.ACTIVE, active), 3);
      }
    }
    if (isActive) onActivated();
    else onDeactivated();
  }

  public abstract boolean isFuelable(ItemStack stack);

  public abstract boolean isBurning();

  public abstract void updateProgress();

  public boolean isOverheated() {
    return energyStage.isOverheated();
  }


  public void overheat() {
    if (BCRebornCore.canEnginesExplode) {
      explode();
    } else {
      // 爆発しない設定の場合はエネルギーを消費して冷却を待つような挙動
      energyStorage.extractEnergy(50, false);
    }
  }

  public void explode() {
    if (level != null && !level.isClientSide) {
      level.explode(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, 3.0F, true, Level.ExplosionInteraction.BLOCK);
      level.removeBlock(worldPosition, false);
    }
  }

  public abstract void burning();

  public void onActivated() {
  }

  public void onDeactivated() {
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      if (progressPart != 0) {
        progress += getPistonSpeed();

        if (progress >= 1) {
          progress -= 1;
          prevPistonProgress -= 1; // 補間の連続性を維持するために prev も調整
          if (!isActive() && !isPumping) {
            progress = 0;
            progressPart = 0;
            prevPistonProgress = 0;
            pistonProgress = 0;
          }
        }
      } else if (this.isPumping || (isRedstonePowered && isActive())) {
        progressPart = 1;
      }
      updatePistonProgress();
      return;
    }

    // サーバーサイドのみ: BlockState の FACING から orientation を同期
    if (state.hasProperty(EngineBlock.FACING)) {
      orientation = state.getValue(EngineBlock.FACING);
    }

    if (isOverheated()) {
      if (energyStorage != null) {
        energyStorage.extractEnergy(50, false);
      }
      updateHeatAndStage(false);
      return;
    }

    // 基本ループ
    checkRedstonePower();
    engineUpdate();

    boolean burning = isBurning();
    // サーバーサイドでの isActive 同期
    if (burning != isActive) {
      setActive(burning);
    }

    if (burning) {
      burning();
    }

    // 温度の更新と段階計算 (木エンジンなどは独自の updateHeatAndStage を持つ場合があるが、基本はこれ)
    if (!(this instanceof IRedstoneEngine)) {
      updateHeatAndStage(burning);
    }

    // ピストンロジック (TileEngineBase.updateEntity より)
    if (progressPart != 0) {
      progress += getPistonSpeed();

      if (progress >= 1) {
        progress -= 1;
        prevPistonProgress -= 1; // 補間の連続性を維持するために prev も調整
        onPistonCycled(); // サイクル完了時の処理
        if (!isRedstonePowered || !isActive()) {
          progress = 0;
          progressPart = 0;
          prevPistonProgress = 0;
          pistonProgress = 0;
        }
      }
    } else if (isRedstonePowered && isActive()) {
      // 実際には出力先があるかどうかのチェックが必要
      if (canPushEnergy()) {
        progressPart = 1;
        setPumping(true);
      } else {
        setPumping(false);
      }
    } else {
      setPumping(false);
    }

    updateProgress();

    // ピストンアニメーション更新
    updatePistonProgress();

    if (this instanceof IRedstoneEngine) {
      if (isRedstonePowered && isActive()) {
        pushEnergyToNeighbor();
      }
    }
  }

  protected void onPistonCycled() {
    pushEnergyToNeighbor();
  }

  protected boolean canPushEnergy() {
    BlockPos outPos = getBlockPos().relative(orientation);
    BlockEntity be = level.getBlockEntity(outPos);
    if (be == null) return false;
    LazyOptional<IEnergyStorage> cap = be.getCapability(ForgeCapabilities.ENERGY, orientation.getOpposite());
    return cap.isPresent();
  }

  protected final void setPumping(boolean active) {
    if (this.isPumping == active) return;
    this.isPumping = active;
    setChanged();
  }

  protected void engineUpdate() {
    // デフォルトのエネルギー減少処理
    if (!isRedstonePowered) {
      if (energyStorage.getEnergyStored() > 0) {
        energyStorage.extractEnergy(10, false);
      }
    }
  }

  protected float getPistonSpeed() {
    if (!isActive()) {
      return 0;
    }
    return switch (energyStage) {
      case BLUE -> 0.01f;
      case GREEN -> 0.02f;
      case YELLOW -> 0.04f;
      case RED -> 0.08f;
      default -> 0.01f;
    };
  }

  protected void updatePistonProgress() {
    prevPistonProgress = pistonProgress;
    pistonProgress = progress;
  }

  public float getPistonProgress(float partialTick) {
    if (progressPart == 0) {
      return 0;
    }
    float interpolated = Mth.lerp(partialTick, prevPistonProgress, pistonProgress);
    // 0.0 -> 0.5 -> 1.0 -> 0.5 -> 0.0 の周期にするために 2倍して 1.0 で折り返す
    // progress が 0.0 ~ 1.0 の範囲で動くとき、以下の式で 0.0 ~ 1.0 ~ 0.0 になる
    if (interpolated > 0.5f) {
      return (1.0f - interpolated) * 2f;
    } else {
      return interpolated * 2f;
    }
  }

  public void checkRedstonePower() {
    boolean powered = level.hasNeighborSignal(getBlockPos());
    if (isRedstonePowered != powered) {
      isRedstonePowered = powered;
      setChanged();
    }
  }

  protected void updateHeatAndStage(boolean burning) {
    if (burning) {
      heat += 0.2f;
    } else {
      heat -= 0.1f;
    }

    if (heat < 0) heat = 0;
    if (heat > 2000) heat = 2000;

    // 段階更新
    EnergyStage newStage = computeStageFromHeat(heat);
    if (newStage != energyStage) {
      energyStage = newStage;
      if (energyStage == EnergyStage.OVERHEAT) {
        overheat();
      }
      setChanged();
    }
  }

  public float getHeatLevel() {
    return heat / 1000f;
  }

  public double getEnergyLevel() {
    return (double) getEnergyStored() / (double) getMaxEnergyStored();
  }

  protected EnergyStage computeStageFromHeat(float h) {
    if (h < 250) {
      return EnergyStage.BLUE;
    } else if (h < 500) {
      return EnergyStage.GREEN;
    } else if (h < 750) {
      return EnergyStage.YELLOW;
    } else if (h < 1000) {
      return EnergyStage.RED;
    } else {
      return EnergyStage.OVERHEAT;
    }
  }

  protected float getOutputMultiplier() {
    return switch (energyStage) {
      case BLUE -> 0.6f;
      case GREEN -> 1.0f;
      case YELLOW -> 1.25f;
      case RED -> 1.5f;
      default -> 0.0f; // OVERHEAT: 出力不可
    };
  }

  public int getEnergyStored() {
    return energyStorage != null ? energyStorage.getEnergyStored() : 0;
  }

  // GUI 用: 最大エネルギー容量の公開
  public int getMaxEnergyStored() {
    return energyStorage != null ? energyStorage.getMaxEnergyStored() : 0;
  }

  protected void pushEnergyToNeighbor() {
    if (level == null || level.isClientSide) return;
    if (energyStorage == null) return;

    BlockPos outPos = getBlockPos().relative(orientation);
    BlockEntity be = level.getBlockEntity(outPos);
    if (be == null) return;

    be.getCapability(ForgeCapabilities.ENERGY, orientation.getOpposite()).ifPresent(target -> {
      int canExtract = energyStorage.getMaxExtract();
      if (canExtract <= 0) return;
      int available = energyStorage.getEnergyStored();
      if (available <= 0) return;
      int toSend = Math.min(canExtract, available);
      int accepted = target.receiveEnergy(toSend, false);
      if (accepted > 0) {
        energyStorage.extractEnergy(accepted, false);
      }
    });
  }

  // サブクラス用：容量と最大出力量を調整
  protected void configureEnergy(int maxEnergy, int maxExtract) {
    if (this.energyStorage != null) {
      this.energyStorage.setMaxEnergy(maxEnergy).setMaxExtract(maxExtract);
    }
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);
    if (data.contains("isActive")) {
      isActive = data.getBoolean("isActive");
    }
    if (data.contains("isPumping")) {
      isPumping = data.getBoolean("isPumping");
    }

    orientation = Direction.values()[data.getByte("orientation")];
    progress = data.getFloat("progress");
    progressPart = data.getInt("progressPart");
    heat = data.getFloat("heat");
    if (data.contains("engineEnergy")) {
      energyStorage.read(data.getCompound("engineEnergy"));
    }
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    data.putBoolean("isActive", isActive);
    data.putBoolean("isPumping", isPumping);

    data.putByte("orientation", (byte) orientation.ordinal());
    data.putFloat("progress", progress);
    data.putInt("progressPart", progressPart);
    data.putFloat("heat", heat);
    if (energyStorage != null) {
      CompoundTag tag = new CompoundTag();
      energyStorage.write(tag);
      data.put("engineEnergy", tag);
    }
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    int flags = stream.readUnsignedByte();
    energyStage = EnergyStage.values()[flags & 0x07];
    isPumping = (flags & 0x08) != 0;
    isRedstonePowered = (flags & 0x10) != 0;
    isActive = (flags & 0x20) != 0;

    orientation = Direction.values()[stream.readByte()];
    progressPart = stream.readByte();
    progress = stream.readFloat();
    updatePistonProgress();
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    int flags = energyStage.ordinal();
    if (isPumping) flags |= 0x08;
    if (isRedstonePowered) flags |= 0x10;
    if (isActive) flags |= 0x20;
    stream.writeByte(flags);

    stream.writeByte(orientation.ordinal());
    stream.writeByte(progressPart);
    stream.writeFloat(progress);
  }

  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side) {
    if (cap == ForgeCapabilities.ENERGY) {
      return energyCap.cast();
    }
    return super.getCapability(cap, side);
  }

  public enum EnergyStage {
    BLUE, GREEN, YELLOW, RED, OVERHEAT;
    public static final EnergyStage[] VALUES = values();

    public boolean isOverheated() {
      return this == OVERHEAT;
    }
  }
}
