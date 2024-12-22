package peco2282.bcreborn.core;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkerPlaceHolderTest {

  private final MarkerPlaceHolder holder = new MarkerPlaceHolder(new BlockPos(1, 1, 1), new BlockPos(10, 10, 10));

  @Test
  void inRange() {
    assertTrue(holder.inRange(new BlockPos(1, 1, 1)));
  }

  @Test
  void getCorner() {
    System.out.println(holder.getEdges());
  }

  @Test
  void add() {
    assertFalse(holder.add(new BlockPos(1, 1, 1)));
    assertTrue(holder.add(new BlockPos(15, 15, 15)));
  }
}