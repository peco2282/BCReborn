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
import com.peco2282.bcreborn.robotics.menu.RequesterMenu;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornRobotics.MODID)
public class RoboticsMenuTypes {
  private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();

  private static <M extends AbstractContainerMenu> RegistryObject<MenuType<M>> register(String name, MenuType<M> menuType) {
    return REGISTRY.registerMenuType(name, () -> menuType);
  }

  public static final RegistryObject<MenuType<RequesterMenu>> REQUESTER = register("requester", IForgeMenuType.create(RequesterMenu::new));
  public static final RegistryObject<MenuType<ZonePlanMenu>> ZONE_PLAN = register("zone_plan", IForgeMenuType.create(ZonePlanMenu::new));


}
