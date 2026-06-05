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
package com.peco2282.bcreborn.silicon;

import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.silicon.item.PackageItem;
import com.peco2282.bcreborn.silicon.item.RedstoneChipsetItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornSilicon.MODID)
public class SiliconItems {
  private static final BCRegistry REGISTRY = BCRebornSilicon.getRegistry();

  public static final RegistryObject<RedstoneChipsetItem> REDSTONE_CHIPSET = register("redstone_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.RED));
  public static final RegistryObject<RedstoneChipsetItem> IRON_CHIPSET = register("iron_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.IRON));
  public static final RegistryObject<RedstoneChipsetItem> GOLD_CHIPSET = register("gold_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.GOLD));
  public static final RegistryObject<RedstoneChipsetItem> DIAMOND_CHIPSET = register("diamond_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.DIAMOND));
  public static final RegistryObject<RedstoneChipsetItem> PULSATING_CHIPSET = register("pulsating_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.PULSATING));
  public static final RegistryObject<RedstoneChipsetItem> QUARTZ_CHIPSET = register("quartz_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.QUARTZ));
  public static final RegistryObject<RedstoneChipsetItem> COMP_CHIPSET = register("comp_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.COMP));
  public static final RegistryObject<RedstoneChipsetItem> EMERALD_CHIPSET = register("emerald_chipset", () -> new RedstoneChipsetItem(RedstoneChipsetItem.Chipset.EMERALD));

  public static final RegistryObject<PackageItem> PACKAGE_ITEM = register("package", PackageItem::new);

  public static List<RegistryObject<RedstoneChipsetItem>> getAllChipsets() {
    return List.of(REDSTONE_CHIPSET, IRON_CHIPSET, GOLD_CHIPSET, DIAMOND_CHIPSET, PULSATING_CHIPSET, QUARTZ_CHIPSET, COMP_CHIPSET, EMERALD_CHIPSET);
  }

  private static <I extends Item> RegistryObject<I> register(String name, Supplier<I> item) {
    return REGISTRY.registerItem(name, item);
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.accept(REDSTONE_CHIPSET.get());
    output.accept(IRON_CHIPSET.get());
    output.accept(GOLD_CHIPSET.get());
    output.accept(DIAMOND_CHIPSET.get());
    output.accept(PULSATING_CHIPSET.get());
    output.accept(QUARTZ_CHIPSET.get());
    output.accept(COMP_CHIPSET.get());
    output.accept(EMERALD_CHIPSET.get());
    output.accept(PACKAGE_ITEM.get());
  }
}
