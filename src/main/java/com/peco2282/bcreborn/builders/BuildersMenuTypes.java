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
package com.peco2282.bcreborn.builders;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.menu.ArchitectMenu;
import com.peco2282.bcreborn.builders.menu.BlueprintLibraryMenu;
import com.peco2282.bcreborn.builders.menu.BuilderMenu;
import com.peco2282.bcreborn.builders.menu.FillerMenu;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornBuilders.MODID)
public class BuildersMenuTypes {
  private static final BCRegistry REGISTRY = BCRebornBuilders.getRegistry();

  private static <M extends AbstractContainerMenu> RegistryObject<MenuType<M>> register(String name, MenuType<M> menuType) {
    return REGISTRY.registerMenuType(name, () -> menuType);
  }  public static final RegistryObject<MenuType<BuilderMenu>> BUILDER =
    register("builder", IForgeMenuType.create(BuilderMenu::new));

  public static final RegistryObject<MenuType<ArchitectMenu>> ARCHITECT =
    register("architect", IForgeMenuType.create(ArchitectMenu::new));

  public static final RegistryObject<MenuType<FillerMenu>> FILLER =
    register("filler", IForgeMenuType.create(FillerMenu::new));

  public static final RegistryObject<MenuType<BlueprintLibraryMenu>> BLUEPRINT_LIBRARY =
    register("blueprint_library", IForgeMenuType.create(BlueprintLibraryMenu::new));


}
