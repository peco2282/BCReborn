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
package com.peco2282.bcreborn.common.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;

/** Keeps Screen-typed lambdas out of the common mod entry points. */
public final class ConfigScreenRegistration {
  private ConfigScreenRegistration() {}

  public static void register(int tab) {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.register(tab));
  }

  private static final class Client {
    private static void register(int tab) {
      MinecraftForge.registerConfigScreen((minecraft, parent) -> new BCRebornConfigScreen(minecraft, parent, tab));
    }
  }
}
