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
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedstoneBoardRobotEmptyNBT extends RedstoneBoardRobotNBT {

  private static final RedstoneBoardRobotEmptyNBT INSTANCE = new RedstoneBoardRobotEmptyNBT();

  private RedstoneBoardRobotEmptyNBT() {
  }

  public static RedstoneBoardRobotEmptyNBT getInstance() {
    return INSTANCE;
  }

  @Override
  public int getEnergyCost() {
    return COST_ZERO;
  }

  @Override
  public BoardRobotEmpty create(RobotEntityBase robot) {
    return new BoardRobotEmpty(robot);
  }

  @Override
  public ResourceLocation getRobotTexture() {
    return BCRebornRobotics.location("textures/entity/robot_base.png");
  }

  @Override
  public ResourceLocation getID() {
    return BCRebornRobotics.location("robot_empty");
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
  }
}
