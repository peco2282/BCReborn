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

public class AverageInt {
  private int[] data;
  private int pos;
  private final int precise;
  private int averageRaw, tickValue;

  public AverageInt(int precise) {
    this.precise = precise;
    clear();
  }

  public void clear() {
    this.data = new int[precise];
    this.pos = 0;
  }

  public double getAverage() {
    return (double) averageRaw / precise;
  }

  public void tick(int value) {
    internalTick(tickValue + value);
    tickValue = 0;
  }

  public void tick() {
    internalTick(tickValue);
    tickValue = 0;
  }

  private void internalTick(int value) {
    pos = ++pos % precise;
    int oldValue = data[pos];
    data[pos] = value;
    if (pos == 0) {
      averageRaw = 0;
      for (int iValue : data) {
        averageRaw += iValue;
      }
    } else {
      averageRaw = averageRaw - oldValue + value;
    }
  }

  public void push(int value) {
    tickValue += value;
  }
}
