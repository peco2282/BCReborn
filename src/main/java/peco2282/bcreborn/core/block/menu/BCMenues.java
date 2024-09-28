package peco2282.bcreborn.core.block.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.lib.block.menu.BCMenu;

import java.util.function.Supplier;

public class BCMenues {
  private static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BCReborn.MODID);

  private static <M extends BCMenu> RegistryObject<MenuType<M>> register(final String name, final Supplier<MenuType<M>> type) {
    return REGISTRY.register(name, type);
  }

  public static void init(IEventBus bus) {
    REGISTRY.register(bus);
  }

  public static final RegistryObject<MenuType<EngineIronMenu>> IRON_ENGINE = register("iron_engine", () -> IForgeMenuType.create(EngineIronMenu::new));
  public static final RegistryObject<MenuType<EngineStoneMenu>> STONE_ENGINE = register("stone_engine", () -> IForgeMenuType.create(EngineStoneMenu::new));
}
