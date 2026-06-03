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
package com.peco2282.bcreborn.transport.pipe;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PipeMaterial implements StringRepresentable {
  WOOD("wood", 0.075f, 10, 320, 0.0000f),
  COBBLESTONE("cobblestone", 0.075f, 10, 80, 0.0500f),
  STONE("stone", 0.075f, 20, 160, 0.0250f),
  GOLD("gold", 0.3f, 80, 2560, 0.003125f),
  IRON("iron", 0.075f, 40, 1280, 0.0125f),
  DIAMOND("diamond", 0.075f, 80, 10240, 0.0000f),
  EMERALD("emerald", 0.075f, 40, -1, 0.0000f),
  OBSIDIAN("obsidian", 0.075f, -1, -1, 0.0000f),
  QUARTZ("quartz", 0.075f, 40, 640, 0.0125f),
  VOID("void", 0.075f, 10, -1, 0.0000f),
  CLAY("clay", 0.075f, 40, -1, 0.0000f),
  SANDSTONE("sandstone", 0.075f, 20, -1, 0.0125f),
  LAPIS("lapis", 0.075f, -1, -1, 0.0000f),
  DAIZULI("daizuli", 0.075f, -1, -1, 0.0000f),
  EMZULI("emzuli", 0.075f, -1, -1, 0.0000f),
  STRIPES("stripes", 0.075f, -1, -1, 0.0000f);

  private static final int UNDEFINE_PIPE = -1;
  private final String name;
  private final float itemSpeed;
  /**
   * 液体パイプの1tick当たり最大転送量 (mB/tick)
   */
  private final int fluidTransferRate;
  /**
   * エネルギーパイプの1tick当たり最大転送量 (RF/tick)
   */
  private final int energyTransferRate;
  /**
   * エネルギーパイプの抵抗値（損失率）。0.0 = 損失なし、0.05 = 5%損失
   */
  private final float powerResistance;

  PipeMaterial(String name, float itemSpeed, int fluidTransferRate, int energyTransferRate, float powerResistance) {
    this.name = name;
    this.itemSpeed = itemSpeed;
    this.fluidTransferRate = fluidTransferRate;
    this.energyTransferRate = energyTransferRate;
    this.powerResistance = powerResistance;
  }

  public float getItemSpeed() {
    return itemSpeed;
  }

  public int getFluidTransferRate() {
    if (fluidTransferRate == UNDEFINE_PIPE) {
      throw new IllegalStateException("Fluid Pipe is not exist. " + this);
    }
    return fluidTransferRate;
  }

  public float getPowerResistance() {
    return powerResistance;
  }

  public int getEnergyTransferRate() {
    if (energyTransferRate == UNDEFINE_PIPE) {
      throw new IllegalStateException("Energy Pipe is not exist. " + this);
    }
    return energyTransferRate;
  }

  public boolean unsupports(PipeType type) {
    return !switch (type) {
      case ITEM -> true;
      case FLUID -> fluidTransferRate != UNDEFINE_PIPE;
      case ENERGY -> energyTransferRate != UNDEFINE_PIPE;
    };
  }

  @Override
  public @NotNull String getSerializedName() {
    return name;
  }
}
