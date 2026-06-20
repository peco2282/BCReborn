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
import com.peco2282.bcreborn.api.blueprints.SchematicBlockEntity;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;

public class SchematicPump extends SchematicBlockEntity {

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    requirements.add(new ItemStack(FactoryBlocks.PUMP.get()));
  }

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {

  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    super.initializeFromObjectAt(context, x, y, z);

    tileNBT.remove("tank");
    tileNBT.remove("mjStored");
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    context.world().setBlock(new BlockPos(x, y, z), state, 0, 3);
  }

  @Override
  public BuildingStage getBuildStage() {
    return BuildingStage.STANDALONE;
  }
}
