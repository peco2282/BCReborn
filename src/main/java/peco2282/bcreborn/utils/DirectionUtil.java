package peco2282.bcreborn.utils;

import net.minecraft.core.Direction;

public class DirectionUtil {
  public static int getFrontOffsetX(Direction facing)
  {
    return facing.getAxis() == Direction.Axis.X ? facing.getAxisDirection().getStep() : 0;
  }

  public static int getFrontOffsetY(Direction facing)
  {
    return facing.getAxis() == Direction.Axis.Y ? facing.getAxisDirection().getStep() : 0;
  }

  public static int getFrontOffsetZ(Direction facing)
  {
    return facing.getAxis() == Direction.Axis.Z ? facing.getAxisDirection().getStep() : 0;
  }
}
