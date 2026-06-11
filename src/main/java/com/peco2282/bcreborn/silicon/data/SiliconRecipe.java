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
package com.peco2282.bcreborn.silicon.data;

import com.peco2282.bcreborn.common.data.BCRecipeHelper;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class SiliconRecipe extends BCRecipeHelper {
  public static void build(Consumer<FinishedRecipe> consumer) {
    shaped(RecipeCategory.REDSTONE, SiliconBlocks.LASER)
      .pattern("RRD")
      .pattern("RGD")
      .pattern("RRD")
      .define('R', Items.REDSTONE)
      .define('G', Items.GLOWSTONE_DUST)
      .define('D', Items.DIAMOND)
      .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, SiliconBlocks.ASSEMBLY_TABLE)
      .pattern("IDI")
      .pattern("IGI")
      .pattern("IPI")
      .define('I', Items.IRON_INGOT)
      .define('D', Items.DIAMOND)
      .define('G', CoreItems.STONE_GEAR.get())
      .define('P', Items.OBSIDIAN)
      .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, SiliconBlocks.ADVANCED_CRAFTING_TABLE)
      .pattern("IDI")
      .pattern("IGI")
      .pattern("IPI")
      .define('I', Items.IRON_INGOT)
      .define('D', Items.DIAMOND)
      .define('G', CoreItems.GOLD_GEAR.get())
      .define('P', Items.OBSIDIAN)
      .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, SiliconBlocks.INTEGRATION_TABLE)
      .pattern("IDI")
      .pattern("IGI")
      .pattern("IPI")
      .define('I', Items.IRON_INGOT)
      .define('D', Items.DIAMOND)
      .define('G', CoreItems.IRON_GEAR.get())
      .define('P', Items.OBSIDIAN)
      .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, SiliconBlocks.CHARGING_TABLE)
      .pattern("IDI")
      .pattern("IGI")
      .pattern("IPI")
      .define('I', Items.IRON_INGOT)
      .define('D', Items.DIAMOND)
      .define('G', Items.LAPIS_LAZULI)
      .define('P', Items.OBSIDIAN)
      .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, SiliconBlocks.PROGRAMMING_TABLE)
      .pattern("IDI")
      .pattern("IGI")
      .pattern("IPI")
      .define('I', Items.IRON_INGOT)
      .define('D', Items.DIAMOND)
      .define('G', Items.GLOWSTONE_DUST)
      .define('P', Items.OBSIDIAN)
      .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, SiliconBlocks.STAMPING_TABLE)
      .pattern("IDI")
      .pattern("IGI")
      .pattern("IPI")
      .define('I', Items.IRON_INGOT)
      .define('D', Items.DIAMOND)
      .define('G', Items.IRON_INGOT)
      .define('P', Items.OBSIDIAN)
      .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, SiliconBlocks.PACKAGER)
      .pattern(" G ")
      .pattern("GIG")
      .pattern(" G ")
      .define('G', CoreItems.GOLD_GEAR.get())
      .define('I', Items.IRON_INGOT)
      .unlockedBy(getHasName(CoreItems.GOLD_GEAR.get()), has(CoreItems.GOLD_GEAR.get()))
      .save(consumer);
  }
}
