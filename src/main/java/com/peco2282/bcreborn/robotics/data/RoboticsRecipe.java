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
package com.peco2282.bcreborn.robotics.data;

import com.peco2282.bcreborn.common.data.BCRecipeHelper;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.silicon.SiliconItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class RoboticsRecipe extends BCRecipeHelper {
  public static void build(Consumer<FinishedRecipe> consumer) {
    shaped(RecipeCategory.MISC, RoboticsItems.ROBOT)
      .pattern("I I")
      .pattern("ICI")
      .pattern("I I")
      .define('I', Items.IRON_INGOT)
      .define('C', SiliconItems.DIAMOND_CHIPSET.get())
      .unlockedBy(getHasName(SiliconItems.DIAMOND_CHIPSET.get()), has(SiliconItems.DIAMOND_CHIPSET.get()))
      .save(consumer);

    shaped(RecipeCategory.MISC, () -> RoboticsItems.REDSTONE_BOARDS.get("clean").get())
      .pattern("RRR")
      .pattern("RCR")
      .pattern("RRR")
      .define('R', Items.REDSTONE)
      .define('C', SiliconItems.GOLD_CHIPSET.get())
      .unlockedBy(getHasName(SiliconItems.GOLD_CHIPSET.get()), has(SiliconItems.GOLD_CHIPSET.get()))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, RoboticsBlocks.ZONE_PLAN)
      .pattern("RRR")
      .pattern("RCR")
      .pattern("RRR")
      .define('R', Items.REDSTONE)
      .define('C', Items.MAP)
      .unlockedBy(getHasName(Items.MAP), has(Items.MAP))
      .save(consumer);

    shaped(RecipeCategory.REDSTONE, RoboticsBlocks.REQUESTER)
      .pattern("RPR")
      .pattern("RCR")
      .pattern("RPR")
      .define('R', Items.REDSTONE)
      .define('P', Items.PAPER)
      .define('C', SiliconItems.IRON_CHIPSET.get())
      .unlockedBy(getHasName(SiliconItems.IRON_CHIPSET.get()), has(SiliconItems.IRON_CHIPSET.get()))
      .save(consumer);

    shaped(RecipeCategory.MISC, RoboticsItems.ROBOT_STATION)
      .pattern("I I")
      .pattern("ICI")
      .pattern("I I")
      .define('I', Items.IRON_INGOT)
      .define('C', SiliconItems.IRON_CHIPSET.get())
      .unlockedBy(getHasName(SiliconItems.IRON_CHIPSET.get()), has(SiliconItems.IRON_CHIPSET.get()))
      .save(consumer);
  }
}
