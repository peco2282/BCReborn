package com.peco2282.bcreborn.common.block.entity;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.api.IEngine;
import com.peco2282.bcreborn.api.IHeatable;
import com.peco2282.bcreborn.api.IPipeConnection;
import com.peco2282.bcreborn.api.energy.IEnergyHandler;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.ResourceUtils;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import com.peco2282.bcreborn.common.energy.EngineEnergyStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class EngineBlockEntity<T extends BlockEntity>
    extends BuildCraftBlockEntity
    implements IEngine, IHeatable, IEnergyHandler, IPipeConnection {
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

  public EngineBlockEntity(BlockEntityType<T> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
    // デフォルト（サブクラスで上書き推奨）
    this.energyStorage = new EngineEnergyStorage<>(this).setMaxEnergy(20000).setMaxExtract(80);
    this.energyCap = LazyOptional.of(() -> this.energyStorage);
  }

  public enum EnergyStage {
    BLUE, GREEN, YELLOW, RED, OVERHEAT;
    public static final EnergyStage[] VALUES = values();

    public boolean isOverheated() {
      return this == OVERHEAT;
    }
  }

  public static final float MIN_HEAT = 20;
  public static final float IDEAL_HEAT = 100;
  public static final float MAX_HEAT = 250;
  public boolean isRedstonePowered = false;
  public float progress;
  public int energy;
  public float heat = MIN_HEAT;
  public EnergyStage energyStage = EnergyStage.BLUE;
  public Direction orientation = Direction.UP;

  // Forge Energy capability
  protected EngineEnergyStorage<?> energyStorage;
  protected LazyOptional<IEnergyStorage> energyCap;

  protected int progressPart = 0;

  // ピストンアニメーション用
  public float pistonProgress = 0.0f;
  public float prevPistonProgress = 0.0f;
  private float pistonDirection = 1.0f;

  private boolean checkOrientation = false;

  private boolean isPumping = false; // Used for SMP synch
  protected boolean isActive = false;

  public EnergyStage getEnergyStage() {
    return energyStage;
  }

  protected abstract ResourceBuilder getEngineResource();

  public void setActive(boolean active) {
    isActive = active;
    if (isActive) onActivated();
    else onDeactivated();
  }

  public boolean isActive() {
    return isActive;
  }

  public abstract boolean isFuelable(ItemStack stack);
  public abstract boolean isBurning();

  public abstract void updateProgress();

  public abstract void overheat();
  public boolean isOverheated() {
    return energyStage.isOverheated();
  }

  public abstract void explode();
  public abstract void burning();

  public void onActivated() {}
  public void onDeactivated() {}

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (isOverheated()) {
      explode();
    }

    // BlockState の FACING から orientation を同期
    if (state.hasProperty(EngineBlock.FACING)) {
      orientation = state.getValue(EngineBlock.FACING);
    }

    // 基本ループ
    checkRedstonePower();
    boolean burning = isBurning();
    setActive(burning);
    if (burning) burinig();
    // 温度の更新と段階計算
    updateHeatAndStage(burning);
    updateProgress();

    // ピストンアニメーション更新
    updatePistonProgress();

    // 隣接へ送電（orientation 方向）
    pushEnergyToNeighbor();
  }

  protected float getPistonSpeed() {
    return switch (energyStage) {
      case BLUE -> 0.01f;
      case GREEN -> 0.02f;
      case YELLOW -> 0.04f;
      case RED -> 0.05f;
      default -> 0.0f; // OVERHEAT: 停止
    };
  }

  protected void updatePistonProgress() {
    prevPistonProgress = pistonProgress;
    float speed = getPistonSpeed();
    if (isActive && speed > 0.0f) {
      pistonProgress += pistonDirection * speed;
      if (pistonProgress >= 1.0f) {
        pistonProgress = 1.0f;
        pistonDirection = -1.0f;
      } else if (pistonProgress <= 0.0f) {
        pistonProgress = 0.0f;
        pistonDirection = 1.0f;
      }
    } else {
      // 非稼働時はゆっくり戻る
      if (pistonProgress > 0.0f) {
        pistonProgress = Math.max(0.0f, pistonProgress - 0.05f);
      }
    }
  }

  public float getPistonProgress(float partialTick) {
    return Mth.lerp(partialTick, prevPistonProgress, pistonProgress);
  }

  protected static int getBurningTime(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return 0;
    }
    return ForgeHooks.getBurnTime(stack, null);
  }

  public void checkRedstonePower() {
    isRedstonePowered = level.hasNeighborSignal(getBlockPos());
  }

  protected void updateHeatAndStage(boolean burning) {
    // 発熱・冷却（暫定値）
    float heatDelta = 0;
    if (burning) {
      // 稼働時は加熱（理想温度までは速く、その先はゆっくり）
      heatDelta = heat < IDEAL_HEAT ? 0.6f : 0.25f;
    } else {
      // 非稼働時は自然冷却
      heatDelta = -0.4f;
    }
    heat = Math.max(0, heat + heatDelta);

    // 段階更新
    EnergyStage newStage = computeStageFromHeat(heat);
    if (newStage != energyStage) {
      energyStage = newStage;
      setChanged();
    }

    // 過熱チェック
    if (heat > MAX_HEAT) {
      energyStage = EnergyStage.OVERHEAT;
    }
  }

  protected EnergyStage computeStageFromHeat(float h) {
    if (h > MAX_HEAT) return EnergyStage.OVERHEAT;
    if (h >= 180) return EnergyStage.RED;
    if (h >= 120) return EnergyStage.YELLOW;
    if (h >= 60) return EnergyStage.GREEN;
    return EnergyStage.BLUE;
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

    orientation = Direction.values()[data.getByte("orientation")];
    progress = data.getFloat("progress");
    energy = data.getInt("energy");
    heat = data.getFloat("heat");
    if (data.contains("engineEnergy")) {
      energyStorage.read(data.getCompound("engineEnergy"));
    }
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);

    data.putByte("orientation", (byte) orientation.ordinal());
    data.putFloat("progress", progress);
    data.putInt("energy", energy);
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
    orientation = Direction.values()[stream.readByte()];
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeByte(energyStage.ordinal() | (isPumping ? 8 : 0));
    stream.writeByte(orientation.ordinal());
  }

  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side) {
    if (cap == ForgeCapabilities.ENERGY) {
      return energyCap.cast();
    }
    return super.getCapability(cap, side);
  }
}
