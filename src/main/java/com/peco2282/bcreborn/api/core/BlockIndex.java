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
package com.peco2282.bcreborn.api.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A container for integer-based block positions that implements {@link Comparable}.
 */
public class BlockIndex implements Comparable<BlockIndex> {
  public int x;
  public int y;
  public int z;

  /**
   * Default constructor.
   */
  public BlockIndex() {
  }

  /**
   * Creates an index for a block at the specified coordinates.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @param z The z-coordinate.
   */
  public BlockIndex(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Creates an index from an NBT tag.
   *
   * @param c The NBT tag to read from.
   */
  public BlockIndex(CompoundTag c) {
    this.x = c.getInt("i");
    this.y = c.getInt("j");
    this.z = c.getInt("k");
  }

  /**
   * Creates an index from an entity's position.
   *
   * @param entity The entity.
   */
  public BlockIndex(Entity entity) {
    x = (int) Math.floor(entity.getX());
    y = (int) Math.floor(entity.getY());
    z = (int) Math.floor(entity.getZ());
  }

  /**
   * Creates an index from a block entity's position.
   *
   * @param entity The block entity.
   */
  public BlockIndex(BlockEntity entity) {
    BlockPos pos = entity.getBlockPos();
    this.x = pos.getX();
    this.y = pos.getY();
    this.z = pos.getZ();
  }

  /**
   * Provides a deterministic and complete ordering of block positions.
   * Order is X, then Z, then Y.
   */
  @Override
  public int compareTo(BlockIndex o) {
    if (o.x < x) {
      return 1;
    } else if (o.x > x) {
      return -1;
    } else if (o.z < z) {
      return 1;
    } else if (o.z > z) {
      return -1;
    } else return Integer.compare(y, o.y);
  }

  /**
   * Writes the index to an NBT tag.
   *
   * @param c The NBT tag to write to.
   */
  public void writeTo(CompoundTag c) {
    c.putInt("i", x);
    c.putInt("j", y);
    c.putInt("k", z);
  }

  /**
   * Gets the block at this index in the specified world.
   *
   * @param world The world.
   * @return The {@link Block}.
   */
  public Block getBlock(Level world) {
    return world.getBlockState(new BlockPos(x, y, z)).getBlock();
  }

  @Override
  public String toString() {
    return "{" + x + ", " + y + ", " + z + "}";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BlockIndex b) {

      return b.x == x && b.y == y && b.z == z;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return (x * 37 + y) * 37 + z;
  }

  /**
   * Checks if this index is adjacent to another index.
   *
   * @param blockIndex The other index.
   * @return True if they are next to each other.
   */
  public boolean nextTo(BlockIndex blockIndex) {
    return (Math.abs(blockIndex.x - x) <= 1 && blockIndex.y == y && blockIndex.z == z)
      || (blockIndex.x == x && Math.abs(blockIndex.y - y) <= 1 && blockIndex.z == z)
      || (blockIndex.x == x && blockIndex.y == y && Math.abs(blockIndex.z - z) <= 1);
  }

  /**
   * Converts this index to a {@link BlockPos}.
   *
   * @return The {@link BlockPos}.
   */
  public BlockPos toBlockPos() {
    return new BlockPos(x, y, z);
  }
}
