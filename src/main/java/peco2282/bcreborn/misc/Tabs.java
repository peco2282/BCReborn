package peco2282.bcreborn.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.item.BCCoreItems;

public class Tabs {
  private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BCReborn.MODID);
  public static final RegistryObject<CreativeModeTab> BC_TAB = CREATIVE_MODE_TABS
      .register("bcreborn", () -> CreativeModeTab.builder()
          .displayItems((params, output) -> output.accept(BCCoreItems.GEAR_WOOD.get()))
          .build());

  static void init(IEventBus bus) {
    CREATIVE_MODE_TABS.register(bus);
  }
}
