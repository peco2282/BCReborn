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

public class Position implements ISerializable {
  public double x, y, z;
  public Direction orientation;

  public Position() {
    x = 0;
    y = 0;
    z = 0;
    orientation = Direction.DOWN;
  }

  public Position(double ci, double cj, double ck) {
    x = ci;
    y = cj;
    z = ck;
    orientation = Direction.DOWN;
  }

  public Position(double ci, double cj, double ck, Direction corientation) {
    x = ci;
    y = cj;
    z = ck;
    orientation = corientation;

    if (orientation == null) {
      orientation = Direction.DOWN;
    }
  }

  public Position(Position p) {
    x = p.x;
    y = p.y;
    z = p.z;
    orientation = p.orientation;
  }

  public Position(CompoundTag nbttagcompound) {
    readFromNBT(nbttagcompound);
  }

  public Position(BlockEntity tile) {
    x = tile.getBlockPos().getX();
    y = tile.getBlockPos().getY();
    z = tile.getBlockPos().getZ();
    orientation = Direction.DOWN;
  }

  public Position(BlockIndex index) {
    x = index.x;
    y = index.y;
    z = index.z;
    orientation = Direction.DOWN;
  }

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

  public void moveLeft(double step) {
    moveRight(-step);
  }

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

  public void moveBackwards(double step) {
    moveForwards(-step);
  }

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

  public void moveDown(double step) {
    moveUp(-step);
  }

  public void writeToNBT(CompoundTag nbttagcompound) {

    nbttagcompound.putDouble("i", x);
    nbttagcompound.putDouble("j", y);
    nbttagcompound.putDouble("k", z);
    nbttagcompound.putByte("orientation", (byte) orientation.get3DDataValue());
  }

  public void readFromNBT(CompoundTag nbttagcompound) {
    x = nbttagcompound.getDouble("i");
    y = nbttagcompound.getDouble("j");
    z = nbttagcompound.getDouble("k");
    orientation = Direction.from3DDataValue(nbttagcompound.getByte("orientation"));
  }

  @Override
  public String toString() {
    return "{" + x + ", " + y + ", " + z + "}";
  }

  public Position min(Position p) {
    return new Position(Math.min(p.x, x), Math.min(p.y, y), Math.min(p.z, z));
  }

  public Position max(Position p) {
    return new Position(Math.max(p.x, x), Math.max(p.y, y), Math.max(p.z, z));
  }

  public boolean isClose(Position newPosition, float f) {
    double dx = x - newPosition.x;
    double dy = y - newPosition.y;
    double dz = z - newPosition.z;

    double sqrDis = dx * dx + dy * dy + dz * dz;

    return !(sqrDis > f * f);
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    x = stream.readDouble();
    y = stream.readDouble();
    z = stream.readDouble();
    orientation = Direction.from3DDataValue(stream.readByte());
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeDouble(x);
    stream.writeDouble(y);
    stream.writeDouble(z);
    stream.writeByte(orientation.get3DDataValue());
  }

  @Override
  public int hashCode() {
    return (51 * (int) x) + (13 * (int) y) + (int) z;
  }

  public BlockPos toBlockPos() {
    return new BlockPos((int) x, (int) y, (int) z);
  }
}
