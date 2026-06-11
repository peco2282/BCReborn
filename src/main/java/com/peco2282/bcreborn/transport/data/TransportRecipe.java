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
package com.peco2282.bcreborn.transport.data;

import com.peco2282.bcreborn.common.data.BCRecipeHelper;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class TransportRecipe extends BCRecipeHelper {
  public static void build(Consumer<FinishedRecipe> consumer) {
    TransportBlocks.pipesForEach((type, material, block) -> {
      if (type == PipeType.ITEM) {
        buildItemPipe(consumer, material, block.get());
      }
    });
  }

  private static void buildItemPipe(Consumer<FinishedRecipe> consumer, PipeMaterial material, ItemLike result) {
    ItemLike ingredient = getIngredient(material);
    if (ingredient == null) return;

    shaped(RecipeCategory.MISC, result::asItem, 8)
      .pattern("GMG")
      .define('G', Items.GLASS)
      .define('M', ingredient)
      .unlockedBy(getHasName(ingredient), has(ingredient))
      .save(consumer);
  }

  private static ItemLike getIngredient(PipeMaterial material) {
    return switch (material) {
      case WOOD -> Items.OAK_PLANKS;
      case COBBLESTONE -> Items.COBBLESTONE;
      case STONE -> Items.STONE;
      case GOLD -> Items.GOLD_INGOT;
      case IRON -> Items.IRON_INGOT;
      case DIAMOND -> Items.DIAMOND;
      case EMERALD -> Items.EMERALD;
      case OBSIDIAN -> Items.OBSIDIAN;
      case QUARTZ -> Items.QUARTZ;
      case VOID -> Items.INK_SAC;
      case CLAY -> Items.CLAY_BALL;
      case SANDSTONE -> Items.SANDSTONE;
      case LAPIS -> Items.LAPIS_LAZULI;
      case DAIZULI -> Items.LAPIS_BLOCK;
      case EMZULI -> Items.EMERALD_BLOCK;
      case STRIPES -> CoreItems.GOLD_GEAR.get();
    };
  }
}
