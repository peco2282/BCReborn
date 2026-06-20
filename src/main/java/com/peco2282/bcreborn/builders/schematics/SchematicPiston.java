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
package com.peco2282.bcreborn.builders.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.common.builder.schematics.SchematicRotateMeta;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;

public class SchematicPiston extends SchematicRotateMeta {

  public SchematicPiston() {
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    context.world().setBlock(new BlockPos(x, y, z), state, 3);
  }

}
