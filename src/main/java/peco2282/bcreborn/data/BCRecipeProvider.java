/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.item.BCCoreItems;
import peco2282.bcreborn.transport.block.BCTransportBlocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * BCRecipeProvider is responsible for defining and registering custom recipes for the game. This
 * class generates recipes for items, blocks, and other crafting materials used in the BCReborn mod.
 *
 * @author peco2282
 */
public class BCRecipeProvider extends RecipeProvider {
  /**
   * Constructs a BCRecipeProvider instance.
   *
   * @param p_248933_ The output destination for generated recipes.
   * @param p_333797_ A future that provides a lookup provider for holders.
   */
  public BCRecipeProvider(
      PackOutput p_248933_, CompletableFuture<HolderLookup.Provider> p_333797_) {
    super(p_248933_, p_333797_);
  }

  /**
   * Convenience method for creating a shaped recipe for a given item.
   *
   * @param item The supplier providing the item to craft.
   * @return A configured ShapedRecipeBuilder instance.
   */
  static ShapedRecipeBuilder shaped(Supplier<? extends ItemLike> item) {
    return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item.get());
  }

  /**
   * Convenience method for creating a shapeless recipe for a given item.
   *
   * @param item The supplier providing the item to craft.
   * @return A configured ShapelessRecipeBuilder instance.
   */
  static ShapelessRecipeBuilder shapeless(Supplier<? extends ItemLike> item) {
    return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item.get());
  }

  /**
   * Builds and registers all custom recipes for the mod.
   *
   * @param p_297267_ The recipe output used to register the recipes.
   */
  @Override
  protected void buildRecipes(RecipeOutput p_297267_) {
    gear(p_297267_);
    pipe(p_297267_);
  }

  /**
   * Defines and registers recipes for gears of different materials.
   *
   * @param output The recipe output used to register the gear recipes.
   */
  void gear(RecipeOutput output) {
    shaped(BCCoreItems.GEAR_WOOD)
        .define('#', Items.STICK)
        .pattern(" # ")
        .pattern("# #")
        .pattern(" # ")
        .unlockedBy("has_item", has(Items.STICK))
        .save(output, BCReborn.location("gear_wood"));
    shaped(BCCoreItems.GEAR_STONE)
        .define('G', BCCoreItems.GEAR_WOOD.get())
        .define('#', Items.STONE)
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_WOOD.get()))
        .save(output, BCReborn.location("gear_stone"));
    shaped(BCCoreItems.GEAR_IRON)
        .define('#', Items.IRON_INGOT)
        .define('G', BCCoreItems.GEAR_STONE.get())
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_STONE.get()))
        .save(output, BCReborn.location("gear_iron"));
    shaped(BCCoreItems.GEAR_GOLD)
        .define('#', Items.GOLD_INGOT)
        .define('G', BCCoreItems.GEAR_IRON.get())
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_IRON.get()))
        .save(output, BCReborn.location("gear_gold"));
    shaped(BCCoreItems.GEAR_DIAMOND)
        .define('#', Items.DIAMOND)
        .define('G', BCCoreItems.GEAR_GOLD.get())
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_GOLD.get()))
        .save(output, BCReborn.location("gear_diamond"));
  }

  /**
   * Defines and registers recipes for pipes.
   *
   * @param output The recipe output used to register the pipe recipes.
   */
  void pipe(RecipeOutput output) {
    shaped(BCTransportBlocks.WOOD_ITEM_PIPE)
        .define('G', Items.GLASS)
        .define('P', ItemTags.PLANKS)
        .pattern("PGP")
        .unlockedBy("has_planks", has(ItemTags.PLANKS))
        .unlockedBy("has_glass", has(Items.GLASS))
        .save(output, BCReborn.location("wood_item_pipe"));
  }
}
