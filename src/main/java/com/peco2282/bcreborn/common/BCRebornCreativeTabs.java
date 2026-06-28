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
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
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
  public static final ResourceKey<CreativeModeTab> BUILDERS_ID = create(BCRebornCore.location("builders"));
  public static final ResourceKey<CreativeModeTab> ENERGY_ID = create(BCRebornCore.location("energy"));
  public static final ResourceKey<CreativeModeTab> FACTORY_ID = create(BCRebornCore.location("factory"));
  public static final ResourceKey<CreativeModeTab> ROBOTICS_ID = create(BCRebornCore.location("robotics"));
  public static final ResourceKey<CreativeModeTab> SILICON_ID = create(BCRebornCore.location("silicon"));

  public static final RegistryObject<CreativeModeTab> CORE = register("core", () -> CreativeModeTab.builder()
    .title(Component.literal("BCReborn Core"))
    .icon(() -> new ItemStack(CoreBlocks.WOODEN_ENGINE.get()))
    .displayItems((param, output) -> {
      CoreBlocks.registerCreativeTab(param, output);
      CoreItems.registerCreativeTab(param, output);
    })
    .build()
  );

  public static final RegistryObject<CreativeModeTab> BUILDERS = register("builders", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Builders"))
      .icon(() -> new ItemStack(BuildersBlock.QUARRY.get()))
      .displayItems(BuildersBlock::registerCreativeTab)
      .withTabsAfter(CORE_ID)
      .build()
  );

  public static final RegistryObject<CreativeModeTab> ENERGY = register("energy", () -> CreativeModeTab.builder()
      .title(Component.literal("BCReborn Energy"))
      .icon(() -> new ItemStack(EnergyBlocks.CREATIVE_ENGINE.get()))
      .displayItems(EnergyBlocks::registerCreativeTab)
      .withTabsAfter(BUILDERS_ID)
      .build()
  );

  public static final RegistryObject<CreativeModeTab> FACTORY = register("factory", () -> CreativeModeTab.builder()
    .title(Component.literal("BCReborn Factory"))
    .icon(() -> new ItemStack(FactoryBlocks.AUTO_WORKBENCH.get()))
    .displayItems(FactoryBlocks::registerCreativeTab)
    .withTabsAfter(ENERGY_ID)
    .build()
  );

  public static final RegistryObject<CreativeModeTab> ROBOTICS = register("robotics", () -> CreativeModeTab.builder()
    .title(Component.literal("BCReborn Robotics"))
    .icon(() -> new ItemStack(RoboticsBlocks.REQUESTER.get()))
    .displayItems((parameters, output) -> {
      RoboticsBlocks.registerCreativeTab(parameters, output);
      RoboticsItems.registerCreativeTab(parameters, output);
    })
    .withTabsAfter(FACTORY_ID)
    .build()
  );

  public static final RegistryObject<CreativeModeTab> SILICON = register("silicon", () -> CreativeModeTab.builder()
    .title(Component.literal("BCReborn Silicon"))
    .icon(() -> new ItemStack(SiliconBlocks.LASER.get()))
    .displayItems((parameters, output) -> {
      SiliconBlocks.registerCreativeTab(parameters, output);
      SiliconItems.registerCreativeTab(parameters, output);
    })
    .withTabsAfter(ROBOTICS_ID)
    .build()
  );

  public static final RegistryObject<CreativeModeTab> TRANSPORT = register("transport", () -> CreativeModeTab.builder()
    .title(Component.literal("BCReborn Transport"))
    .icon(() -> new ItemStack(TransportBlocks.get(PipeType.ITEM, PipeMaterial.WOOD).get()))
    .displayItems((param, output) -> {
      TransportBlocks.registerCreativeTab(param, output);
      TransportItems.registerCreativeTab(param, output);
    })
    .withTabsAfter(SILICON_ID)
    .build()
  );

  private static ResourceKey<CreativeModeTab> create(ResourceLocation location) {
    return ResourceKey.create(Registries.CREATIVE_MODE_TAB, location);
  }

  public static RegistryObject<CreativeModeTab> register(String name, Supplier<CreativeModeTab> supplier) {
    return BCRebornCore.getRegistry().registerCreativeTab(name, supplier);
  }
}
