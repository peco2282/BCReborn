package com.peco2282.bcreborn.transport.pipe.transport;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.behaviour.FluidPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * 流体パイプの輸送ロジックを担うモジュール。
 * <p>
 * originalの PipeTransportFluids を参考に、TTL/TransferState による逆流防止と
 * 異種流体混入防止を実装する。BCReborn のシンプル設計に合わせ、7セクション方式は
 * 採用せず単一 FluidTank を使用するが、方向ごとの TransferState で逆流を防ぐ。
 */
public class FluidTransportModule {

  // 入力状態の持続時間 (tick)
  public static final short INPUT_TTL = 60;
  // 出力状態の持続時間 (tick)
  public static final short OUTPUT_TTL = 80;
  // 出力失敗後のクールダウン (tick)
  public static final short OUTPUT_COOLDOWN = 30;

  /**
   * 各方向の転送状態。
   * Input: この方向から流体が流入中（逆流防止のため出力禁止）
   * Output: この方向へ流体を出力中
   * None: 未使用
   */
  public enum TransferState {
    None, Input, Output
  }

  private final PipeBlockEntity pipe;

  private final TransferState[] transferState = new TransferState[6];
  private final short[] inputTTL = new short[6];
  private final short[] outputTTL = new short[6];
  private final short[] outputCooldown = new short[6];

  public FluidTransportModule(PipeBlockEntity pipe) {
    this.pipe = pipe;
    for (int i = 0; i < 6; i++) {
      transferState[i] = TransferState.None;
      inputTTL[i] = 0;
      outputTTL[i] = OUTPUT_TTL;
      outputCooldown[i] = 0;
    }
  }

  /**
   * 毎tick呼び出される流体輸送処理。
   * TTL管理と逆流防止を行いながら接続先へ流体を転送する。
   */
  public void tick(Level level, BlockPos pos) {
    if (level.isClientSide) return;

    FluidTank tank = pipe.getFluidTank();
    if (tank == null) return;

    // TTL を更新し、出力可能な方向を決定する
    int outputCount = computeTransferStates(level, pos);

    FluidStack stored = tank.getFluid();
    if (stored.isEmpty()) {
      // タンクが空になったら全状態をリセット
      resetAllStates();
      return;
    }

    if (outputCount == 0) return;

    PipeBehaviour behaviour = pipe.getBehaviour();

    // 出力方向へ均等分配
    Direction[] directions = Direction.values();
    int amountPerOutput = Math.max(1, Math.min(pipe.getPipeMaterial().getFluidTransferRate(), stored.getAmount()) / outputCount);

    for (int i = 0; i < directions.length; i++) {
      if (transferState[i] != TransferState.Output) continue;

      Direction dir = directions[i];

      // FluidPipeBehaviour による出力方向フィルタリング
      if (behaviour instanceof FluidPipeBehaviour fb) {
        if (!fb.canOutputFluid(pipe, dir)) continue;
      }

      if (!isConnected(pos, dir)) continue;

      BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
      if (neighbor == null) continue;

      final int idx = i;
      final int amount = amountPerOutput;
      neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
        FluidStack toSend = new FluidStack(stored, amount);
        int canFill = handler.fill(toSend, IFluidHandler.FluidAction.SIMULATE);
        if (canFill <= 0) {
          // 受け入れ不可 → outputTTL を減らす
          outputTTL[idx]--;
        } else {
          int filled = handler.fill(new FluidStack(stored, canFill), IFluidHandler.FluidAction.EXECUTE);
          if (filled > 0) {
            tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            pipe.setChanged();
            level.sendBlockUpdated(pos, pipe.getBlockState(), pipe.getBlockState(), 3);
          }
        }
      });

      if (tank.getFluid().isEmpty()) break;
    }
  }

  /**
   * 各方向の TransferState と TTL を更新し、出力可能な方向数を返す。
   */
  private int computeTransferStates(Level level, BlockPos pos) {
    int outputCount = 0;
    Direction[] directions = Direction.values();

    for (int i = 0; i < directions.length; i++) {
      Direction dir = directions[i];

      // Input 状態の TTL 管理
      if (transferState[i] == TransferState.Input) {
        if (inputTTL[i] > 0) {
          inputTTL[i]--;
        } else {
          transferState[i] = TransferState.None;
        }
        continue;
      }

      // 接続されていない方向はスキップ
      if (!isConnected(pos, dir)) {
        transferState[i] = TransferState.None;
        continue;
      }

      // クールダウン中はスキップ
      if (outputCooldown[i] > 0) {
        outputCooldown[i]--;
        continue;
      }

      // outputTTL 切れ → クールダウンへ
      if (outputTTL[i] <= 0) {
        transferState[i] = TransferState.None;
        outputCooldown[i] = OUTPUT_COOLDOWN;
        outputTTL[i] = OUTPUT_TTL;
        continue;
      }

      // 受け入れ可能か確認
      BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
      if (neighbor == null) continue;

      boolean canReceive = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).isPresent();
      if (canReceive) {
        transferState[i] = TransferState.Output;
        outputCount++;
      }
    }

    return outputCount;
  }

  /**
   * 外部から流体が流入した際に呼び出す（fill() 時）。
   * 入力方向を Input 状態にして逆流を防ぐ。
   */
  public void onFluidFilled(Direction from) {
    if (from == null) return;
    int i = from.ordinal();
    transferState[i] = TransferState.Input;
    inputTTL[i] = INPUT_TTL;
  }

  /**
   * 指定方向が接続されているか確認する。
   */
  private boolean isConnected(BlockPos pos, Direction dir) {
    var blockState = pipe.getBlockState();
    var prop = PipeBlock.PROPERTY_MAP.get(dir);
    if (prop == null) return false;
    return blockState.getValue(prop);
  }

  /**
   * タンクが空になった際に全状態をリセットする。
   */
  private void resetAllStates() {
    for (int i = 0; i < 6; i++) {
      transferState[i] = TransferState.None;
    }
  }

  public TransferState getTransferState(Direction dir) {
    return transferState[dir.ordinal()];
  }

  // ---- NBT ----

  public void save(CompoundTag tag) {
    CompoundTag fluidModuleTag = new CompoundTag();
    for (int i = 0; i < 6; i++) {
      fluidModuleTag.putInt("transferState[" + i + "]", transferState[i].ordinal());
      fluidModuleTag.putShort("inputTTL[" + i + "]", inputTTL[i]);
      fluidModuleTag.putShort("outputTTL[" + i + "]", outputTTL[i]);
      fluidModuleTag.putShort("outputCooldown[" + i + "]", outputCooldown[i]);
    }
    tag.put("FluidModule", fluidModuleTag);
  }

  public void load(CompoundTag tag) {
    if (!tag.contains("FluidModule")) return;
    CompoundTag fluidModuleTag = tag.getCompound("FluidModule");
    for (int i = 0; i < 6; i++) {
      if (fluidModuleTag.contains("transferState[" + i + "]")) {
        int ordinal = fluidModuleTag.getInt("transferState[" + i + "]");
        transferState[i] = TransferState.values()[ordinal];
      }
      if (fluidModuleTag.contains("inputTTL[" + i + "]")) {
        inputTTL[i] = fluidModuleTag.getShort("inputTTL[" + i + "]");
      }
      if (fluidModuleTag.contains("outputTTL[" + i + "]")) {
        outputTTL[i] = fluidModuleTag.getShort("outputTTL[" + i + "]");
      }
      if (fluidModuleTag.contains("outputCooldown[" + i + "]")) {
        outputCooldown[i] = fluidModuleTag.getShort("outputCooldown[" + i + "]");
      }
    }
  }
}
