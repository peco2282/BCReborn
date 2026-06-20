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
package com.peco2282.bcreborn.transport.pipe.transport;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Arrays;

/**
 * オリジナル BuildCraft 1.7.10 の PipeTransportPower に沿ったエネルギー輸送モジュール。
 * <p>
 * 設計方針:
 * - 各方向に internalPower（現tick）と internalNextPower（次tick）のダブルバッファを持つ（double精度）。
 * - powerQuery（現tick需要）と nextPowerQuery（次tick需要）で需要を1tick遅延集計する。
 * - tick ごとに internalPower を比例配分して隣接パイプ/機械へ送電する。
 * - powerResistance による損失計算を receiveEnergy 時に適用する。
 * - displayPower は移動平均（10tick）でクライアント表示用に保持する。
 * - maxPower を超えた場合はオーバーロードとしてカウントする。
 * - 全エネルギー転送はこのモジュールが一元管理する（Behaviour の transferEnergy() は使用しない）。
 */
public class EnergyTransportModule {

  private static final int OVERLOAD_TICKS = 60;
  private static final int AVERAGE_WINDOW = 10;

  private final PipeBlockEntity pipe;
  private final int[][] powerHistory = new int[6][AVERAGE_WINDOW];
  // 抵抗値（損失率）— PipeMaterial から初期化
  private final float powerResistance;
  public int[] nextPowerQuery = new int[6];
  // クライアント表示用移動平均（10tick窓）
  public short[] displayPower = new short[6];
  // ダブルバッファ: 現tick / 次tick（double精度でoriginalに合わせる）
  private double[] internalPower = new double[6];
  private double[] internalNextPower = new double[6];
  // 需要バッファ: 現tick / 次tick
  private int[] powerQuery = new int[6];
  private int historyIndex = 0;
  // オーバーロードカウンタ
  private int overload = 0;
  // 最大容量 (RF/tick) — PipeMaterial から初期化
  private int maxPower;
  // 最後にステップした worldTime（同一tick内の二重ステップ防止）
  private long currentDate = Long.MIN_VALUE;

  public EnergyTransportModule(PipeBlockEntity pipe) {
    this.pipe = pipe;
    PipeMaterial mat = pipe.getPipeMaterial();
    this.maxPower = mat.getEnergyTransferRate();
    this.powerResistance = mat.getPowerResistance();
  }

  // ---- 公開API ----

