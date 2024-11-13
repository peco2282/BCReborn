package peco2282.bcreborn.builder.block.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.lib.block.menu.BCMenu;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCBuilderMenuTypes {
  public static final RegistryObject<MenuType<FillerMenu>> FILLER = register("filler", () -> IForgeMenuType.create(FillerMenu::new));

  private static <M extends BCMenu> RegistryObject<MenuType<M>> register(final String name, final Supplier<MenuType<M>> type) {
    return BCRegistry.registerMenuType(name, type);
  }
}
