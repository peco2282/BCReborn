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

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.Random;

public class WrapRandomSource extends Random implements RandomSource {
  private final RandomSource wrapped;

  public WrapRandomSource(RandomSource wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public RandomSource fork() {
    return wrapped.fork();
  }

  @Override
  public PositionalRandomFactory forkPositional() {
    return wrapped.forkPositional();
  }


  @Override
  public int nextInt(int origin, int bound) {
    return wrapped.nextInt(origin, bound);
  }
}
