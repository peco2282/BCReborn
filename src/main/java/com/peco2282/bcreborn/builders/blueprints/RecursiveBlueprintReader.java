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
package com.peco2282.bcreborn.builders.blueprints;


import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.api.blueprints.Translation;
import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.builders.block.entity.ArchitectBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.ConstructionMarkerBlockEntity;
import com.peco2282.bcreborn.builders.item.BlueprintItem;
import com.peco2282.bcreborn.builders.item.BlueprintStandardItem;
import com.peco2282.bcreborn.builders.item.BlueprintTemplateItem;
import com.peco2282.bcreborn.common.blueprint.Blueprint;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.BptContext;
import com.peco2282.bcreborn.common.blueprint.Template;
import com.peco2282.bcreborn.common.utils.BlockScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RecursiveBlueprintReader {

  private static final int SCANNER_ITERATION = 100;

  public ArchitectBlockEntity architect;

  private BlockScanner blockScanner;
  private BlueprintBase writingBlueprint;
  private BptContext writingContext;

  private int subIndex = 0;
  private RecursiveBlueprintReader currentSubReader;
  private float computingTime = 0;

  private boolean done = false;

  private BlueprintBase parentBlueprint;

  public RecursiveBlueprintReader(ArchitectBlockEntity iArchitect) {
    architect = iArchitect;
    ItemStack stack = architect.getItem(0);

    if (!stack.isEmpty() && stack.getItem() instanceof BlueprintItem && architect.getBox().isInitialized()) {
      blockScanner = new BlockScanner(architect.getBox(), architect.getLevel(), SCANNER_ITERATION);

      if (stack.getItem() instanceof BlueprintStandardItem) {
        writingBlueprint = new Blueprint(architect.getBox().sizeX(), architect.getBox().sizeY(), architect.getBox().sizeZ());
      } else if (stack.getItem() instanceof BlueprintTemplateItem) {
        writingBlueprint = new Template(architect.getBox().sizeX(), architect.getBox().sizeY(), architect.getBox().sizeZ());
      }

      writingContext = writingBlueprint.getContext(architect.getLevel(), architect.getBox());
      writingContext.readConfiguration = architect.readConfiguration;

      writingBlueprint.id.name = architect.name;
      writingBlueprint.author = architect.currentAuthorName;
      writingBlueprint.anchorX = architect.getBlockPos().getX() - architect.getBox().xMin;
      writingBlueprint.anchorY = architect.getBlockPos().getY() - architect.getBox().yMin;
      writingBlueprint.anchorZ = architect.getBlockPos().getZ() - architect.getBox().zMin;
    } else {
      done = true;
    }
  }

  protected RecursiveBlueprintReader(ArchitectBlockEntity iArchitect, BlueprintBase iParentBlueprint) {
    parentBlueprint = iParentBlueprint;
    architect = iArchitect;

    if (architect.getBox().isInitialized()) {
      blockScanner = new BlockScanner(architect.getBox(), architect.getLevel(), SCANNER_ITERATION);

      if (parentBlueprint instanceof Blueprint) {
        writingBlueprint = new Blueprint(architect.getBox().sizeX(), architect.getBox().sizeY(), architect.getBox().sizeZ());
      } else if (parentBlueprint instanceof Template) {
        writingBlueprint = new Template(architect.getBox().sizeX(), architect.getBox().sizeY(), architect.getBox().sizeZ());
      }

      writingContext = writingBlueprint.getContext(architect.getLevel(), architect.getBox());
      writingContext.readConfiguration = architect.readConfiguration;

      writingBlueprint.id.name = architect.getName().getString();
      writingBlueprint.author = architect.currentAuthorName;
      writingBlueprint.anchorX = architect.getBlockPos().getX() - architect.getBox().xMin;
      writingBlueprint.anchorY = architect.getBlockPos().getY() - architect.box.yMin;
      writingBlueprint.anchorZ = architect.getBlockPos().getZ() - architect.getBox().zMin;
    }
  }

  public void iterate() {
    if (done) {
    } else if (currentSubReader == null && subIndex < architect.subBlueprints.size()) {
      BlockIndex subBlock = architect.subBlueprints.get(subIndex);

      BlockEntity subTile = architect.getLevel().getBlockEntity(new BlockPos(subBlock.x, subBlock.y,
        subBlock.z));

      if (subTile instanceof ArchitectBlockEntity subArchitect) {
        currentSubReader = new RecursiveBlueprintReader(subArchitect, writingBlueprint);
      } else if (subTile instanceof ConstructionMarkerBlockEntity || subTile instanceof BuilderBlockEntity) {
        BlueprintBase blueprint = null;
        Direction orientation = Direction.EAST;

        if (subTile instanceof ConstructionMarkerBlockEntity marker) {
          blueprint = BlueprintItem.loadBlueprint(marker.getBlueprint());
          orientation = marker.getDirection();
        } else {
          BuilderBlockEntity builder = (BuilderBlockEntity) subTile;
          blueprint = BlueprintItem.loadBlueprint(builder.getItem(0));
          orientation = Direction.values()[architect.getLevel().getBlockState(new BlockPos(subBlock.x, subBlock.y,
            subBlock.z)).getValue(BlockStateProperties.HORIZONTAL_FACING).ordinal()].getOpposite();
        }

        if (blueprint != null) {
          writingBlueprint.addSubBlueprint(
            blueprint,
            subTile.getBlockPos().getX() - architect.getBox().xMin,
            subTile.getBlockPos().getY() - architect.getBox().yMin,
            subTile.getBlockPos().getZ() - architect.getBox().zMin,
            orientation);
        }

        subIndex++;
      } else {
        subIndex++;
      }
    } else if (currentSubReader != null) {
      currentSubReader.iterate();

      if (currentSubReader.isDone()) {
        writingBlueprint.addSubBlueprint
          (currentSubReader.getBlueprint(),
            currentSubReader.architect.getBlockPos().getX() - architect.getBox().xMin,
            currentSubReader.architect.getBlockPos().getY() - architect.getBox().yMin,
            currentSubReader.architect.getBlockPos().getZ() - architect.getBox().zMin,
            architect.getLevel().getBlockState(currentSubReader.architect.getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());

        currentSubReader = null;
        subIndex++;
      }
    } else if (blockScanner != null && blockScanner.blocksLeft() != 0) {
      for (BlockIndex index : blockScanner) {
        writingBlueprint.readFromWorld(writingContext, architect,
          index.x, index.y, index.z);
      }

      computingTime = 1 - (float) blockScanner.blocksLeft()
        / (float) blockScanner.totalBlocks();

      if (blockScanner.blocksLeft() == 0) {
        writingBlueprint.readEntitiesFromWorld(writingContext, architect);

        Translation transform = new Translation();

        transform.x = -writingContext.surroundingBox().pMin().x;
        transform.y = -writingContext.surroundingBox().pMin().y;
        transform.z = -writingContext.surroundingBox().pMin().z;

        writingBlueprint.translateToBlueprint(transform);

        Direction o = architect.getLevel().getBlockState(architect.getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();

        writingBlueprint.rotate = architect.readConfiguration.rotate;
        writingBlueprint.excavate = architect.readConfiguration.excavate;

        if (writingBlueprint.rotate) {
          if (o == Direction.EAST) {
            // Do nothing
          } else if (o == Direction.SOUTH) {
            writingBlueprint.rotateLeft(writingContext);
            writingBlueprint.rotateLeft(writingContext);
            writingBlueprint.rotateLeft(writingContext);
          } else if (o == Direction.WEST) {
            writingBlueprint.rotateLeft(writingContext);
            writingBlueprint.rotateLeft(writingContext);
          } else if (o == Direction.NORTH) {
            writingBlueprint.rotateLeft(writingContext);
          }
        }
      }
    } else if (blockScanner != null) {
      createBlueprint();

      done = true;
    }
  }

  private BlueprintBase getBlueprint() {
    return writingBlueprint;
  }

  public void createBlueprint() {
    writingBlueprint.id.name = architect.getName().getString();
    writingBlueprint.author = architect.currentAuthorName;
    CompoundTag nbt = writingBlueprint.getNBT();
    BCRebornBuilders.getServerDB().add(writingBlueprint.id, nbt);

    if (parentBlueprint == null) {
      architect.storeBlueprintStack(writingBlueprint.getStack());
    }
  }

  public boolean isDone() {
    return done;
  }

  public float getComputingProgressScaled() {
    float sections = architect.subBlueprints.size() + 1;

    float processed = subIndex;

    if (currentSubReader != null) {
      processed += currentSubReader.getComputingProgressScaled();
    }

    processed += computingTime;

    return processed / sections;
  }
}
