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
package com.peco2282.bcreborn.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/** Loaded only when handling client-bound packets; signatures use common types. */
@OnlyIn(Dist.CLIENT)
public final class ClientPacketAccess {
  private ClientPacketAccess() {}

  @Nullable
  public static Level level() {
    return Minecraft.getInstance().level;
  }

  @Nullable
  public static Player player() {
    return Minecraft.getInstance().player;
  }
}
