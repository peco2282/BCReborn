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

import com.peco2282.bcreborn.api.RegistryUtil;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IRobotRegistry;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.boards.RedstoneBoardRobotEmptyNBT;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import com.peco2282.bcreborn.robotics.station.RobotStationPluggable;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobotItem extends BuildCraftItem {

  public RobotItem() {
    super(new Properties().stacksTo(1));
  }

  public static RedstoneBoardRobotNBT getRobotNBT(ItemStack stack) {
    return getRobotNBT(getNBT(stack));
  }

  public static int getEnergy(ItemStack stack) {
    return getEnergy(getNBT(stack));
  }

  public static ItemStack createRobotStack(RedstoneBoardRobotNBT board, int energy) {
    ItemStack robot = new ItemStack(RoboticsItems.ROBOT.get());
    CompoundTag boardCpt = new CompoundTag();
    board.createBoard(boardCpt);
    CompoundTag itemData = robot.getOrCreateTag();
    itemData.put("board", boardCpt);
    itemData.putInt("energy", energy);
    return robot;
  }

  private static CompoundTag getNBT(ItemStack stack) {
    CompoundTag cpt = stack.getOrCreateTag();
    if (!cpt.contains("board")) {
      RoboticsRedstoneRobots.EMPTY.get().createBoard(cpt);
    }
    return cpt;
  }

  private static RedstoneBoardRobotNBT getRobotNBT(CompoundTag cpt) {
    CompoundTag boardCpt = cpt.getCompound("board");
    return (RedstoneBoardRobotNBT) RegistryUtil.getRedstoneBoard(boardCpt);
  }

  private static int getEnergy(CompoundTag cpt) {
    return cpt.getInt("energy");
  }

  private static void setEnergy(CompoundTag cpt, int energy) {
    cpt.putInt("energy", energy);
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    CompoundTag cpt = getNBT(stack);
    RedstoneBoardRobotNBT boardNBT = getRobotNBT(cpt);

    if (!(boardNBT instanceof RedstoneBoardRobotEmptyNBT)) {
      return 1;
    } else {
      return 16;
    }
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    CompoundTag cpt = getNBT(stack);
    RedstoneBoardRobotNBT boardNBT = getRobotNBT(cpt);

    if (!(boardNBT instanceof RedstoneBoardRobotEmptyNBT)) {
      boardNBT.addInformation(stack, level, tooltip, flag);

      int energy = getEnergy(cpt);
      // TODO: Add energy information when MAX_ENERGY is available
      tooltip.add(Component.literal("Energy: " + energy));
    }
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    if (level.isClientSide) {
      return InteractionResult.PASS;
    }

    BlockEntity tile = level.getBlockEntity(context.getClickedPos());
    if (!(tile instanceof PipeBlockEntity pipe)) {
      return InteractionResult.PASS;
    }

    Direction side = context.getClickedFace();
    PipePluggable<?> pluggable = pipe.sideProperties.pluggables[side.ordinal()];
    if (!(pluggable instanceof RobotStationPluggable stationPluggable)) {
      return InteractionResult.PASS;
    }

    DockingStation<?> station = stationPluggable.getStation();
    if (station == null || station.isTaken()) {
      return InteractionResult.PASS;
    }

    ItemStack stack = context.getItemInHand();
    CompoundTag cpt = getNBT(stack);
    RedstoneBoardRobotNBT boardNBT = getRobotNBT(cpt);

    if (boardNBT instanceof RedstoneBoardRobotEmptyNBT) {
      return InteractionResult.PASS;
    }

    IRobotRegistry registry = RobotManager.registryProvider.getRegistry(level);
    if (registry == null) {
      return InteractionResult.PASS;
    }

    RobotEntity robot = new RobotEntity(level, boardNBT);
    robot.setUniqueRobotId(registry.getNextRobotId());
    robot.setPos(station.x() + 0.5, station.y() + 0.5, station.z() + 0.5);
    robot.setMainStation(station);
    robot.getBattery().receiveEnergy(getEnergy(cpt), false);

    if (level.addFreshEntity(robot)) {
      robot.dock(station);
      Player player = context.getPlayer();
      if (player == null || !player.getAbilities().instabuild) {
        stack.shrink(1);
      }
      return InteractionResult.SUCCESS;
    }

    return InteractionResult.PASS;
  }
}
