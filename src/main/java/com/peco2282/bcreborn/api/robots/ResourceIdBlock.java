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
package com.peco2282.bcreborn.api.robots;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

/**
 * Resource identifier for a specific block and side.
 */
public class ResourceIdBlock extends ResourceId<ResourceIdBlock> {
  /**
   * The block position.
   */
  public BlockPos index = BlockPos.ZERO;

  /**
   * The side of the block.
   */
  public Direction side = null;

  /**
   * Default constructor for NBT loading.
   */
  public ResourceIdBlock() {
    super(ResourceIdType.BLOCK);
  }

  /**
   * Constructs a ResourceIdBlock for the specified block position.
   *
   * @param iIndex The block position.
   */
  public ResourceIdBlock(BlockPos iIndex) {
    this();
    index = iIndex;
  }

  /**
   * Constructs a ResourceIdBlock for the specified block entity.
   *
   * @param tile The block entity.
   */
  public ResourceIdBlock(BlockEntity tile) {
    this();
    index = tile.getBlockPos();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    ResourceIdBlock compareId = (ResourceIdBlock) obj;
    return index.equals(compareId.index) && side == compareId.side;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, side);
  }

  @Override
  public void writeToNBT(CompoundTag nbt) {
    super.writeToNBT(nbt);
    CompoundTag indexNBT = new CompoundTag();
    indexNBT.putInt("x", index.getX());
    indexNBT.putInt("y", index.getY());
    indexNBT.putInt("z", index.getZ());
    nbt.put("index", indexNBT);
    nbt.putByte("side", (byte) (side != null ? side.ordinal() : 0));
  }

  @Override
  protected void readFromNBT(CompoundTag nbt) {
    super.readFromNBT(nbt);
    CompoundTag indexNBT = nbt.getCompound("index");
    index = new BlockPos(indexNBT.getInt("x"), indexNBT.getInt("y"), indexNBT.getInt("z"));
    side = Direction.values()[nbt.getByte("side")];
  }
}
