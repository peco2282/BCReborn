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
package com.peco2282.bcreborn.factory.data;

import com.peco2282.bcreborn.common.data.BCRecipeHelper;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class FactoryRecipe extends BCRecipeHelper {
  public static void build(Consumer<FinishedRecipe> p_251297_) {
    shaped(RecipeCategory.MISC, FactoryBlocks.MINING_WELL)
      .pattern("IPI")
      .pattern("IGI")
      .pattern("IRI")
      .define('I', Items.IRON_INGOT)
      .define('P', Items.IRON_PICKAXE)
      .define('G', CoreItems.IRON_GEAR.get())
      .define('R', Items.REDSTONE)
      .unlockedBy("has_iron", inventoryTrigger(get(Items.IRON_INGOT)))
      .unlockedBy("has_redstone", inventoryTrigger(get(Items.REDSTONE)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, FactoryBlocks.PUMP)
      .pattern("IRI")
      .pattern("IGI")
      .pattern("TBT")
      .define('I', Items.IRON_INGOT)
      .define('B', Items.BUCKET)
      .define('T', FactoryBlocks.TANK.get())
      .define('G', CoreItems.IRON_GEAR.get())
      .define('R', Items.REDSTONE)
      .unlockedBy("has_iron", inventoryTrigger(get(Items.IRON_INGOT)))
      .unlockedBy("has_tank", inventoryTrigger(get(FactoryBlocks.TANK.get())))
      .unlockedBy("has_redstone", inventoryTrigger(get(Items.REDSTONE)))
      .save(p_251297_);

    // Auto workbench (Horizontal and Vertical Crafting)
    shaped(RecipeCategory.MISC, FactoryBlocks.AUTO_WORKBENCH)
      .pattern("SCS")
      .define('S', CoreItems.STONE_GEAR.get())
      .define('C', Items.CRAFTING_TABLE)
      .unlockedBy("has_stone_gear", inventoryTrigger(get(CoreItems.STONE_GEAR.get())))
      .save(p_251297_, RecipeBuilder.getDefaultRecipeId(FactoryBlocks.AUTO_WORKBENCH.get()).withPath(it -> it + "_holizontal"));

    shaped(RecipeCategory.MISC, FactoryBlocks.AUTO_WORKBENCH)
      .pattern("S")
      .pattern("C")
      .pattern("S")
      .define('S', CoreItems.STONE_GEAR.get())
      .define('C', Items.CRAFTING_TABLE)
      .unlockedBy("has_stone_gear", inventoryTrigger(get(CoreItems.STONE_GEAR.get())))
      .save(p_251297_, RecipeBuilder.getDefaultRecipeId(FactoryBlocks.AUTO_WORKBENCH.get()).withPath(it -> it + "_vertical"));

    shaped(RecipeCategory.MISC, FactoryBlocks.TANK)
      .pattern("GGG")
      .pattern("G G")
      .pattern("GGG")
      .define('G', Items.GLASS)
      .unlockedBy("has_glass", inventoryTrigger(get(Items.GLASS)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, FactoryBlocks.REFINERY)
      .pattern("RTR")
      .pattern("TDT")
      .define('R', Items.REDSTONE_TORCH)
      .define('T', FactoryBlocks.TANK.get())
      .define('D', CoreItems.DIAMOND_GEAR.get())
      .unlockedBy("has_redstone_torch", inventoryTrigger(get(Items.REDSTONE_TORCH)))
      .unlockedBy("has_diamond_gear", inventoryTrigger(get(CoreItems.DIAMOND_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, FactoryBlocks.HOPPER)
      .pattern("ICI")
      .pattern(" S ")
      .define('I', Items.IRON_INGOT)
      .define('C', Items.CHEST)
      .define('S', CoreItems.STONE_GEAR.get())
      .unlockedBy("has_stone_gear", inventoryTrigger(get(CoreItems.STONE_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, FactoryBlocks.FLOOD_GATE)
      .pattern("IGI")
      .pattern("BTB")
      .pattern("IBI")
      .define('I', Items.IRON_INGOT)
      .define('B', Items.IRON_BARS)
      .define('G', CoreItems.IRON_GEAR.get())
      .define('T', FactoryBlocks.TANK.get())
      .unlockedBy("has_iron", inventoryTrigger(get(Items.IRON_INGOT)))
      .unlockedBy("has_tank", inventoryTrigger(get(FactoryBlocks.TANK.get())))
      .save(p_251297_);
  }
}
