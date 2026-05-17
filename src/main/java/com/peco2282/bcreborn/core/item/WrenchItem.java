package com.peco2282.bcreborn.core.item;

import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.core.IWrench;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class WrenchItem extends BuildCraftItem implements IWrench {
  public WrenchItem(Item.Properties properties) {
    super(properties);
  }

  @Override
  public boolean canWrench(Player player, int x, int y, int z) {
    return true;
  }

  @Override
  public void wrenchUsed(Player player, int x, int y, int z) {
  }
}
