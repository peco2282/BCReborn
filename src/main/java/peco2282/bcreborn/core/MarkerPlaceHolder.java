/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class MarkerPlaceHolder {
  public static final Codec<MarkerPlaceHolder> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      BlockPos.CODEC.fieldOf("start").forGetter(MarkerPlaceHolder::getStart),
                      BlockPos.CODEC.fieldOf("end").forGetter(MarkerPlaceHolder::getEnd))
                  .apply(instance, MarkerPlaceHolder::new));
  public int xStart;
  public int yStart;
  public int zStart;
  public int xEnd;
  public int yEnd;
  public int zEnd;
  private BlockPos start;
  private BlockPos end;
  private BlockPos base;
  private BlockPos last;

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

  public XYZ getEdges() {
    List<Edge> edgesX = new ArrayList<>();
    List<Edge> edgesY = new ArrayList<>();
    List<Edge> edgesZ = new ArrayList<>();

    // 各頂点を定義
    BlockPos v1 = new BlockPos(start.getX(), start.getY(), start.getZ());
    BlockPos v2 = new BlockPos(end.getX(), start.getY(), start.getZ());
    BlockPos v3 = new BlockPos(start.getX(), end.getY(), start.getZ());
    BlockPos v4 = new BlockPos(start.getX(), start.getY(), end.getZ());
    BlockPos v5 = new BlockPos(end.getX(), end.getY(), start.getZ());
    BlockPos v6 = new BlockPos(start.getX(), end.getY(), end.getZ());
    BlockPos v7 = new BlockPos(end.getX(), start.getY(), end.getZ());
    BlockPos v8 = new BlockPos(end.getX(), end.getY(), end.getZ());

    // X 軸方向の辺
    edgesX.add(new Edge(v1, v2)); // 下底 (前)
    edgesX.add(new Edge(v3, v5)); // 上面 (前)
    edgesX.add(new Edge(v4, v7)); // 下底 (後)
    edgesX.add(new Edge(v6, v8)); // 上面 (後)

    // Y 軸方向の辺
    edgesY.add(new Edge(v1, v3)); // 左 (前)
    edgesY.add(new Edge(v2, v5)); // 右 (前)
    edgesY.add(new Edge(v4, v6)); // 左 (後)
    edgesY.add(new Edge(v7, v8)); // 右 (後)

    // Z 軸方向の辺
    edgesZ.add(new Edge(v1, v4)); // 下 (左)
    edgesZ.add(new Edge(v2, v7)); // 下 (右)
    edgesZ.add(new Edge(v3, v6)); // 上 (左)
    edgesZ.add(new Edge(v5, v8)); // 上 (右)

    return new XYZ(edgesX, edgesY, edgesZ);
  }

  public List<BlockPos> getPosList() {
    List<BlockPos> stream = new ArrayList<>();
    for (int x = xStart; x <= xEnd; x++) {
      for (int y = yStart; y <= yEnd; y++) {
        for (int z = zStart; z <= zEnd; z++) {
          stream.add(new BlockPos(x, y, z));
        }
      }
    }
    return stream;
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
        new BlockPos(this.xEnd, this.yEnd, this.zEnd));
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

  public int allBlockCount() {
    return (distanceX() + 1) * (distanceY() + 1) * (distanceZ() + 1);
  }

  @Override
  public String toString() {
    return "start: %s, end: %s x: %d y: %d z: %d"
        .formatted(start, end, rangeX(), rangeY(), rangeZ());
  }

  /** 辺を表現する内部クラス */
  public record Edge(BlockPos start, BlockPos end) {
    private static int dist(int s, int e) {
      return Math.abs(s - e);
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", Edge.class.getSimpleName() + "[", "]")
          .add("start=" + start)
          .add("end=" + end)
          .toString();
    }
  }

  public record XYZ(EdgeList x, EdgeList y, EdgeList z) {
    public XYZ(List<Edge> x, List<Edge> y, List<Edge> z) {
      this(new EdgeList(x), new EdgeList(y), new EdgeList(z));
    }

    public boolean isXEmpty() {
      return x.stream().allMatch(e -> e.start.getX() - e.end.getX() == 0);
    }

    public boolean isYEmpty() {
      return y.stream().allMatch(e -> e.start.getY() - e.end.getY() == 0);
    }

    public boolean isZEmpty() {
      return z.stream().allMatch(e -> e.start.getZ() - e.end.getZ() == 0);
    }

    public boolean isEmpty() {
      return isXEmpty() && isYEmpty() && isZEmpty();
    }
  }

  public static class EdgeList implements Iterable<Edge> {
    private final List<Edge> edges;

    EdgeList(List<Edge> edges) {
      this.edges = edges;
    }

    public Stream<Edge> stream() {
      return edges.stream();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public @NotNull Iterator<Edge> iterator() {
      return this.edges.iterator();
    }

    public void rendering(BiConsumer<PoseStack, Edge> renderer, PoseStack stack) {
      Edge last = null;
      for (Edge edge : this) {
        if (last != null) {
          stack.translate(
              last.end.getX() - edge.start.getX(),
              last.end.getY() - edge.start.getY(),
              last.end.getZ() - edge.start.getZ());
        }
        renderer.accept(stack, edge);
        if (edge.equals(edges.getLast())) return;
        last = edge;
      }
    }
  }
}
