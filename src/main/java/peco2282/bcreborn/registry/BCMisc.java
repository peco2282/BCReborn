package peco2282.bcreborn.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.data.tag.BCBlockTag;
import peco2282.bcreborn.data.tag.BCItemTag;
import peco2282.bcreborn.utils.RegistryUtil;

public class BCMisc {
  public static final RegistryObject<CreativeModeTab> CORE_TAB = BCRegistry
      .registerTab(name("core"), () -> CreativeModeTab.builder()
          .displayItems((params, output) -> {
            output.acceptAll(RegistryUtil.flattenBlock(BCBlockTag.CORE).stream().map(ItemStack::new).toList());
            output.acceptAll(RegistryUtil.fromItemTag(BCItemTag.GEAR).stream().map(ItemStack::new).toList());

          })
          .build());
  public static final RegistryObject<CreativeModeTab> BUILDER_TAB = BCRegistry
      .registerTab(name("builder"), () -> CreativeModeTab.builder()
          .displayItems((params, output) -> output.acceptAll(RegistryUtil.flattenBlock(BCBlockTag.BUILDER).stream().map(ItemStack::new).toList()))
          .build());
  public static final RegistryObject<CreativeModeTab> TRANSPORT_TAB = BCRegistry
      .registerTab(name("transport"), () -> CreativeModeTab.builder()
          .displayItems((params, output) -> output.acceptAll(RegistryUtil.flattenBlock(BCBlockTag.TRANSPORT).stream().map(ItemStack::new).toList()))
          .build());

  private static String name(String name) {
    return BCReborn.MODID + "_" + name;
  }

  static void init() {
  }
}
