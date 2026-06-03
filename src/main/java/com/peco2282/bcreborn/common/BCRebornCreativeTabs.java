/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.data.tag.CommonBlockTags;
import com.peco2282.bcreborn.common.data.tag.CommonItemTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID)
public class BCRebornCreativeTabs {
  public static final ResourceKey<CreativeModeTab> CORE_ID = create(BCRebornCore.location("core"));
  public static final ResourceKey<CreativeModeTab> ENERGY_ID = create(BCRebornCore.location("energy"));
  public static final ResourceKey<CreativeModeTab> TRANSPORT_ID = create(BCRebornCore.location("transport"));
  public static final ResourceKey<CreativeModeTab> BUILDERS_ID = create(BCRebornCore.location("builders"));
  public static final RegistryObject<CreativeModeTab> CORE = register("core", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Core"))
//      .icon(() -> new ItemStack(BlocksCore.WOODEN_ENGINE.get()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenBlock(CommonBlockTags.CORE).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenItem(CommonItemTags.CORE).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .build()
  );
  public static final RegistryObject<CreativeModeTab> BUILDERS = register("builders", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Builders"))
//      .icon(() -> new ItemStack(BlocksBuilders.COPPER_BLOCK.get()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenBlock(CommonBlockTags.BUILDERS).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenItem(CommonItemTags.BUILDERS).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
//      .withTabsAfter(CORE_ID)
      .build()
  );
  public static final RegistryObject<CreativeModeTab> ENERGY = register("energy", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Energy"))
//      .icon(() -> new ItemStack(BlocksEnergy.CREATIVE_ENGINE.get()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenBlock(CommonBlockTags.ENERGY).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenItem(CommonItemTags.ENERGY).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
//      .withTabsAfter(CORE_ID)
      .build()
  );
  public static final RegistryObject<CreativeModeTab> TRANSPORT = register("transport", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Transport"))
//      .icon(() -> new ItemStack(BlocksTransport.PIPES_BY_MAT.get(PipeMaterial.WOOD).get(PipeType.ITEM).get()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenBlock(CommonBlockTags.TRANSPORT).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenItem(CommonItemTags.TRANSPORT).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
//      .withTabsAfter(CORE_ID)
      .build()
  );
  public static final RegistryObject<CreativeModeTab> SILICON = register("silicon", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Silicon"))
//      .icon(() -> new ItemStack(BlocksSilicon.SILICON_ORE.get()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenBlock(CommonBlockTags.SILICON).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenItem(CommonItemTags.SILICON).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
//      .withTabsAfter(CORE_ID)
      .build()
  );
  public static final RegistryObject<CreativeModeTab> ROBOTICS = register("robotics", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Robotics"))
//      .icon(() -> new ItemStack(BlocksTransport.PIPES_BY_MAT.get(PipeMaterial.WOOD).get(PipeType.ITEM).get()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenBlock(CommonBlockTags.ROBOTICS).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenItem(CommonItemTags.ROBOTICS).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
//      .withTabsAfter(CORE_ID)
      .build()
  );
  public static final RegistryObject<CreativeModeTab> FACTORY = register("factory", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Factory"))
//      .icon(() -> new ItemStack(BlocksFactory.FACTORY.get()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenBlock(CommonBlockTags.FACTORY).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
      .displayItems((parameters, output) -> output.acceptAll(RegistryUtil.flattenItem(CommonItemTags.FACTORY).stream().map(ItemStack::new).filter(stack -> !stack.isEmpty()).toList()))
//      .withTabsAfter(CORE_ID)
      .build()
  );

  private static ResourceKey<CreativeModeTab> create(ResourceLocation location) {
    return ResourceKey.create(Registries.CREATIVE_MODE_TAB, location);
  }

  public static RegistryObject<CreativeModeTab> register(String name, Supplier<CreativeModeTab> supplier) {
    return BCRebornCore.getRegistry().registerCreativeTab(name, supplier);
  }
}
