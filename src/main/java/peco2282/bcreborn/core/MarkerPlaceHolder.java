package peco2282.bcreborn.core;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;

public class MarkerPlaceHolder {
  public static final Codec<MarkerPlaceHolder> CODEC = RecordCodecBuilder
      .create(instance -> instance.group(
          BlockPos.CODEC.fieldOf("start").forGetter(MarkerPlaceHolder::getStart),
          BlockPos.CODEC.fieldOf("end").forGetter(MarkerPlaceHolder::getEnd)
      ).apply(instance, MarkerPlaceHolder::new));
  private BlockPos start;
  private BlockPos end;
  private BlockPos base;
  private BlockPos last;
  public int xStart;
  public int yStart;
  public int zStart;
  public int xEnd;
  public int yEnd;
  public int zEnd;

  public MarkerPlaceHolder(BlockPos start) {
    this(start, start);
  }

  public MarkerPlaceHolder(final @NotNull BlockPos start, final @NotNull BlockPos end) {
    this.xStart = Math.min(end.getX(), start.getX());
    this.yStart = Math.min(end.getY(), start.getY());
    this.zStart = Math.min(end.getZ(), start.getZ());
    this.xEnd = Math.max(end.getX(), start.getX());
    this.yEnd = Math.max(end.getY(), start.getY());
    this.zEnd = Math.max(end.getZ(), start.getZ());
    this.start = new BlockPos(this.xStart, this.yStart, this.zStart);
    this.end = new BlockPos(this.xEnd, this.yEnd, this.zEnd);
    this.base = this.start;
    this.last = this.end;
  }

  public boolean inRange(final @NotNull BlockPos pos) {
    boolean x = xStart <= pos.getX() && xEnd >= pos.getX();
    boolean y = yStart <= pos.getY() && yEnd >= pos.getY();
    boolean z = zStart <= pos.getZ() && zEnd >= pos.getZ();
    return x && y && z;
  }

  public Optional<Corner> getCorner() {
    return canRender() ?
        Optional.empty() :
        Optional.of(
            new Corner(
                new BlockPos(xStart, yStart, zStart),
                new BlockPos(xStart, yStart, zEnd),
                new BlockPos(xStart, yEnd, zEnd),
                new BlockPos(xStart, yEnd, zStart),
                new BlockPos(xEnd, yStart, zStart),
                new BlockPos(xEnd, yStart, zEnd),
                new BlockPos(xEnd, yEnd, zEnd),
                new BlockPos(xEnd, yEnd, zStart)
            )
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
    this.last = pos;
    return true;
  }

  @SuppressWarnings("unused")
  public MarkerPlaceHolder copy() {
    return new MarkerPlaceHolder(
        new BlockPos(this.xStart, this.yStart, this.zStart),
        new BlockPos(this.xEnd, this.yEnd, this.zEnd)
    );
  }

  public BlockPos getStart() {
    return start;
  }

  public BlockPos getEnd() {
    return end;
  }

  public boolean canRender() {
    return this.start != this.end;
  }

  public int rangeX() {
    return this.xEnd - this.xStart;
  }

  public int rangeY() {
    return this.yEnd - this.yStart;
  }

  public int rangeZ() {
    return this.zEnd - this.zStart;
  }

  public Direction directionX() {
    return (last.getX() - base.getX()) > 0 ? Direction.EAST : Direction.WEST;
  }
  public Direction directionY() {
    return (last.getY() - base.getY()) > 0 ? Direction.UP : Direction.DOWN;
  }
  public Direction directionZ() {
    return (last.getZ() - base.getZ()) > 0 ? Direction.SOUTH : Direction.NORTH;
  }

  public int distanceX() {
    return last.getX() - base.getX();
  }
  public int distanceY() {
    return last.getY() - base.getY();
  }
  public int distanceZ() {
    return last.getZ() - base.getZ();
  }

  @Override
  public String toString() {
    return "start: %s, end: %s x: %d y: %d z: %d".formatted(start, end, rangeX(), rangeY(), rangeZ());
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
    public @NotNull @Unmodifiable List<List<BlockPos>> renderList() {
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

    @Contract(" -> new")
    @Override
    public @NotNull String toString() {
      return String.join(", ", renderList().stream().flatMap(List::stream).map(BlockPos::toString).toList());
    }
  }
}
