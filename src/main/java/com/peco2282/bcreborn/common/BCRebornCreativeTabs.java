package com.peco2282.bcreborn.common;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.data.tag.CommonBlockTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID, priority = 0)
public class BCRebornCreativeTabs {
  public static final ResourceKey<CreativeModeTab> CORE_ID = create(BCRebornCore.location("core"));
  public static final ResourceKey<CreativeModeTab> ENERGY_ID = create(BCRebornCore.location("energy"));
  public static final ResourceKey<CreativeModeTab> TRANSPORT_ID = create(BCRebornCore.location("transport"));
  public static final ResourceKey<CreativeModeTab> BUILDERS_ID = create(BCRebornCore.location("builders"));

  private static ResourceKey<CreativeModeTab> create(ResourceLocation location) {
    return ResourceKey.create(Registries.CREATIVE_MODE_TAB, location);
  }

  public static final RegistryObject<CreativeModeTab> CORE = register("core", () -> CreativeModeTab.builder()
          .title(Component.literal("BCReborn Core"))
//      .icon(() -> new ItemStack(BlocksCore.WOODEN_ENGINE.get()))
          .displayItems((parameters, output) -> RegistryUtil.flattenBlock(CommonBlockTags.CORE).forEach(output::accept))
          .build()
  );

  public static final RegistryObject<CreativeModeTab> BUILDERS = register("builders", () -> CreativeModeTab.builder()
          .title(Component.literal("BCReborn Builders"))
//      .icon(() -> new ItemStack(BlocksBuilders.COPPER_BLOCK.get()))
          .displayItems((parameters, output) -> RegistryUtil.flattenBlock(CommonBlockTags.BUILDERS).forEach(output::accept))
//      .withTabsAfter(CORE_ID)
          .build()
  );

  public static final RegistryObject<CreativeModeTab> ENERGY = register("energy", () -> CreativeModeTab.builder()
          .title(Component.literal("BCReborn Energy"))
//      .icon(() -> new ItemStack(BlocksEnergy.CREATIVE_ENGINE.get()))
          .displayItems((parameters, output) -> RegistryUtil.flattenBlock(CommonBlockTags.ENERGY).forEach(output::accept))
//      .withTabsAfter(CORE_ID)
          .build()
  );

  public static final RegistryObject<CreativeModeTab> TRANSPORT = register("transport", () -> CreativeModeTab.builder()
          .title(Component.literal("BCReborn Transport"))
//      .icon(() -> new ItemStack(BlocksTransport.PIPES_BY_MAT.get(PipeMaterial.WOOD).get(PipeType.ITEM).get()))
          .displayItems((parameters, output) -> RegistryUtil.flattenBlock(CommonBlockTags.TRANSPORT).forEach(output::accept))
//      .withTabsAfter(CORE_ID)
          .build()
  );

  public static RegistryObject<CreativeModeTab> register(String name, Supplier<CreativeModeTab> supplier) {
    return BCRebornCore.getRegistry().registerCreativeTab(name, supplier);
  }
}
