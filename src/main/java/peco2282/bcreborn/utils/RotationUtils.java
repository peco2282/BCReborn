package peco2282.bcreborn.utils;

import net.minecraft.core.Direction;

public class RotationUtils {
  public static Direction rotateAll(Direction facing) {
    return switch (facing) {
      case NORTH -> Direction.EAST;
      case EAST -> Direction.SOUTH;
      case SOUTH -> Direction.WEST;
      case WEST -> Direction.UP;
      case UP -> Direction.DOWN;
      case DOWN -> Direction.NORTH;
    };
  }
}
