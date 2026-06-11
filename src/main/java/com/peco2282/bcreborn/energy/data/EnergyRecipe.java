package com.peco2282.bcreborn.energy.data;

import com.peco2282.bcreborn.common.data.BCRecipeHelper;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class EnergyRecipe extends BCRecipeHelper {
  public static void build(Consumer<FinishedRecipe> p_251297_) {
    shaped(RecipeCategory.MISC, EnergyBlocks.STONE_ENGINE)
      .pattern("CCC")
      .pattern(" G ")
      .pattern("SPS")
      .define('C', Items.COBBLESTONE)
      .define('G', Items.GLASS)
      .define('S', CoreItems.STONE_GEAR.get())
      .define('P', Items.PISTON)
      .unlockedBy("has_cobblestone", inventoryTrigger(get(Items.COBBLESTONE)))
      .unlockedBy("has_glass", inventoryTrigger(get(Items.GLASS)))
      .unlockedBy("has_stone_gear", inventoryTrigger(get(CoreItems.STONE_GEAR.get())))
      .unlockedBy("has_piston", inventoryTrigger(get(Items.PISTON)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, EnergyBlocks.IRON_ENGINE)
      .pattern("CCC")
      .pattern(" G ")
      .pattern("SPS")
      .define('C', Items.IRON_INGOT)
      .define('G', Items.GLASS)
      .define('S', CoreItems.IRON_GEAR.get())
      .define('P', Items.PISTON)
      .unlockedBy("has_iron_ingot", inventoryTrigger(get(Items.IRON_INGOT)))
      .unlockedBy("has_glass", inventoryTrigger(get(Items.GLASS)))
      .unlockedBy("has_iron_gear", inventoryTrigger(get(CoreItems.IRON_GEAR.get())))
      .unlockedBy("has_piston", inventoryTrigger(get(Items.PISTON)))
      .save(p_251297_);
  }
}
