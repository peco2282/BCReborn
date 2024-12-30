package peco2282.bcreborn.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.data.tag.BCBlockTag;
import peco2282.bcreborn.data.tag.BCItemTag;
import peco2282.bcreborn.registry.levelgen.OilPlacementFilter;
import peco2282.bcreborn.utils.RegistryUtil;

@InitRegister
public class BCMisc {
  /**
   * The main creative mode tab for core items and blocks.
   */
  public static final RegistryObject<CreativeModeTab> CORE_TAB = BCRegistry
      .registerTab(name("core"), () -> CreativeModeTab.builder()
          .displayItems((params, output) -> {
            output.acceptAll(RegistryUtil.flattenBlock(BCBlockTag.CORE).stream().map(ItemStack::new).toList());
            output.acceptAll(RegistryUtil.fromItemTag(BCItemTag.GEAR).stream().map(ItemStack::new).toList());

          })
          .build());
  /**
   * Creative mode tab for builder-related items and blocks.
   */
  public static final RegistryObject<CreativeModeTab> BUILDER_TAB = BCRegistry
      .registerTab(name("builder"), () -> CreativeModeTab.builder()
          .displayItems((params, output) -> output.acceptAll(RegistryUtil.flattenBlock(BCBlockTag.BUILDER).stream().map(ItemStack::new).toList()))
          .build());
  /**
   * Creative mode tab for transport-related items and blocks.
   */
  public static final RegistryObject<CreativeModeTab> TRANSPORT_TAB = BCRegistry
      .registerTab(name("transport"), () -> CreativeModeTab.builder()
          .displayItems((params, output) -> output.acceptAll(RegistryUtil.flattenBlock(BCBlockTag.TRANSPORT).stream().map(ItemStack::new).toList()))
          .build());

  /**
   * Placement modifier type for oil sources in world generation.
   */
  public static final RegistryObject<PlacementModifierType<OilPlacementFilter>> OIL_PMT = BCRegistry.registerPlacementModifierType("oil_source_type", OilPlacementFilter.CODEC);

  /**
   * Constructs a namespaced registry name with the BCReborn mod ID.
   *
   * @param name the name to be prefixed with the mod ID
   * @return the namespaced registry name
   */
  private static String name(String name) {
    return BCReborn.MODID + "_" + name;
  }
}
