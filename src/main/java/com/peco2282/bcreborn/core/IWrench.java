package com.peco2282.bcreborn.core;

import net.minecraft.world.entity.player.Player;

public interface IWrench {
  boolean canWrench(Player player, int x, int y, int z);

  void wrenchUsed(Player player, int x, int y, int z);
}
