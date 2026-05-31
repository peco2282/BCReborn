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
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import com.peco2282.bcreborn.robotics.item.RedstoneBoardItem;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import com.peco2282.bcreborn.robotics.item.RobotStationItem;

@InitRegister(modId = BCRebornRobotics.MODID)
public class RoboticsItems {
    private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();

    public static final RegistryObject<RedstoneBoardItem> REDSTONE_BOARD = register("redstone_board", () -> new RedstoneBoardItem());
    public static final RegistryObject<RobotItem> ROBOT = register("robot", () -> new RobotItem());
    public static final RegistryObject<RobotStationItem> ROBOT_STATION = register("robot_station", () -> new RobotStationItem());


    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return REGISTRY.registerItem(name, supplier);
    }
}
