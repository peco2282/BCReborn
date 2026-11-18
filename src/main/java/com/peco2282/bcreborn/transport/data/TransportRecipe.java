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

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.common.Tags;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.transport.TransportItems;
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
    buildSealant(consumer);
    TransportBlocks.pipesForEach((type, material, block) -> {
      if (type == PipeType.ITEM) {
        buildItemPipe(consumer, material, block.get());
      } else if (type == PipeType.FLUID) {
        // Original: pipe sealant + item pipe -> fluid pipe.
        convertItemPipe(consumer, material, block.get(), TransportItems.PIPE_SEALANT.get(), "sealant");
      } else if (type == PipeType.ENERGY) {
        // Original: redstone + item pipe -> kinesis (power) pipe.
        convertItemPipe(consumer, material, block.get(), Items.REDSTONE, "redstone");
      } else if (type == PipeType.STRUCTURE) {
        // Original: cobblestone, gravel, cobblestone -> 8 structure pipes.
        shaped(RecipeCategory.MISC, block.get()::asItem, 8)
          .pattern("CGC")
          .define('C', Items.COBBLESTONE)
          .define('G', Items.GRAVEL)
          .unlockedBy(getHasName(Items.GRAVEL), has(Items.GRAVEL))
          .save(consumer);
      }
    });
  }

  private static void buildSealant(Consumer<FinishedRecipe> consumer) {
    // Original: cactus green -> pipe sealant.
    shapeless(RecipeCategory.MISC, TransportItems.PIPE_SEALANT)
      .requires(Tags.Items.DYES_GREEN)
      .unlockedBy("has_green_dye", has(Tags.Items.DYES_GREEN))
      .save(consumer);
  }

  private static void convertItemPipe(Consumer<FinishedRecipe> consumer, PipeMaterial material, ItemLike result,
                                      ItemLike additive, String additiveName) {
    ItemLike itemPipe = TransportBlocks.get(PipeType.ITEM, material).get();
    shapeless(RecipeCategory.MISC, result::asItem)
      .requires(itemPipe)
      .requires(additive)
      .unlockedBy(getHasName(itemPipe), has(itemPipe))
      .save(consumer, BCRebornTransport.location(BuiltInRegistries.ITEM.getKey(result.asItem()).getPath() + "_from_" + additiveName));
    // Original buildPipe: the converted pipe crafts back into its item pipe on its own.
    shapeless(RecipeCategory.MISC, itemPipe::asItem)
      .requires(result)
      .unlockedBy(getHasName(result), has(result))
      .save(consumer, BCRebornTransport.location(BuiltInRegistries.ITEM.getKey(itemPipe.asItem()).getPath() + "_from_"
        + BuiltInRegistries.ITEM.getKey(result.asItem()).getPath()));
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
