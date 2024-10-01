package peco2282.bcreborn.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;

public class BCMisc {
  public static final RegistryObject<CreativeModeTab> TAB = BCRegistry
      .registerTab(BCReborn.MODID, () -> CreativeModeTab.builder()
          .displayItems((params, output) -> {
          })
          .build());

  static void init() {
  }
}
