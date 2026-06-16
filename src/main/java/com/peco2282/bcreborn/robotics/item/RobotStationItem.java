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
package com.peco2282.bcreborn.robotics.item;

import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableItem;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.robotics.render.RobotStationItemRenderer;
import com.peco2282.bcreborn.robotics.station.RobotStationPluggable;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class RobotStationItem extends BuildCraftItem implements IPipePluggableItem {

  public RobotStationItem() {
    super(new Properties());
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return RobotStationItemRenderer.INSTANCE;
      }
    });
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
    return true;
  }

  @Override
  public RobotStationPluggable createPipePluggable(IPipe pipe, Direction side, ItemStack stack) {
    return new RobotStationPluggable();
  }
}
