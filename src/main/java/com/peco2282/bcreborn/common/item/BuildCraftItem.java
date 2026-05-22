package com.peco2282.bcreborn.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

public class BuildCraftItem extends Item {
  private boolean passSneakClick;

  public BuildCraftItem(Properties properties) {
    super(properties);
  }


  public Item setPassSneakClick(boolean passClick) {
    this.passSneakClick = passClick;
    return this;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
    return passSneakClick;
  }
}
