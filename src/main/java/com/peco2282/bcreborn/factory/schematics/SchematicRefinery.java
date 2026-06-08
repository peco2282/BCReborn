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
package com.peco2282.bcreborn.factory.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

import java.util.LinkedList;

public class SchematicRefinery extends SchematicTile {

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    requirements.add(new ItemStack(FactoryBlocks.REFINERY.get()));
  }

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {

  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    state = state.rotate( Rotation.COUNTERCLOCKWISE_90);
  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    super.initializeFromObjectAt(context, x, y, z);

    tileNBT.remove("tank1");
    tileNBT.remove("tank2");
    tileNBT.remove("result");
    tileNBT.remove("mjStored");
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    // to support refineries coming from older blueprints
    tileNBT.remove("tank1");
    tileNBT.remove("tank2");
    tileNBT.remove("result");
    tileNBT.remove("mjStored");

    super.placeInWorld(context, x, y, z, stacks);
  }

}
