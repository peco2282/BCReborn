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
package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.registry.KeyedRegistryObject;
import com.peco2282.bcreborn.robotics.item.RedstoneBoardItem;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import com.peco2282.bcreborn.robotics.item.RobotStationItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornRobotics.MODID)
public class RoboticsItems {
  private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();
  public static final RegistryObject<RobotItem> ROBOT = register("robot", RobotItem::new);
  public static final RegistryObject<RobotStationItem> ROBOT_STATION = register("robot_station", RobotStationItem::new);
  private static final List<String> boardNames = List.of("clean", "green", "blue", "red", "yellow", "unknown");
  public static final KeyedRegistryObject.SingleKey<RedstoneBoardItem, String> REDSTONE_BOARDS = KeyedRegistryObject.single(
    boardNames,
    name -> name + "_board",
    RoboticsItems::register,
    ignore -> new RedstoneBoardItem()
  );// register("redstone_board", RedstoneBoardItem::new);

  private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier) {
    return REGISTRY.registerItem(name, supplier);
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    REDSTONE_BOARDS.getAll().forEach(it -> output.accept(it.get()));
    output.accept(ROBOT.get());
    output.accept(ROBOT_STATION.get());
  }
}
