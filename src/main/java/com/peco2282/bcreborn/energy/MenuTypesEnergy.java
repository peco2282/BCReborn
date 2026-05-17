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

@InitRegister(modId = BCRebornEnergy.MODID, priority = 0)
public class MenuTypesEnergy {
  private static final BCRegistry REGISTRY = BCRebornEnergy.getRegistry();

  public static final RegistryObject<MenuType<StoneEngineMenu>> STONE_ENGINE = register("stone_engine", StoneEngineMenu::new);
  public static final RegistryObject<MenuType<IronEngineMenu>> IRON_ENGINE = register("iron_engine", IronEngineMenu::new);

  private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType.MenuSupplier<T> type) {
    return REGISTRY.registerMenuType(name, () -> new MenuType<>(type, FeatureFlagSet.of()));
  }
}
