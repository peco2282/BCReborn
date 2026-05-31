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
package com.peco2282.bcreborn.factory;

import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.factory.menu.AutoWorkbenchMenu;
import com.peco2282.bcreborn.factory.menu.HopperMenu;
import com.peco2282.bcreborn.factory.menu.RefineryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornFactory.MODID)
public class FactoryMenuTypes {
  private static final BCRegistry REGISTRY = BCRebornFactory.getRegistry();

  private static <M extends BuildCraftMenu<M>> RegistryObject<MenuType<M>> register(
    String name, IContainerFactory<M> supplier
  ) {
    return REGISTRY.registerMenuType(name, () -> IForgeMenuType.create(supplier));
  }  public static final RegistryObject<MenuType<AutoWorkbenchMenu>> AUTO_WORKBENCH = register(
    "auto_workbench", AutoWorkbenchMenu::new
  );

  private static <M extends BuildCraftMenu<M>> Supplier<MenuType<M>> of(IContainerFactory<M> supplier) {
    return () -> IForgeMenuType.create(supplier);
  }  public static final RegistryObject<MenuType<HopperMenu>> HOPPER = register(
    "hopper", HopperMenu::new
  );

  public static final RegistryObject<MenuType<RefineryMenu>> REFINERY = register(
    "refinery", RefineryMenu::new
  );




}
