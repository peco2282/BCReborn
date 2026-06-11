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
package com.peco2282.bcreborn.builders.data;

import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.BuildersItems;
import com.peco2282.bcreborn.common.data.BCRecipeHelper;
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.CoreItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class BuildersRecipe extends BCRecipeHelper {

  public static void build(Consumer<FinishedRecipe> p_251297_) {
    shaped(RecipeCategory.MISC, BuildersBlock.QUARRY)
      .pattern("IRI")
      .pattern("GIG")
      .pattern("DPD")
      .define('I', CoreItems.IRON_GEAR.get())
      .define('G', CoreItems.GOLD_GEAR.get())
      .define('R', Items.REDSTONE)
      .define('D', Items.DIAMOND)
      .define('P', Items.DIAMOND_PICKAXE)
      .unlockedBy("has_iron_gear", inventoryTrigger(get(CoreItems.IRON_GEAR.get())))
      .unlockedBy("has_gold_gear", inventoryTrigger(get(CoreItems.GOLD_GEAR.get())))
      .unlockedBy("has_redstone", inventoryTrigger(get(Items.REDSTONE)))
      .unlockedBy("has_diamond", inventoryTrigger(get(Items.DIAMOND)))
      .unlockedBy("has_pickaxe", inventoryTrigger(get(Items.DIAMOND_PICKAXE)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, BuildersItems.BLUEPRINT_TEMPLATE)
      .pattern("PPP")
      .pattern("PBP")
      .pattern("PPP")
      .define('P', Items.PAPER)
      .define('B', Items.BLACK_DYE)
      .unlockedBy("has_paper", inventoryTrigger(get(Items.PAPER)))
      .unlockedBy("has_black_dye", inventoryTrigger(get(Items.BLACK_DYE)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, BuildersItems.BLUEPRINT_STANDARD)
      .pattern("PPP")
      .pattern("PLP")
      .pattern("PPP")
      .define('P', Items.PAPER)
      .define('L', Items.LAPIS_LAZULI)
      .unlockedBy("has_paper", inventoryTrigger(get(Items.PAPER)))
      .unlockedBy("has_lapis_lazuli", inventoryTrigger(get(Items.LAPIS_LAZULI)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, BuildersBlock.CONSTRUCTION_MARKER)
      .pattern("G")
      .pattern("R")
      .define('G', CoreItems.GOLD_GEAR.get())
      .define('R', Items.REDSTONE_TORCH)
      .unlockedBy("has_gold_gear", inventoryTrigger(get(CoreItems.GOLD_GEAR.get())))
      .unlockedBy("has_redstone_torch", inventoryTrigger(get(Items.REDSTONE_TORCH)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, BuildersBlock.FILLER)
      .pattern("BMB")
      .pattern("YcY")
      .pattern("GCG")
      .define('B', Items.BLACK_DYE)
      .define('M', CoreBlocks.BLUE_MARKER.get())
      .define('Y', Items.YELLOW_DYE)
      .define('c', Items.CRAFTING_TABLE)
      .define('G', CoreItems.GOLD_GEAR.get())
      .define('C', Tags.Items.CHESTS)
      .unlockedBy("has_black_dye", inventoryTrigger(get(Items.BLACK_DYE)))
      .unlockedBy("has_blue_marker", inventoryTrigger(get(CoreBlocks.BLUE_MARKER.get())))
      .unlockedBy("has_yellow_dye", inventoryTrigger(get(Items.YELLOW_DYE)))
      .unlockedBy("has_crafting_table", inventoryTrigger(get(Items.CRAFTING_TABLE)))
      .unlockedBy("has_gold_gear", inventoryTrigger(get(CoreItems.GOLD_GEAR.get())))
      .unlockedBy("has_chest", inventoryTrigger(get(Tags.Items.CHESTS)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, BuildersBlock.BUILDER)
      .pattern("BMB")
      .pattern("YcY")
      .pattern("DCD")
      .define('B', Items.BLACK_DYE)
      .define('M', CoreBlocks.BLUE_MARKER.get())
      .define('Y', Items.YELLOW_DYE)
      .define('c', Items.CRAFTING_TABLE)
      .define('D', CoreItems.DIAMOND_GEAR.get())
      .define('C', Tags.Items.CHESTS)
      .unlockedBy("has_black_dye", inventoryTrigger(get(Items.BLACK_DYE)))
      .unlockedBy("has_blue_marker", inventoryTrigger(get(CoreBlocks.BLUE_MARKER.get())))
      .unlockedBy("has_yellow_dye", inventoryTrigger(get(Items.YELLOW_DYE)))
      .unlockedBy("has_crafting_table", inventoryTrigger(get(Items.CRAFTING_TABLE)))
      .unlockedBy("has_diamond_gear", inventoryTrigger(get(CoreItems.DIAMOND_GEAR.get())))
      .unlockedBy("has_chest", inventoryTrigger(get(Tags.Items.CHESTS)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, BuildersBlock.ARCHITECT)
      .pattern("BMB")
      .pattern("YCY")
      .pattern("DTD")
      .define('B', Items.BLACK_DYE)
      .define('M', CoreBlocks.BLUE_MARKER.get())
      .define('Y', Items.YELLOW_DYE)
      .define('C', Items.CRAFTING_TABLE)
      .define('D', CoreItems.DIAMOND_GEAR.get())
      .define('T', BuildersItems.BLUEPRINT_TEMPLATE.get())
      .unlockedBy("has_black_dye", inventoryTrigger(get(Items.BLACK_DYE)))
      .unlockedBy("has_blue_marker", inventoryTrigger(get(CoreBlocks.BLUE_MARKER.get())))
      .unlockedBy("has_yellow_dye", inventoryTrigger(get(Items.YELLOW_DYE)))
      .unlockedBy("has_crafting_table", inventoryTrigger(get(Items.CRAFTING_TABLE)))
      .unlockedBy("has_diamond_gear", inventoryTrigger(get(CoreItems.DIAMOND_GEAR.get())))
      .unlockedBy("has_blueprint", inventoryTrigger(get(BuildersItems.BLUEPRINT_TEMPLATE.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, BuildersBlock.BLUEPRINT_LIBRARY)
      .pattern("IGI")
      .pattern("BPB")
      .pattern("IRI")
      .define('I', Items.IRON_INGOT)
      .define('G', CoreItems.IRON_GEAR.get())
      .define('B', Items.BOOKSHELF)
      .define('P', BuildersItems.BLUEPRINT_TEMPLATE.get())
      .define('R', Items.REDSTONE)
      .unlockedBy("has_iron_gear", inventoryTrigger(get(CoreItems.IRON_GEAR.get())))
      .unlockedBy("has_book", inventoryTrigger(get(Items.BOOKSHELF)))
      .unlockedBy("has_blueprint", inventoryTrigger(get(BuildersItems.BLUEPRINT_TEMPLATE.get())))
      .unlockedBy("has_redstone", inventoryTrigger(get(Items.REDSTONE)))
      .save(p_251297_);
  }
}
