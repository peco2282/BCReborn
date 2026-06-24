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
package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.common.registry.RegistryEnumObject;
import com.peco2282.bcreborn.core.item.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID)
public class CoreItems {
  private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

  public static final RegistryObject<WrenchItem> WRENCH = register("wrench", () -> new WrenchItem(new Item.Properties()));

  public static final RegistryObject<BuildCraftItem> WOODEN_GEAR = register("wooden_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> STONE_GEAR = register("stone_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> IRON_GEAR = register("iron_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> GOLD_GEAR = register("gold_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> DIAMOND_GEAR = register("diamond_gear", () -> new BuildCraftItem(new Item.Properties()));

  public static final RegistryObject<BuildCraftItem> BUILD_TOOL_BOX = register("build_tool_box", () -> new BuildCraftItem(new Item.Properties()));


  public static final RegistryObject<ListItem> LIST = register("list", ListItem::new);
  public static final RegistryObject<PaintbrushItem> PAINTBRUSH = register("paintbrush", () -> new PaintbrushItem(null));
  public static final RegistryEnumObject<DyeColor, PaintbrushItem> COLORED_PAINTBRUSH = RegistryEnumObject.create(
    DyeColor.class,
    e -> "paintbrush_" + e.getSerializedName().toLowerCase(Locale.ENGLISH),
    PaintbrushItem::new,
    CoreItems::register
  );
  public static final RegistryObject<MapLocationItem> MAP_LOCATION = register("map_location", MapLocationItem::new);

  public static final RegistryObject<DebuggerItem> DEBUGGER = register("debugger", DebuggerItem::new);

  private static <I extends Item> RegistryObject<I> register(String name, Supplier<I> item) {
    return REGISTRY.registerItem(name, item);
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.accept(WRENCH.get());
    output.accept(WOODEN_GEAR.get());
    output.accept(STONE_GEAR.get());
    output.accept(IRON_GEAR.get());
    output.accept(GOLD_GEAR.get());
    output.accept(DIAMOND_GEAR.get());
    output.accept(BUILD_TOOL_BOX.get());
    output.accept(LIST.get());

    output.acceptAll(COLORED_PAINTBRUSH.values().stream().map(RegistryObject::get).map(ItemStack::new).toList());
    output.accept(MAP_LOCATION.get());
    output.accept(DEBUGGER.get());
  }
}
