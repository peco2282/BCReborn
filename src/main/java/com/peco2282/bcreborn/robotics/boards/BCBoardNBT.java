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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BCBoardNBT extends RedstoneBoardRobotNBT {
  public static final Map<String, RedstoneBoardRobotNBT> REGISTRY = new HashMap<>();
  private final ResourceLocation texture;
  private final String id, upperName, boardType;
  private final Function<RobotEntityBase, ? extends RedstoneBoardRobot> boardInit;

  @OnlyIn(Dist.CLIENT)
  private TextureAtlasSprite icon;

  public BCBoardNBT(String id, String name, Function<RobotEntityBase, ? extends RedstoneBoardRobot> board, String boardType) {
    this.id = id;
    this.boardType = boardType;
    this.upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
    this.texture = BCRebornRobotics.location("textures/entity/robot/robot_" + name + ".png");
    this.boardInit = board;

    REGISTRY.put(name, this);
  }

  @Override
  public String getID() {
    return id;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(Component.translatable("buildcraft.boardRobot" + upperName).withStyle(ChatFormatting.BOLD));
    tooltip.add(Component.translatable("buildcraft.boardRobot" + upperName + ".desc"));
  }

  @Override
  public RedstoneBoardRobot create(RobotEntityBase robot) {
    return boardInit.apply(robot);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornRobotics.location("board/" + boardType));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public TextureAtlasSprite getIcon(CompoundTag nbt) {
    return icon;
  }

  @Override
  public ResourceLocation getRobotTexture() {
    return texture;
  }
}
