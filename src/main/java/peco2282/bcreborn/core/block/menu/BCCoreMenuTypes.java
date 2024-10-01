package peco2282.bcreborn.core.block.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.builder.block.menu.FillerMenu;
import peco2282.bcreborn.lib.block.menu.BCMenu;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCCoreMenuTypes {
  public static final RegistryObject<MenuType<EngineIronMenu>> IRON_ENGINE = register("iron_engine", () -> IForgeMenuType.create(EngineIronMenu::new));
  public static final RegistryObject<MenuType<EngineStoneMenu>> STONE_ENGINE = register("stone_engine", () -> IForgeMenuType.create(EngineStoneMenu::new));

  private static <M extends BCMenu> RegistryObject<MenuType<M>> register(final String name, final Supplier<MenuType<M>> type) {
    return BCRegistry.registerMenuType(name, type);
  }
public static void init() {}
}
