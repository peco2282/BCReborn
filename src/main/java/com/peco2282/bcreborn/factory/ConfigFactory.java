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
package com.peco2282.bcreborn.factory;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ConfigFactory {
  // blocks
  private static ForgeConfigSpec.BooleanValue autoWorkbenchBlock;
  private static ForgeConfigSpec.BooleanValue filteredBufferBlock;
  private static ForgeConfigSpec.BooleanValue packagerBlock;
  private static ForgeConfigSpec.BooleanValue requester;

  // items
  private static ForgeConfigSpec.BooleanValue blueprintItem;
  private static ForgeConfigSpec.BooleanValue templateItem;
  private static ForgeConfigSpec.BooleanValue list;
  private static ForgeConfigSpec.BooleanValue mapLocation;
  private static ForgeConfigSpec.BooleanValue packageItem;

  public static boolean isAutoWorkbenchBlock() { return autoWorkbenchBlock.get(); }
  public static boolean isFilteredBufferBlock() { return filteredBufferBlock.get(); }
  public static boolean isPackagerBlock() { return packagerBlock.get(); }
  public static boolean isRequester() { return requester.get(); }
  public static boolean isBlueprintItem() { return blueprintItem.get(); }
  public static boolean isTemplateItem() { return templateItem.get(); }
  public static boolean isList() { return list.get(); }
  public static boolean isMapLocation() { return mapLocation.get(); }
  public static boolean isPackageItem() { return packageItem.get(); }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Factory settings").push("factory");

    builder.comment("Factory block enable/disable").push("blocks");
    autoWorkbenchBlock = builder.define("autoWorkbenchBlock", true);
    filteredBufferBlock = builder.define("filteredBufferBlock", true);
    packagerBlock = builder.define("packagerBlock", true);
    requester = builder.define("requester", true);
    builder.pop();

    builder.comment("Factory item enable/disable").push("items");
    blueprintItem = builder.define("blueprintItem", true);
    templateItem = builder.define("templateItem", true);
    list = builder.define("list", true);
    mapLocation = builder.define("mapLocation", true);
    packageItem = builder.define("package", true);
    builder.pop();

    builder.pop();
    return builder;
  }
}