  /**
   * 毎tick呼び出されるメインロジック。
   * PipeBlockEntity.tick() から呼ぶこと。
   * Behaviour の transferEnergy() は呼ばないこと（責務重複を避けるため）。
   */
  public void tick(Level level, BlockPos pos) {
    if (level.isClientSide) return;

    step(level);

    // 1. internalPower を隣接へ比例配分して送電
    for (int i = 0; i < 6; i++) {
      if (internalPower[i] <= 0) continue;

      int totalQuery = 0;
      for (int j = 0; j < 6; j++) {
        if (j != i && powerQuery[j] > 0) {
          totalQuery += powerQuery[j];
        }
      }
      if (totalQuery <= 0) continue;

      int unusedQuery = totalQuery;
      for (int j = 0; j < 6; j++) {
        if (j == i || powerQuery[j] <= 0) continue;

        Direction outDir = Direction.from3DDataValue(j);
        BlockPos neighborPos = pos.relative(outDir);
        if (!level.isLoaded(neighborPos)) continue;

        BlockEntity be = level.getBlockEntity(neighborPos);
        if (be == null) continue;

        // 比例配分量を計算（double精度）
        double share = Math.min(
          internalPower[i] * powerQuery[j] / (double) unusedQuery,
          internalPower[i]
        );
        unusedQuery -= powerQuery[j];

        if (be instanceof PipeBlockEntity neighborPipe
          && neighborPipe.getTransportType() == PipeType.ENERGY) {
          // 隣接エネルギーパイプへ転送
          EnergyTransportModule neighborModule = neighborPipe.getEnergyTransportModule();
          if (neighborModule != null) {
            double accepted = neighborModule.receiveEnergy(outDir.getOpposite(), share);
            internalPower[i] -= accepted;
          }
        } else {
          // 一般機械（IEnergyStorage）へ転送
          Direction incomingFace = outDir.getOpposite();
          //noinspection DataFlowIssue
          IEnergyStorage handler = be.getCapability(ForgeCapabilities.ENERGY, incomingFace).orElse(null);
          //noinspection ConstantValue
          if (handler != null && handler.canReceive()) {
            int iShare = (int) share;
            int accepted = handler.receiveEnergy(iShare, false);
            internalPower[i] -= accepted;
          }
        }
      }
    }

    // 2. displayPower 更新（移動平均）
    for (int i = 0; i < 6; i++) {
      powerHistory[i][historyIndex] = (int) Math.ceil(internalPower[i]);
    }
    historyIndex = (historyIndex + 1) % AVERAGE_WINDOW;

    short highestPower = 0;
    for (int i = 0; i < 6; i++) {
      int sum = 0;
      for (int k = 0; k < AVERAGE_WINDOW; k++) {
        sum += powerHistory[i][k];
      }
      displayPower[i] = (short) Math.round(sum / (double) AVERAGE_WINDOW);
      if (displayPower[i] > highestPower) highestPower = displayPower[i];
    }

    // 3. オーバーロード判定
    if (highestPower > maxPower * 0.95f) {
      overload = Math.min(overload + 1, OVERLOAD_TICKS);
    } else {
      overload = Math.max(overload - 1, 0);
    }

    // オーバーロード時に爆発（originalと同様）
    if (overload >= OVERLOAD_TICKS) {
      overload = 0;
      level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
        1.0f, Level.ExplosionInteraction.BLOCK);
    }

    // 4. 隣接機械から需要を収集（次tick用）
    for (Direction dir : Direction.values()) {
      BlockPos neighborPos = pos.relative(dir);
      if (!level.isLoaded(neighborPos)) continue;

      BlockEntity be = level.getBlockEntity(neighborPos);
      if (be == null) continue;

      // 隣接エネルギーパイプは需要収集対象外（パイプ間は requestEnergy で伝播）
      if (be instanceof PipeBlockEntity neighborPipe
        && neighborPipe.getTransportType() == PipeType.ENERGY) {
        continue;
      }

      Direction incomingFace = dir.getOpposite();
      //noinspection DataFlowIssue
      IEnergyStorage handler = be.getCapability(ForgeCapabilities.ENERGY, incomingFace).orElse(null);
      //noinspection ConstantValue
      if (handler != null && handler.canReceive()) {
        int request = handler.receiveEnergy(maxPower, true); // simulate
        if (request > 0) {
          requestEnergy(dir, request);
        }
      }
    }

