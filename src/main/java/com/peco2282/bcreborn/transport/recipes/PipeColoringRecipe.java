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

import com.peco2282.bcreborn.transport.TransportRecipeSerializers;
import com.peco2282.bcreborn.transport.pipe.PipeGlassColor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Original {@code PipeColoringRecipe}: eight pipes of one kind and a dye make eight pipes stained in that colour;
 * pipes of one kind and a water bucket wash the colour off. The pipes may be stained already (in any colours).
 * The water bucket comes back empty.
 */
public class PipeColoringRecipe extends CustomRecipe {
  /** Pipes needed with a dye (the dye in the middle of eight pipes). */
  public static final int PIPES_PER_DYE = 8;

  public PipeColoringRecipe(ResourceLocation id, CraftingBookCategory category) {
    super(id, category);
  }

  /** What the grid holds, or null if it is not this recipe. */
  private record Input(ItemStack pipe, int pipes, @Nullable DyeColor dye, boolean water) {
  }

  private static @Nullable Input read(CraftingContainer container) {
    Item pipe = null;
    ItemStack pipeStack = ItemStack.EMPTY;
    int pipes = 0;
    DyeColor dye = null;
    boolean water = false;
    for (int i = 0; i < container.getContainerSize(); i++) {
      ItemStack stack = container.getItem(i);
      if (stack.isEmpty()) continue;
      if (PipeGlassColor.isPipe(stack)) {
        if (pipe != null && stack.getItem() != pipe) return null;
        pipe = stack.getItem();
        pipeStack = stack;
        pipes++;
      } else if (stack.is(Items.WATER_BUCKET)) {
        if (water || dye != null) return null;
        water = true;
      } else {
        DyeColor color = DyeColor.getColor(stack);
        if (color == null || dye != null || water) return null;
        dye = color;
      }
    }
    if (pipe == null) return null;
    if (dye != null && pipes != PIPES_PER_DYE) return null;
    if (dye == null && !water) return null;
    return new Input(pipeStack, pipes, dye, water);
  }

  @Override
  public boolean matches(CraftingContainer container, Level level) {
    return read(container) != null;
  }

  @Override
  public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
    Input input = read(container);
    if (input == null) return ItemStack.EMPTY;
    ItemStack result = PipeGlassColor.with(input.pipe(), input.water() ? null : input.dye());
    result.setCount(input.pipes());
    return result;
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TransportRecipeSerializers.PIPE_COLORING.get();
  }
}
