package peco2282.bcreborn.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.item.BCCoreItems;
import peco2282.bcreborn.transport.block.BCTransportBlocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BCRecipeProvider extends RecipeProvider {
  public BCRecipeProvider(PackOutput p_248933_, CompletableFuture<HolderLookup.Provider> p_333797_) {
    super(p_248933_, p_333797_);
  }

  static ShapedRecipeBuilder shapedItem(Supplier<? extends Item> item) {
    return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item.get());
  }

  static ShapedRecipeBuilder shapedBlock(Supplier<? extends Block> block) {
    return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block.get());
  }

  static ShapelessRecipeBuilder shapeless(Supplier<Item> item) {
    return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item.get());
  }

  @Override
  protected void buildRecipes(RecipeOutput p_297267_) {
    gear(p_297267_);
    pipe(p_297267_);
  }

  void gear(RecipeOutput output) {
    shapedItem(BCCoreItems.GEAR_WOOD)
        .define('#', Items.STICK)
        .pattern(" # ")
        .pattern("# #")
        .pattern(" # ")
        .unlockedBy("has_item", has(Items.STICK))
        .save(output, BCReborn.location("gear_wood"));
    shapedItem(BCCoreItems.GEAR_STONE)
        .define('G', BCCoreItems.GEAR_WOOD.get())
        .define('#', Items.STONE)
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_WOOD.get()))
        .save(output, BCReborn.location("gear_stone"));
    shapedItem(BCCoreItems.GEAR_IRON)
        .define('#', Items.IRON_INGOT)
        .define('G', BCCoreItems.GEAR_STONE.get())
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_STONE.get()))
        .save(output, BCReborn.location("gear_iron"));
    shapedItem(BCCoreItems.GEAR_GOLD)
        .define('#', Items.GOLD_INGOT)
        .define('G', BCCoreItems.GEAR_IRON.get())
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_IRON.get()))
        .save(output, BCReborn.location("gear_gold"));
    shapedItem(BCCoreItems.GEAR_GOLD)
        .define('#', Items.DIAMOND)
        .define('G', BCCoreItems.GEAR_DIAMOND.get())
        .pattern(" # ")
        .pattern("#G#")
        .pattern(" # ")
        .unlockedBy("has_item", has(BCCoreItems.GEAR_GOLD.get()))
        .save(output, BCReborn.location("gear_diamond"));
  }

  void pipe(RecipeOutput output) {
    shapedBlock(BCTransportBlocks.WOOD_ITEM_PIPE)
        .define('G', Items.GLASS)
        .define('P', ItemTags.PLANKS)
        .pattern("PGP")
        .unlockedBy("has_planks", has(ItemTags.PLANKS))
        .unlockedBy("has_glass", has(Items.GLASS))
        .save(output, BCReborn.location("wood_item_pipe"));
  }
}
