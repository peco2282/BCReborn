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
package com.peco2282.bcreborn.api.registry;

import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.core.IWorldProperty;
import com.peco2282.bcreborn.api.crops.ICropHandler;
import com.peco2282.bcreborn.api.facades.IFacadeItem;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.fuels.ICoolant;
import com.peco2282.bcreborn.api.fuels.IFuel;
import com.peco2282.bcreborn.api.fuels.ISolidCoolant;
import com.peco2282.bcreborn.api.statements.IStatement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BCRegistryKeys {
  public static final ResourceKey<Registry<Schematic>> SCHEMATIC = key("schematic");
  public static final ResourceKey<Registry<IWorldProperty>> WORLD_PROPERTY = key("world_property");
  public static final ResourceKey<Registry<ICropHandler>> CROP_HANDLER = key("crop_handler");
  public static final ResourceKey<Registry<IFacadeItem>> FACADE_ITEM = key("facade_item");
  public static final ResourceKey<Registry<IFillerPattern>> FILLER_PATTERNS = key("filler_patterns");
  public static final ResourceKey<Registry<IStatement>> STATEMENT = key("statement");


  private static <T> ResourceKey<Registry<T>> key(String name) {
    return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("buildcraftcore", name));
  }
}
