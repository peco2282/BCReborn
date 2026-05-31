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
package com.peco2282.bcreborn.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

import javax.annotation.Nullable;

public interface ContainerBlockEntity extends Container, MenuProvider, Nameable {
  @Nullable
  @Override
  default AbstractContainerMenu createMenu(int p_58641_, Inventory p_58642_, Player p_58643_) {
    return BaseContainerBlockEntity.canUnlock(p_58643_, LockCode.NO_LOCK, getDisplayName())
        ? this.createMenu(p_58641_, p_58642_)
        : null;
  }

  @Override
  default Component getDisplayName() {
    return Nameable.super.getDisplayName();
  }

  AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_);
}
