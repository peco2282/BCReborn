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
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class BCBoardNBT extends RedstoneBoardRobotNBT {
  private final ResourceLocation texture;
  private final ResourceLocation id;
  private final String upperName, boardType;
  private final Function<RobotEntityBase, ? extends RedstoneBoardRobot<?>> boardInit;
  private final int energyCost;

  public BCBoardNBT(ResourceLocation id, String name, Function<RobotEntityBase, ? extends RedstoneBoardRobot<?>> board, String boardType, int energyCost) {
    this.id = id;
    this.boardType = boardType;
    this.upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
    this.texture = BCRebornRobotics.location("textures/entity/robot_" + name + ".png");
    this.boardInit = board;
    this.energyCost = energyCost;
  }

  public int getEnergyCost() {
    return energyCost;
  }

  @Override
  public ResourceLocation getID() {
    return id;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(Component.translatable("buildcraft.boardRobot" + upperName).withStyle(ChatFormatting.BOLD));
    tooltip.add(Component.translatable("buildcraft.boardRobot" + upperName + ".desc"));
  }

  @Override
  public RedstoneBoardRobot<?> create(RobotEntityBase robot) {
    return boardInit.apply(robot);
  }

  @Override
  public ResourceLocation getRobotTexture() {
    return texture;
  }

  public String getBoardType() {
    return boardType;
  }
}
