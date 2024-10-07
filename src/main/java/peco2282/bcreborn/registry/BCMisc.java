package peco2282.bcreborn.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;

import java.util.Map;

public class BCMisc {
  public static final RegistryObject<CreativeModeTab> TAB = BCRegistry
      .registerTab(BCReborn.MODID, () -> CreativeModeTab.builder()
          .displayItems((params, output) -> {
            ForgeRegistries.BLOCKS.getEntries()
                .stream().filter(e -> e.getKey().location().getNamespace().equals(BCReborn.MODID))
                .map(Map.Entry::getValue)
                .forEach(output::accept);

            ForgeRegistries.ITEMS.getEntries()
                .stream().filter(e -> e.getKey().location().getNamespace().equals(BCReborn.MODID))
                .map(Map.Entry::getValue)
                .forEach(output::accept);
          })
          .build());

  static void init() {
  }
}
