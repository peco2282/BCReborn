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
package com.peco2282.bcreborn.energy;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.energy.menu.IronEngineMenu;
import com.peco2282.bcreborn.energy.menu.StoneEngineMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornEnergy.MODID)
public class MenuTypesEnergy {
  private static final BCRegistry REGISTRY = BCRebornEnergy.getRegistry();

  private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType.MenuSupplier<T> type) {
    return REGISTRY.registerMenuType(name, () -> new MenuType<>(type, FeatureFlagSet.of()));
  }  public static final RegistryObject<MenuType<StoneEngineMenu>> STONE_ENGINE = register("stone_engine", StoneEngineMenu::new);
  public static final RegistryObject<MenuType<IronEngineMenu>> IRON_ENGINE = register("iron_engine", IronEngineMenu::new);


}
