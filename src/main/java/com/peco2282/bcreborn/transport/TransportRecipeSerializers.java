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
package com.peco2282.bcreborn.transport;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.transport.recipes.PipeColoringRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

/** Crafting recipes of the transport module that are code, not JSON patterns. */
@InitRegister(modId = BCRebornTransport.MODID)
public final class TransportRecipeSerializers {
  public static final RegistryObject<SimpleCraftingRecipeSerializer<PipeColoringRecipe>> PIPE_COLORING =
    BCRebornTransport.getRegistry().registerRecipeSerializer("pipe_coloring",
      () -> new SimpleCraftingRecipeSerializer<>(PipeColoringRecipe::new));

  private TransportRecipeSerializers() {
  }
}
