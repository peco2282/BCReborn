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
package com.peco2282.bcreborn.common.blueprint;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.common.builder.BuildingSlotBlock;
import net.minecraft.core.BlockPos;

import java.util.Set;

public class IndexRequirementMap {
  private final Multimap<BlockPos, BlockPos> requirements = HashMultimap.create();
  private final Multimap<BlockPos, BlockPos> requirementsInv = HashMultimap.create();

  public IndexRequirementMap() {

  }

  public void add(BuildingSlotBlock b, IBuilderContext context) {
    if (b.schematic instanceof SchematicBlock) {
      BlockPos index = new BlockPos(b.x, b.y, b.z);
      Set<BlockPos> prereqs = ((SchematicBlock) b.schematic).getPrerequisiteBlocks(context);

      if (!prereqs.isEmpty()) {
        Position min = context.surroundingBox().pMin();
        Position max = context.surroundingBox().pMax();
        for (BlockPos i : prereqs) {
          BlockPos ia = new BlockPos(i.getX() + index.getX(), i.getY() + index.getY(), i.getZ() + index.getZ());
          if (ia.equals(index) || ia.getX() < min.x || ia.getY() < min.y || ia.getZ() < min.z || ia.getX() > max.x || ia.getY() > max.y || ia.getZ() > max.z) {
            continue;
          }
          requirements.put(index, ia);
          requirementsInv.put(ia, index);
        }
      }
    }
  }

  public boolean contains(BlockPos index) {
    return requirements.containsKey(index);
  }

  public void remove(BuildingSlotBlock b) {
    BlockPos index = new BlockPos(b.x, b.y, b.z);
    remove(index);
  }

  public void remove(BlockPos index) {
    for (BlockPos reqingIndex : requirementsInv.get(index)) {
      requirements.remove(reqingIndex, index);
    }
    requirementsInv.removeAll(index);
  }
}
