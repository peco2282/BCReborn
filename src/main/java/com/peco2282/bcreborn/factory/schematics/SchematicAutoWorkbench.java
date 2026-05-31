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
import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.api.core.JavaTools;
import com.peco2282.bcreborn.common.inventory.InventoryIterator;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.AutoWorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.LinkedList;

public class SchematicAutoWorkbench extends SchematicTile {

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {
    AutoWorkbenchBlockEntity autoWb = getTile(context, new BlockPos(x, y, z));
    if (autoWb != null) {
      ArrayList<ItemStack> rqs = new ArrayList<>();
      rqs.add(new ItemStack(FactoryBlocks.AUTO_WORKBENCH.get()));

      for (IInvSlot slot : InventoryIterator.getIterable(autoWb.craftMatrix, Direction.UP)) {
        ItemStack stack = slot.getStackInSlot();
        if (stack != null) {
          stack = stack.copy();
          stack.setCount(1);
          rqs.add(stack);
        }
      }

      storedRequirements = JavaTools.concat(storedRequirements, rqs
        .toArray(ItemStack[]::new));
    }
  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    super.initializeFromObjectAt(context, x, y, z);

    tileNBT.remove("Items");
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    super.placeInWorld(context, x, y, z, stacks);

    AutoWorkbenchBlockEntity autoWb = getTile(context, new BlockPos(x, y, z));
    if (autoWb != null) {
      for (IInvSlot slot : InventoryIterator.getIterable(autoWb.craftMatrix, Direction.UP)) {
        ItemStack stack = slot.getStackInSlot();
        if (stack != null) {
          stack.setCount(1);
        }
      }
    }
  }

  @Override
  public BuildingStage getBuildStage() {
    return BuildingStage.STANDALONE;
  }

  private AutoWorkbenchBlockEntity getTile(IBuilderContext context, BlockPos pos) {
    BlockEntity tile = context.world().getBlockEntity(pos);
    if (tile instanceof AutoWorkbenchBlockEntity autoWork) {
      return autoWork;
    }
    return null;
  }
}
