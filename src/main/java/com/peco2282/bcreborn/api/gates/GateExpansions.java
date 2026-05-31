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
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class GateExpansions {
  private static final Map<String, IGateExpansion> expansions = new HashMap<>();
  private static final ArrayList<IGateExpansion> expansionIDs = new ArrayList<>();
  private static final Map<IGateExpansion, ItemStack> recipes = HashBiMap.create();

  private GateExpansions() {
  }

  public static void registerExpansion(IGateExpansion expansion) {
    registerExpansion(expansion.getUniqueIdentifier(), expansion);
  }

  public static void registerExpansion(String identifier, IGateExpansion expansion) {
    expansions.put(identifier, expansion);
    expansionIDs.add(expansion);
  }

  public static void registerExpansion(IGateExpansion expansion, ItemStack addedRecipe) {
    registerExpansion(expansion.getUniqueIdentifier(), expansion);
    recipes.put(expansion, addedRecipe);
  }

  public static IGateExpansion getExpansion(String identifier) {
    return expansions.get(identifier);
  }

  public static Set<IGateExpansion> getExpansions() {
    return new HashSet<>(expansionIDs);
  }

  public static Map<IGateExpansion, ItemStack> getRecipesForPostInit() {
    return recipes;
  }

  public static IGateExpansion getExpansionByID(int id) {
    return expansionIDs.get(id);
  }

  public static int getExpansionID(IGateExpansion expansion) {
    return expansionIDs.indexOf(expansion);
  }
}