    // 5. 需要を隣接エネルギーパイプへ伝播（入力側を探す）
    int[] transferQuery = new int[6];
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 6; j++) {
        if (j != i) {
          transferQuery[i] += powerQuery[j];
        }
      }
      transferQuery[i] = Math.min(transferQuery[i], maxPower);
    }

    for (int i = 0; i < 6; i++) {
      if (transferQuery[i] == 0) continue;
      Direction dir = Direction.from3DDataValue(i);
      BlockPos neighborPos = pos.relative(dir);
      if (!level.isLoaded(neighborPos)) continue;

      BlockEntity be = level.getBlockEntity(neighborPos);
      if (be instanceof PipeBlockEntity neighborPipe
        && neighborPipe.getTransportType() == PipeType.ENERGY) {
        EnergyTransportModule neighborModule = neighborPipe.getEnergyTransportModule();
        if (neighborModule != null) {
          neighborModule.requestEnergy(dir.getOpposite(), transferQuery[i]);
        }
      }
    }

    pipe.setChanged();
  }

  /**
   * 外部（エンジン等）またはパイプからエネルギーを受け取る。
   * powerResistance による損失を適用してから internalNextPower に積む。
   *
   * @param from   エネルギーが入ってきた方向
   * @param amount 受け取るRF量（double精度）
   * @return 実際に受け取ったRF量（損失後）
   */
  public double receiveEnergy(Direction from, double amount) {
    int side = from.get3DDataValue();

    step(pipe.getLevel());

    if (internalNextPower[side] >= maxPower) {
      return 0;
    }

    // 抵抗による損失を適用
    double effective = amount * (1.0 - powerResistance);

    double space = maxPower - internalNextPower[side];
    double accepted = Math.min(effective, space);

    // 実際に受け取れる量（損失前）を逆算して返す
    double actualInput = Math.min(amount, space / (1.0 - powerResistance + 1e-9));
    if (powerResistance <= 0) {
      actualInput = accepted;
    }

    internalNextPower[side] += accepted;

    if (internalNextPower[side] > maxPower) {
      internalNextPower[side] = maxPower;
    }

    return actualInput;
  }

  /**
   * int版 receiveEnergy（外部capability経由用）。
   */
  public int receiveEnergy(Direction from, int amount) {
    return (int) receiveEnergy(from, (double) amount);
  }

  /**
   * 需要を登録する（次tick用）。
   * 隣接パイプから伝播されてくる。
   *
   * @param from   需要が来た方向
   * @param amount 要求RF量
   */
  public void requestEnergy(Direction from, int amount) {
    nextPowerQuery[from.get3DDataValue()] += amount;
  }

  public boolean isOverloaded() {
    return overload >= OVERLOAD_TICKS;
  }

  public int getOverload() {
    return overload;
  }

  public double[] getInternalPower() {
    return Arrays.copyOf(internalPower, 6);
  }

  public int getMaxPower() {
    return maxPower;
  }

  /**
   * 最大転送量を動的に変更する（鉄パイプのレンチ操作用）。
   */
  public void setMaxPower(int maxPower) {
    this.maxPower = maxPower;
  }

  public float getPowerResistance() {
    return powerResistance;
  }

  // ---- ダブルバッファのステップ ----

  /**
   * 同一worldTime内では一度だけステップする。
   * internalPower ← internalNextPower、powerQuery ← nextPowerQuery を入れ替える。
   */
  private void step(Level level) {
    if (level == null) return;
    long worldTime = level.getGameTime();
    if (currentDate == worldTime) return;
    currentDate = worldTime;

    // バッファスワップ
    double[] tmp = internalPower;
    internalPower = internalNextPower;
    internalNextPower = tmp;
    Arrays.fill(internalNextPower, 0);

    powerQuery = nextPowerQuery;
    nextPowerQuery = new int[6];
  }

  // ---- NBT ----

  public void save(CompoundTag tag) {
    CompoundTag energyTag = new CompoundTag();
    for (int i = 0; i < 6; i++) {
      energyTag.putDouble("internalPower" + i, internalPower[i]);
      energyTag.putDouble("internalNextPower" + i, internalNextPower[i]);
      energyTag.putInt("powerQuery" + i, powerQuery[i]);
      energyTag.putInt("nextPowerQuery" + i, nextPowerQuery[i]);
    }
    energyTag.putInt("overload", overload);
    tag.put("EnergyTransport", energyTag);
  }

  public void load(CompoundTag tag) {
    if (!tag.contains("EnergyTransport")) return;
    CompoundTag energyTag = tag.getCompound("EnergyTransport");
    for (int i = 0; i < 6; i++) {
      // 後方互換: 旧int形式も読み込める
      if (energyTag.contains("internalPower" + i, 6 /* DOUBLE */)) {
        internalPower[i] = energyTag.getDouble("internalPower" + i);
        internalNextPower[i] = energyTag.getDouble("internalNextPower" + i);
      } else {
        internalPower[i] = energyTag.getInt("internalPower" + i);
        internalNextPower[i] = energyTag.getInt("internalNextPower" + i);
      }
      powerQuery[i] = energyTag.getInt("powerQuery" + i);
      nextPowerQuery[i] = energyTag.getInt("nextPowerQuery" + i);
    }
    overload = energyTag.getInt("overload");
  }
}
