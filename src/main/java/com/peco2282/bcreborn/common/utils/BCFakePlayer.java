package com.peco2282.bcreborn.common.utils;

import com.mojang.authlib.GameProfile;
import com.peco2282.bcreborn.BCReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.UUID;

public class BCFakePlayer {
  public static final GameProfile GAME_PROFILE = new GameProfile(UUID.nameUUIDFromBytes(BCReborn.MOD_ID_BASE.getBytes()), "[BuildCraft]");

  public static FakePlayer getBuildCraftPlayer(ServerLevel world, BlockPos pos) {
    return FakePlayerFactory.get(world, GAME_PROFILE);
  }
  public static FakePlayer getBuildCraftPlayer(ServerLevel world, int x, int y, int z) {
    return FakePlayerFactory.get(world, GAME_PROFILE);
  }
}
