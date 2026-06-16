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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Represents a 3D position in world space with an associated orientation.
 * <p>
 * This class provides methods for movement relative to orientation and serialization
 * to/from NBT and network packets.
 */
public class Position implements IBufferSerializable {
  public double x, y, z;
  public Direction orientation;

  /**
   * Creates a new Position at coordinates (0, 0, 0) with DOWN orientation.
   */
  public Position() {
    x = 0;
    y = 0;
    z = 0;
    orientation = Direction.DOWN;
  }

  /**
   * Creates a new Position with the specified coordinates and DOWN orientation.
   *
   * @param ci The x coordinate.
   * @param cj The y coordinate.
   * @param ck The z coordinate.
   */
  public Position(double ci, double cj, double ck) {
    x = ci;
    y = cj;
    z = ck;
    orientation = Direction.DOWN;
  }

  /**
   * Creates a new Position with the specified coordinates and orientation.
   *
   * @param ci           The x coordinate.
   * @param cj           The y coordinate.
   * @param ck           The z coordinate.
   * @param corientation The orientation direction (defaults to DOWN if null).
   */
  public Position(double ci, double cj, double ck, Direction corientation) {
    x = ci;
    y = cj;
    z = ck;
    orientation = corientation;

    if (orientation == null) {
      orientation = Direction.DOWN;
    }
  }

  /**
   * Creates a copy of the specified Position.
   *
   * @param p The position to copy.
   */
  public Position(Position p) {
    x = p.x;
    y = p.y;
    z = p.z;
    orientation = p.orientation;
  }

  /**
   * Creates a new Position by deserializing from an NBT tag.
   *
   * @param nbttagcompound The NBT tag to read from.
   */
  public Position(CompoundTag nbttagcompound) {
    readFromNBT(nbttagcompound);
  }

  /**
   * Creates a new Position from a BlockEntity's position with DOWN orientation.
   *
   * @param tile The block entity whose position to use.
   */
  public Position(BlockEntity tile) {
    x = tile.getBlockPos().getX();
    y = tile.getBlockPos().getY();
    z = tile.getBlockPos().getZ();
    orientation = Direction.DOWN;
  }

  /**
   * Creates a new Position from a BlockIndex with DOWN orientation.
   *
   * @param index The block index whose coordinates to use.
   */
  public Position(BlockIndex index) {
    x = index.x;
    y = index.y;
    z = index.z;
    orientation = Direction.DOWN;
  }

  /**
   * Moves the position to the right relative to the current orientation.
   *
   * @param step The distance to move (negative values move left).
   */
  public void moveRight(double step) {
    switch (orientation) {
      case SOUTH:
        x = x - step;
        break;
      case NORTH:
        x = x + step;
        break;
      case EAST:
        z = z + step;
        break;
      case WEST:
        z = z - step;
        break;
      default:
    }
  }

  /**
   * Moves the position to the left relative to the current orientation.
   *
   * @param step The distance to move (negative values move right).
   */
  public void moveLeft(double step) {
    moveRight(-step);
  }

  /**
   * Moves the position forwards relative to the current orientation.
   *
   * @param step The distance to move (negative values move backwards).
   */
  public void moveForwards(double step) {
    switch (orientation) {
      case UP:
        y = y + step;
        break;
      case DOWN:
        y = y - step;
        break;
      case SOUTH:
        z = z + step;
        break;
      case NORTH:
        z = z - step;
        break;
      case EAST:
        x = x + step;
        break;
      case WEST:
        x = x - step;
        break;
      default:
    }
  }

  /**
   * Moves the position backwards relative to the current orientation.
   *
   * @param step The distance to move (negative values move forwards).
   */
  public void moveBackwards(double step) {
    moveForwards(-step);
  }

  /**
   * Moves the position upwards (only for horizontal orientations).
   *
   * @param step The distance to move (negative values move down).
   */
  public void moveUp(double step) {
    switch (orientation) {
      case SOUTH:
      case NORTH:
      case EAST:
      case WEST:
        y = y + step;
        break;
      default:
    }

  }

  /**
   * Moves the position downwards (only for horizontal orientations).
   *
   * @param step The distance to move (negative values move up).
   */
  public void moveDown(double step) {
    moveUp(-step);
  }

  /**
   * Serializes this position to an NBT tag.
   *
   * @param nbttagcompound The NBT tag to write to.
   */
  public void writeToNBT(CompoundTag nbttagcompound) {

    nbttagcompound.putDouble("i", x);
    nbttagcompound.putDouble("j", y);
    nbttagcompound.putDouble("k", z);
    nbttagcompound.putByte("orientation", (byte) orientation.get3DDataValue());
  }

  /**
   * Deserializes this position from an NBT tag.
   *
   * @param nbttagcompound The NBT tag to read from.
   */
  public void readFromNBT(CompoundTag nbttagcompound) {
    x = nbttagcompound.getDouble("i");
    y = nbttagcompound.getDouble("j");
    z = nbttagcompound.getDouble("k");
    orientation = Direction.from3DDataValue(nbttagcompound.getByte("orientation"));
  }

  /**
   * Returns a string representation of this position in the format {x, y, z}.
   *
   * @return A string representation of the coordinates.
   */
  @Override
  public String toString() {
    return "{" + x + ", " + y + ", " + z + "}";
  }

  /**
   * Returns a new Position with the minimum x, y, and z values from this and the given position.
   *
   * @param p The position to compare with.
   * @return A new Position with minimum coordinates.
   */
  public Position min(Position p) {
    return new Position(Math.min(p.x, x), Math.min(p.y, y), Math.min(p.z, z));
  }

  /**
   * Returns a new Position with the maximum x, y, and z values from this and the given position.
   *
   * @param p The position to compare with.
   * @return A new Position with maximum coordinates.
   */
  public Position max(Position p) {
    return new Position(Math.max(p.x, x), Math.max(p.y, y), Math.max(p.z, z));
  }

  /**
   * Checks if this position is within a specified distance of another position.
   *
   * @param newPosition The position to compare with.
   * @param f           The maximum distance threshold.
   * @return True if the distance is less than or equal to the threshold.
   */
  public boolean isClose(Position newPosition, float f) {
    double dx = x - newPosition.x;
    double dy = y - newPosition.y;
    double dz = z - newPosition.z;

    double sqrDis = dx * dx + dy * dy + dz * dz;

    return !(sqrDis > f * f);
  }

  /**
   * Deserializes this position from a network packet buffer.
   *
   * @param stream The buffer to read from.
   */
  @Override
  public void readData(FriendlyByteBuf stream) {
    x = stream.readDouble();
    y = stream.readDouble();
    z = stream.readDouble();
    orientation = Direction.from3DDataValue(stream.readByte());
  }

  /**
   * Serializes this position to a network packet buffer.
   *
   * @param stream The buffer to write to.
   */
  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeDouble(x);
    stream.writeDouble(y);
    stream.writeDouble(z);
    stream.writeByte(orientation.get3DDataValue());
  }

  /**
   * Returns a hash code based on the integer parts of the coordinates.
   *
   * @return The hash code value.
   */
  @Override
  public int hashCode() {
    return (51 * (int) x) + (13 * (int) y) + (int) z;
  }

  /**
   * Converts this position to a BlockPos by truncating the coordinates to integers.
   *
   * @return A BlockPos representing this position's block coordinates.
   */
  public BlockPos toBlockPos() {
    return new BlockPos((int) x, (int) y, (int) z);
  }
}
