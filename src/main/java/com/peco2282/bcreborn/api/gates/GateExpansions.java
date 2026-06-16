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
package com.peco2282.bcreborn.api.gates;

import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class GateExpansions {
  private static final Map<ResourceLocation, IGateExpansion> expansions = new HashMap<>();
  private static final Map<IGateExpansion, ItemStack> recipes = HashBiMap.create();

  private GateExpansions() {
  }

  public static void registerExpansion(IGateExpansion expansion) {
    registerExpansion(expansion.getUniqueIdentifier(), expansion);
  }

  public static void registerExpansion(ResourceLocation identifier, IGateExpansion expansion) {
    expansions.put(identifier, expansion);
  }

  public static void registerExpansion(IGateExpansion expansion, ItemStack addedRecipe) {
    registerExpansion(expansion.getUniqueIdentifier(), expansion);
    recipes.put(expansion, addedRecipe);
  }

  public static IGateExpansion getExpansion(String identifier) {
    return getExpansion(ResourceLocation.parse(identifier));
  }

  public static IGateExpansion getExpansion(ResourceLocation identifier) {
    return expansions.get(identifier);
  }

  public static Collection<? extends IGateExpansion> getExpansions() {
    return Collections.unmodifiableCollection(expansions.values());
  }

  public static Map<IGateExpansion, ItemStack> getRecipesForPostInit() {
    return recipes;
  }
}
