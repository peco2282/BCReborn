/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.block.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.lib.block.menu.BCMenu;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCCoreMenuTypes {
  public static final RegistryObject<MenuType<EngineIronMenu>> IRON_ENGINE =
      register("iron_engine", () -> IForgeMenuType.create(EngineIronMenu::new));
  public static final RegistryObject<MenuType<EngineStoneMenu>> STONE_ENGINE =
      register("stone_engine", () -> IForgeMenuType.create(EngineStoneMenu::new));

  private static <M extends BCMenu> RegistryObject<MenuType<M>> register(
      final String name, final Supplier<MenuType<M>> type) {
    return BCRegistry.registerMenuType(name, type);
  }
}
