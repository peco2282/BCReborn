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
package com.peco2282.bcreborn.transport.recipes;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipe;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Original {@code ItemFacade.addFacade()}: on the Assembly Table, three cobblestone structure pipes and a block make
 * six facades of that block, for 8000 RF. One recipe per block that can be a facade, made on every reload.
 */
public final class FacadeAssemblyRecipes {
  public static final int ENERGY = 8000;
  public static final int FACADES_PER_CRAFT = 6;
  public static final int STRUCTURE_PIPES = 3;

  private FacadeAssemblyRecipes() {
  }

  public static ResourceLocation id(Block block) {
    ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
    return BCRebornTransport.location("facade/" + key.getNamespace() + "/" + key.getPath());
  }

  public static Collection<AssemblyRecipe> create() {
    List<AssemblyRecipe> recipes = new ArrayList<>();
    FacadeItem facade = TransportItems.FACADE.get();
    Ingredient structurePipe = Ingredient.of(TransportBlocks.get(PipeType.STRUCTURE, PipeMaterial.COBBLESTONE).get());
    for (Block block : BuiltInRegistries.BLOCK) {
      BlockState state = block.defaultBlockState();
      if (block.asItem() == Items.AIR || !FacadeItem.isBlockValidForCreativeTab(state)) {
        continue;
      }
      List<Ingredient> ingredients = new ArrayList<>();
      for (int i = 0; i < STRUCTURE_PIPES; i++) {
        ingredients.add(structurePipe);
      }
      ingredients.add(Ingredient.of(block));
      ItemStack result = facade.getFacadeForBlock(state);
      result.setCount(FACADES_PER_CRAFT);
      recipes.add(new AssemblyRecipe(id(block), ingredients, result, ENERGY, 0));
    }
    return recipes;
  }
}
