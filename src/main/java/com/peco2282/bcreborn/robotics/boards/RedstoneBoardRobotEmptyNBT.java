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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class RedstoneBoardRobotEmptyNBT extends RedstoneBoardRobotNBT {

  public static RedstoneBoardRobotEmptyNBT instance = new RedstoneBoardRobotEmptyNBT();
  private TextureAtlasSprite icon;

  @Override
  public RedstoneBoardRobot create(RobotEntityBase robot) {
    return null; // TODO: Implement when BoardRobotEmpty is available
  }

  @Override
  public ResourceLocation getRobotTexture() {
    return BCRebornRobotics.location("textures/entity/robot/robot_base.png");
  }

  @Override
  public String getID() {
    return "buildcraft:boardRobotEmpty";
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornRobotics.location("board/clean"));
  }

  @Override
  public TextureAtlasSprite getIcon(CompoundTag nbt) {
    return icon;
  }

}
