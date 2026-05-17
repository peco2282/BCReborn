package com.peco2282.bcreborn.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class BuildCraftMenu<M extends BuildCraftMenu<M>> extends AbstractContainerMenu {
  public BuildCraftMenu(@Nullable MenuType<M> p_38851_, int p_38852_, Inventory p_38853_) {
    super(p_38851_, p_38852_);
  }

}
