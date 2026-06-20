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


import com.peco2282.bcreborn.api.blueprints.BuilderAPI;
import com.peco2282.bcreborn.api.blueprints.SchematicBlockBase;
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.common.builder.AbstractBuilderBlockEntity;
import com.peco2282.bcreborn.common.builder.BuildingSlot;
import com.peco2282.bcreborn.common.builder.BuildingSlotBlock;
import com.peco2282.bcreborn.common.builder.BuildingSlotBlock.Mode;
import com.peco2282.bcreborn.common.builder.BuildingSlotIterator;
import com.peco2282.bcreborn.common.inventory.InventoryIterator;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.LinkedList;

public class BptBuilderTemplate extends BptBuilderBase {

  private final LinkedList<BuildingSlotBlock> clearList = new LinkedList<>();
  private final LinkedList<BuildingSlotBlock> buildList = new LinkedList<>();
  private BuildingSlotIterator iteratorBuild, iteratorClear;

  public BptBuilderTemplate(BlueprintBase bluePrint, Level world, int x, int y, int z) {
    super(bluePrint, world, x, y, z);
  }

  @Override
  protected void internalInit() {
    if (blueprint.excavate) {
      for (int j = blueprint.sizeY - 1; j >= 0; --j) {
        int yCoord = j + y - blueprint.anchorY;

        if (yCoord < 0 || yCoord >= context.world.getHeight()) {
          continue;
        }

        for (int i = 0; i < blueprint.sizeX; ++i) {
          int xCoord = i + x - blueprint.anchorX;

          for (int k = 0; k < blueprint.sizeZ; ++k) {
            int zCoord = k + z - blueprint.anchorZ;

            SchematicBlockBase slot = blueprint.get(i, j, k);

            if (slot == null && !isLocationUsed(xCoord, yCoord, zCoord)) {
              BuildingSlotBlock b = new BuildingSlotBlock();

              b.schematic = null;
              b.x = xCoord;
              b.y = yCoord;
              b.z = zCoord;
              b.mode = Mode.ClearIfInvalid;
              b.buildStage = 0;

              clearList.add(b);
            }
          }
        }
      }
    }

    for (int j = 0; j < blueprint.sizeY; ++j) {
      int yCoord = j + y - blueprint.anchorY;

      if (yCoord < 0 || yCoord >= context.world.getHeight()) {
        continue;
      }

      for (int i = 0; i < blueprint.sizeX; ++i) {
        int xCoord = i + x - blueprint.anchorX;

        for (int k = 0; k < blueprint.sizeZ; ++k) {
          int zCoord = k + z - blueprint.anchorZ;

          SchematicBlockBase slot = blueprint.get(i, j, k);

          if (slot != null && !isLocationUsed(xCoord, yCoord, zCoord)) {
            BuildingSlotBlock b = new BuildingSlotBlock();

            b.schematic = slot;
            b.x = xCoord;
            b.y = yCoord;
            b.z = zCoord;

            b.mode = Mode.Build;
            b.buildStage = 1;

            buildList.add(b);
          }
        }
      }
    }

    iteratorBuild = new BuildingSlotIterator(buildList);
    iteratorClear = new BuildingSlotIterator(clearList);
  }

  private void checkDone() {
    done = buildList.isEmpty() && clearList.isEmpty();
  }

  @Override
  public BuildingSlot reserveNextBlock(Level world) {
    return null;
  }

  @Override
  public BuildingSlot getNextBlock(Level world, AbstractBuilderBlockEntity inv) {
    if (!buildList.isEmpty() || !clearList.isEmpty()) {
      BuildingSlotBlock slot = internalGetNextBlock(world, inv);
      checkDone();

      return slot;
    } else {
      checkDone();
    }

    return null;
  }

  private BuildingSlotBlock internalGetNextBlock(Level world, AbstractBuilderBlockEntity builder) {
    BuildingSlotBlock result = null;

    IInvSlot firstSlotToConsume = null;

    for (IInvSlot invSlot : InventoryIterator.getIterable(builder, Direction.UP)) {
      if (!builder.isBuildingMaterialSlot(invSlot.getIndex())) {
        continue;
      }

      ItemStack stack = invSlot.getStackInSlot();

      if (!stack.isEmpty() && stack.getCount() > 0) {
        firstSlotToConsume = invSlot;
        break;
      }
    }

    // Step 1: Check the cleared
    iteratorClear.startIteration();
    while (iteratorClear.hasNext()) {
      BuildingSlotBlock slot = iteratorClear.next();

      if (slot.buildStage > clearList.getFirst().buildStage) {
        iteratorClear.reset();
        break;
      }

      if (!world.hasChunkAt(new BlockPos(slot.x, slot.y, slot.z))) {
        continue;
      }

      if (canDestroy(builder, context, slot)) {
        if (BlockUtils.isUnbreakableBlock(world, slot.x, slot.y, slot.z)
          || isBlockBreakCanceled(world, slot.x, slot.y, slot.z)
          || BuildCraftAPI.isSoftBlock(world, slot.x, slot.y, slot.z)) {
          iteratorClear.remove();
          markLocationUsed(slot.x, slot.y, slot.z);
        } else {
          consumeEnergyToDestroy(builder, slot);
          createDestroyItems(slot);

          result = slot;
          iteratorClear.remove();
          markLocationUsed(slot.x, slot.y, slot.z);
          break;
        }
      }
    }

    if (result != null) {
      return result;
    }

    // Step 2: Check the built, but only if we have anything to place and enough energy
    if (firstSlotToConsume == null) {
      return null;
    }

    iteratorBuild.startIteration();

    while (iteratorBuild.hasNext()) {
      BuildingSlotBlock slot = iteratorBuild.next();

      if (slot.buildStage > buildList.getFirst().buildStage) {
        iteratorBuild.reset();
        break;
      }

      if (BlockUtils.isUnbreakableBlock(world, slot.x, slot.y, slot.z)
        || isBlockPlaceCanceled(world, slot.x, slot.y, slot.z, slot.schematic)
        || !BuildCraftAPI.isSoftBlock(world, slot.x, slot.y, slot.z)) {
        iteratorBuild.remove();
        markLocationUsed(slot.x, slot.y, slot.z);
      } else if (builder.consumeEnergy(BuilderAPI.BUILD_ENERGY)) {
        slot.addStackConsumed(firstSlotToConsume.decreaseStackInSlot(1));
        result = slot;
        iteratorBuild.remove();
        markLocationUsed(slot.x, slot.y, slot.z);
        break;
      }
    }

    return result;
  }
}
