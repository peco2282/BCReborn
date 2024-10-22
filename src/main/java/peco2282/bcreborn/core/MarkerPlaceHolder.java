package peco2282.bcreborn.core;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;

import java.util.List;

public class MarkerPlaceHolder {
  private BlockPos start;
  private BlockPos end;
  private int xStart;
  private int yStart;
  private int zStart;
  private int xEnd;
  private int yEnd;
  private int zEnd;

  public MarkerPlaceHolder(final BlockPos start, final BlockPos end) {
    this.xStart = Math.min(end.getX(), start.getX());
    this.yStart = Math.min(end.getY(), start.getY());
    this.zStart = Math.min(end.getZ(), start.getZ());
    this.xEnd = Math.max(end.getX(), start.getX());
    this.yEnd = Math.max(end.getY(), start.getY());
    this.zEnd = Math.max(end.getZ(), start.getZ());
    this.start = new BlockPos(this.xStart, this.yStart, this.zStart);
    this.end = new BlockPos(this.xEnd, this.yEnd, this.zEnd);
  }

  public boolean inRange(final BlockPos pos) {
    boolean x = xStart <= pos.getX() && xEnd >= pos.getX();
    boolean y = yStart <= pos.getY() && yEnd >= pos.getY();
    boolean z = zStart <= pos.getZ() && zEnd >= pos.getZ();
    return x && y && z;
  }

  public Corner getCorner() {
    return new Corner(
        new BlockPos(xStart, yStart, zStart),
        new BlockPos(xStart, yStart, zEnd),
        new BlockPos(xStart, yEnd, zEnd),
        new BlockPos(xStart, yEnd, zStart),
        new BlockPos(xEnd, yStart, zStart),
        new BlockPos(xEnd, yStart, zEnd),
        new BlockPos(xEnd, yEnd, zEnd),
        new BlockPos(xEnd, yEnd, zStart)
    );
  }

  public boolean add(BlockPos pos) {
    if (this.inRange(pos)) return false;
    this.xStart = Math.min(pos.getX(), this.xStart);
    this.yStart = Math.min(pos.getY(), this.yStart);
    this.zStart = Math.min(pos.getZ(), this.zStart);
    this.xEnd = Math.max(pos.getX(), this.xEnd);
    this.yEnd = Math.max(pos.getY(), this.yEnd);
    this.zEnd = Math.max(pos.getZ(), this.zEnd);
    this.start = new BlockPos(this.xStart, this.yStart, this.zStart);
    this.end = new BlockPos(this.xEnd, this.yEnd, this.zEnd);
    return true;
  }

  @SuppressWarnings("unused")
  public MarkerPlaceHolder copy() {
    return new MarkerPlaceHolder(
        new BlockPos(this.xStart, this.yStart, this.zStart),
        new BlockPos(this.xEnd, this.yEnd, this.zEnd)
    );
  }

  public record Corner(
      BlockPos corner1,
      BlockPos corner2,
      BlockPos corner3,
      BlockPos corner4,
      BlockPos corner5,
      BlockPos corner6,
      BlockPos corner7,
      BlockPos corner8
  ) {
    public List<List<BlockPos>> renderList() {
      List<BlockPos> f = ImmutableList.<BlockPos>builder()
          .add(corner1())
          .add(corner2())
          .add(corner3())
          .add(corner4())
          .build();
      List<BlockPos> s = ImmutableList.<BlockPos>builder()
          .add(corner5())
          .add(corner6())
          .add(corner7())
          .add(corner8())
          .build();

      // edges
      List<BlockPos> e1 = ImmutableList.<BlockPos>builder()
          .add(corner1())
          .add(corner5())
          .build();
      List<BlockPos> e2 = ImmutableList.<BlockPos>builder()
          .add(corner2())
          .add(corner6())
          .build();
      List<BlockPos> e3 = ImmutableList.<BlockPos>builder()
          .add(corner3())
          .add(corner7())
          .build();
      List<BlockPos> e4 = ImmutableList.<BlockPos>builder()
          .add(corner4())
          .add(corner8())
          .build();
      return ImmutableList.of(f, s, e1, e2, e3, e4);
    }

    @Override
    public String toString() {
      return String.join(", ", renderList().stream().flatMap(List::stream).map(BlockPos::toString).toList());
    }
  }
}
