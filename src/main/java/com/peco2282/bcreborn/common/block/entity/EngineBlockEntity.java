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

  private boolean checkOrientation = false;

  private boolean isPumping = false; // Used for SMP synch
  protected boolean isActive = false;

  public EnergyStage getEnergyStage() {
    return energyStage;
  }

  protected abstract ResourceBuilder getEngineResource();

  public void setActive(boolean active) {
    if (isActive == active) return;
    isActive = active;
    if (level != null && !level.isClientSide) {
      BlockState state = level.getBlockState(worldPosition);
      if (state.hasProperty(ACTIVE)) {
        level.setBlock(worldPosition, state.setValue(ACTIVE, active), 3);
      }
    }
    if (isActive) onActivated();
    else onDeactivated();
  }

  public boolean isActive() {
    return isActive;
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

  public void onActivated() {}
  public void onDeactivated() {}

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      if (progressPart != 0) {
        progress += getPistonSpeed();

        if (progress > 1) {
          progressPart = 0;
          progress = 0;
        }
      } else if (this.isPumping) {
        progressPart = 1;
      }
      updatePistonProgress();
      return;
    }

    if (isOverheated()) {
      if (energyStorage != null) {
        energyStorage.extractEnergy(50, false);
      }
      updateHeatAndStage(false);
      return;
    }

    // BlockState の FACING から orientation を同期
    if (state.hasProperty(EngineBlock.FACING)) {
      orientation = state.getValue(EngineBlock.FACING);
    }

    // 基本ループ
    checkRedstonePower();
    engineUpdate();

    boolean burning = isBurning();
    // setActive は BlockState の更新を伴うため、頻繁に呼びすぎないよう注意が必要だが
    // ここでは元のロジックを尊重しつつ、isPumping と連動させる
    // setActive(burning); // 基底クラスでは isBurning() に基づく

    if (burning) {
      burning();
    }

    // 温度の更新と段階計算
    updateHeatAndStage(burning);

    // ピストンロジック (TileEngineBase.updateEntity より)
    if (progressPart != 0) {
      progress += getPistonSpeed();

      if (progress > 0.5 && progressPart == 1) {
        progressPart = 2;
      } else if (progress >= 1) {
        progress = 0;
        progressPart = 0;
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

    if (isRedstonePowered && isActive()) {
      pushEnergyToNeighbor();
    }
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
    setActive(active);
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
    if (level == null) return 0.0f;
    if (!level.isClientSide) {
      return Math.max(0.16f * getHeatLevel(), 0.01f);
    }

    return switch (getEnergyStage()) {
      case BLUE -> 0.02f;
      case GREEN -> 0.04f;
      case YELLOW -> 0.08f;
      case RED -> 0.16f;
      default -> 0.0f;
    };
  }

  protected void updatePistonProgress() {
    prevPistonProgress = pistonProgress;
    pistonProgress = progress;
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
    // TileEngineBase.updateHeat()
    heat = (float) ((MAX_HEAT - MIN_HEAT) * getEnergyLevel()) + MIN_HEAT;

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
    return (heat - MIN_HEAT) / (MAX_HEAT - MIN_HEAT);
  }

  public double getEnergyLevel() {
    return (double) getEnergyStored() / getMaxEnergyStored();
  }

  protected EnergyStage computeStageFromHeat(float h) {
    float energyLevel = getHeatLevel();
    if (energyLevel < 0.25f) {
      return EnergyStage.BLUE;
    } else if (energyLevel < 0.5f) {
      return EnergyStage.GREEN;
    } else if (energyLevel < 0.75f) {
      return EnergyStage.YELLOW;
    } else if (energyLevel < 1f) {
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
    orientation = Direction.values()[stream.readByte()];
    progressPart = stream.readByte();
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeByte(energyStage.ordinal() | (isPumping ? 8 : 0));
    stream.writeByte(orientation.ordinal());
    stream.writeByte(progressPart);
  }

  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side) {
    if (cap == ForgeCapabilities.ENERGY) {
      return energyCap.cast();
    }
    return super.getCapability(cap, side);
  }
}
