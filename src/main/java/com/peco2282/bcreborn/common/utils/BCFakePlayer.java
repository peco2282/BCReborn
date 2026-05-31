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

import com.mojang.authlib.GameProfile;
import com.peco2282.bcreborn.BCReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class BCFakePlayer {
  public static final GameProfile GAME_PROFILE = new GameProfile(UUID.nameUUIDFromBytes(BCReborn.MOD_ID_BASE.getBytes()), "[BuildCraft]");
  private static WeakReference<FakePlayer> playerCache = new WeakReference<>(null);

  public static FakePlayer createBuildCraftPlayer(ServerLevel world) {
    return FakePlayerFactory.get(world, GAME_PROFILE);
  }

  public static FakePlayer createBuildCraftPlayer(ServerLevel world, BlockPos pos) {
    var p = FakePlayerFactory.get(world, GAME_PROFILE);
    p.setPos(pos.getX(), pos.getY(), pos.getZ());
    return p;
  }

  public static FakePlayer createBuildCraftPlayer(ServerLevel world, int x, int y, int z) {
    var p = FakePlayerFactory.get(world, GAME_PROFILE);
    p.setPos(x, y, z);
    return p;
  }

  public static WeakReference<FakePlayer> getBuildCraftPlayer(ServerLevel world) {
    FakePlayer fakePlayer = playerCache.get();
    if (fakePlayer == null) {
      fakePlayer = createBuildCraftPlayer(world);

      playerCache = new WeakReference<>(fakePlayer);
    }
    return playerCache;
  }

  public static WeakReference<FakePlayer> getBuildCraftPlayer(ServerLevel world, BlockPos pos) {
    FakePlayer fakePlayer = playerCache.get();
    if (fakePlayer == null) {
      fakePlayer = createBuildCraftPlayer(world, pos);

      playerCache = new WeakReference<>(fakePlayer);
    }
    return playerCache;
  }

  public static WeakReference<FakePlayer> getBuildCraftPlayer(ServerLevel world, int x, int y, int z) {
    FakePlayer fakePlayer = playerCache.get();
    if (fakePlayer == null) {
      fakePlayer = createBuildCraftPlayer(world, x, y, z);

      playerCache = new WeakReference<>(fakePlayer);
    }
    return playerCache;
  }
}
