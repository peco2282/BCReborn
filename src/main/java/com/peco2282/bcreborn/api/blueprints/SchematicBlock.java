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
package com.peco2282.bcreborn.api.blueprints;

import com.peco2282.bcreborn.api.core.BlockIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * Schematic implementation for a standard block.
 */
public class SchematicBlock extends SchematicBlockBase {

  /**
   * Relative indexes used to identify neighbor positions.
   * 0: UP, 1: DOWN, 2: NORTH, 3: SOUTH, 4: WEST, 5: EAST
   */
  public static final BlockIndex[] RELATIVE_INDEXES = new BlockIndex[]{
    new BlockIndex(0, -1, 0),
    new BlockIndex(0, 1, 0),
    new BlockIndex(0, 0, -1),
    new BlockIndex(0, 0, 1),
    new BlockIndex(-1, 0, 0),
    new BlockIndex(1, 0, 0),
  };

  /**
   * The orientation of the block.
   */
  public Direction facing = Direction.UP;

  /**
   * The block state represented by this schematic.
   */
  public BlockState state = Blocks.AIR.defaultBlockState();

  /**
   * The default building permission for this block.
   */
  public BuildingPermission defaultPermission = BuildingPermission.ALL;

  /**
   * Requirements for the block when stored in a blueprint.
   */
  public ItemStack[] storedRequirements = new ItemStack[0];

  private boolean doNotUse = false;

  /**
   * Helper method to check if the block at the specified position matches one of the given blocks.
   *
   * @param context The builder context.
   * @param x       The x-coordinate.
   * @param y       The y-coordinate.
   * @param z       The z-coordinate.
   * @param blocks  The blocks to check against.
   * @return True if it matches.
   */
  protected static boolean isBlock(IBuilderContext context, int x, int y, int z, Block... blocks) {
    BlockState state = context.world().getBlockState(new BlockPos(x, y, z));
    for (Block block : blocks) {
      if (state.is(block)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    if (state != null) {
      if (storedRequirements.length != 0) {
        Collections.addAll(requirements, storedRequirements);
      } else {
        requirements.add(new ItemStack(state.getBlock()));
      }
    }
  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    super.initializeFromObjectAt(context, x, y, z);
    BlockPos pos = new BlockPos(x, y, z);
    state = context.world().getBlockState(pos);
  }

  @Override
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    return state != null && state == context.world().getBlockState(new BlockPos(x, y, z));
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    super.placeInWorld(context, x, y, z, stacks);

    this.setBlockInWorld(context, x, y, z);
  }

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {
    super.storeRequirements(context, x, y, z);

    BlockPos pos = new BlockPos(x, y, z);
    BlockState worldState = context.world().getBlockState(pos);
    List<ItemStack> req = Block.getDrops(
      worldState,
      (ServerLevel) context.world(),
      pos,
      context.world().getBlockEntity(pos));

    storedRequirements = req.toArray(new ItemStack[0]);
  }

  @Override
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
    super.writeSchematicToNBT(nbt, registry);

    writeBlockToNBT(nbt, registry);
    writeRequirementsToNBT(nbt, registry);
  }

  @Override
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
    super.readSchematicFromNBT(nbt, registry);

    readBlockFromNBT(nbt, registry);
    if (!doNotUse()) {
      readRequirementsFromNBT(nbt, registry);
    }
  }

  /**
   * Gets a set of relative block indexes that must be built before this block.
   *
   * @param context The builder context.
   * @return A set of {@link BlockIndex} prerequisites.
   */
  public Set<BlockIndex> getPrerequisiteBlocks(IBuilderContext context) {
    Set<BlockIndex> indexes = new HashSet<>();
    if (state.getBlock() instanceof FallingBlock) {
      indexes.add(RELATIVE_INDEXES[1]); // DOWN index
    }
    return indexes;
  }

  @Override
  public BuildingStage getBuildStage() {
    if (state.getBlock() instanceof LiquidBlock) {
      return BuildingStage.EXPANDING;
    } else {
      return BuildingStage.STANDALONE;
    }
  }

  @Override
  public BuildingPermission getBuildingPermission() {
    return defaultPermission;
  }

  /**
   * Utility function to set the block in the world.
   *
   * @param context The builder context.
   * @param x       The x-coordinate.
   * @param y       The y-coordinate.
   * @param z       The z-coordinate.
   */
  protected void setBlockInWorld(IBuilderContext context, int x, int y, int z) {
    if (state != null) {
      context.world().setBlock(new BlockPos(x, y, z), state, 3);
    }
  }

  /**
   * @return True if this schematic should not be used (e.g., mapping failed).
   */
  public boolean doNotUse() {
    return doNotUse;
  }

  /**
   * Reads the block state from NBT.
   *
   * @param nbt      The NBT tag.
   * @param registry The mapping registry.
   */
  protected void readBlockFromNBT(CompoundTag nbt, MappingRegistry registry) {
    try {
      state = registry.readBlockStateFromNBT(nbt);
    } catch (MappingNotFoundException e) {
      doNotUse = true;
    }
  }

  /**
   * Reads requirements from NBT.
   *
   * @param nbt      The NBT tag.
   * @param registry The mapping registry.
   */
  protected void readRequirementsFromNBT(CompoundTag nbt, MappingRegistry registry) {
    if (nbt.contains("rq")) {
      ListTag rq = nbt.getList("rq", Tag.TAG_COMPOUND);

      ArrayList<ItemStack> rqs = new ArrayList<>();
      for (int i = 0; i < rq.size(); ++i) {
        try {
          CompoundTag sub = rq.getCompound(i);
          if (sub.getInt("id") >= 0) {
            registry.stackToWorld(sub);
            ItemStack stack = ItemStack.of(sub);
            if (!stack.isEmpty()) {
              rqs.add(stack);
            }
          } else {
            defaultPermission = BuildingPermission.CREATIVE_ONLY;
          }
        } catch (MappingNotFoundException e) {
          defaultPermission = BuildingPermission.CREATIVE_ONLY;
        } catch (Throwable t) {
          t.printStackTrace();
          defaultPermission = BuildingPermission.CREATIVE_ONLY;
        }
      }

      storedRequirements = rqs.toArray(new ItemStack[0]);
    } else {
      storedRequirements = new ItemStack[0];
    }
  }

  /**
   * Writes the block state to NBT.
   *
   * @param nbt      The NBT tag.
   * @param registry The mapping registry.
   */
  protected void writeBlockToNBT(CompoundTag nbt, MappingRegistry registry) {
    registry.writeBlockStateToNBT(nbt, state);
  }

  /**
   * Writes requirements to NBT.
   *
   * @param nbt      The NBT tag.
   * @param registry The mapping registry.
   */
  protected void writeRequirementsToNBT(CompoundTag nbt, MappingRegistry registry) {
    if (storedRequirements.length > 0) {
      ListTag rq = new ListTag();

      for (ItemStack stack : storedRequirements) {
        CompoundTag sub = new CompoundTag();
        stack.save(sub);
        registry.stackToRegistry(sub);
        rq.add(sub);
      }

      nbt.put("rq", rq);
    }
  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    if (state != null) {
      state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
    }
  }
}
