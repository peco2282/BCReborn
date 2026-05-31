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
package com.peco2282.bcreborn.common.utils;

public class AverageDouble {
  private final double[] data;
  private int pos;
  private final int precise;
  private double averageRaw, tickValue;

  public AverageDouble(int precise) {
    this.precise = precise;
    this.data = new double[precise];
    this.pos = 0;
  }

  public double getAverage() {
    return averageRaw / precise;
  }

  public void tick(double value) {
    internalTick(tickValue + value);
    tickValue = 0;
  }

  public void tick() {
    internalTick(tickValue);
    tickValue = 0;
  }

  private void internalTick(double value) {
    pos = ++pos % precise;
    double oldValue = data[pos];
    data[pos] = value;
    if (pos == 0) {
      averageRaw = 0;
      for (double iValue : data) {
        averageRaw += iValue;
      }
    } else {
      averageRaw = averageRaw - oldValue + value;
    }
  }

  public void push(double value) {
    tickValue += value;
  }
}
