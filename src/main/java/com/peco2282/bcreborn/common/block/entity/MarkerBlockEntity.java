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
package com.peco2282.bcreborn.common.block.entity;

import com.peco2282.bcreborn.api.core.IBufferSerializable;
import com.peco2282.bcreborn.api.tiles.IBlockEntityAreaProvider;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.LaserKind;
import com.peco2282.bcreborn.common.utils.LaserUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code buildcraft.core.TileMarker} (the Land Mark).
 * <ul>
 *   <li>Right-click ({@link #tryConnection}) links to another landmark of the same block on each unlinked axis,
 *       within {@link #MARKER_RANGE}. Up to three landmarks join one {@link Origin} and form a box.</li>
 *   <li>The origin landmark shows the box as red lasers ({@link #lasers}) for as long as the group exists.</li>
 *   <li>While powered by redstone, blue signal beams ({@link #signals}) are shown along every axis that is not
 *       linked yet, to help place the next landmark.</li>
 *   <li>Machines (Quarry, Filler, Architect Table) take the box through {@link #isValidFromLocation} and then
 *       {@link #removeFromWorld} the landmarks.</li>
 * </ul>
 * Lasers and signals are computed on the server and synced to the client as data only.
 */
public class MarkerBlockEntity extends BuildCraftBlockEntity implements IBlockEntityAreaProvider {
  public static final int MARKER_RANGE = 64;

  public Origin origin = new Origin();
  public boolean showSignals = false;
  /** Box lasers; only the origin landmark of a group holds them. */
  public List<LaserData> lasers = new ArrayList<>();
  /** Redstone signal beams along the unlinked axes. */
  public List<LaserData> signals = new ArrayList<>();

  // Positions loaded from NBT, resolved in initialize()
  @Nullable
  private BlockPos initVectO = null;
  @Nullable
  private BlockPos[] initVect = null;

  public MarkerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  // -----------------------------------------------------------------------
  // Lifecycle
  // -----------------------------------------------------------------------

  /**
   * Original {@code initialize()}: the origin landmark restores its group from NBT and re-links the
   * landmarks that are loaded. Unlike the original, links to landmarks that are not loaded yet are kept,
   * so the box survives chunk load order.
   */
  @Override
  public void initialize() {
    super.initialize();
    if (level == null || level.isClientSide) {
      return;
    }

    if (initVectO != null && initVectO.equals(worldPosition)) {
      origin = new Origin();
      origin.vectO = new TileWrapper(worldPosition);
      if (initVect != null) {
        for (int i = 0; i < 3; ++i) {
          if (initVect[i] == null) continue;
          origin.vect[i] = new TileWrapper(initVect[i]);
          MarkerBlockEntity marker = origin.vect[i].getMarker(level);
          if (marker != null && marker != this && !marker.origin.isSet()) {
            marker.origin = origin;
            marker.updateSignals();
          }
        }
      }
      updateBounds();
      createLasers();
    }
    initVectO = null;
    initVect = null;

    updateSignals();
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    // No per-tick logic needed for markers
  }

  // -----------------------------------------------------------------------
  // Signals
  // -----------------------------------------------------------------------

  /**
   * Original {@code updateSignals()}: re-reads the redstone input and rebuilds the signal beams.
   */
  public void updateSignals() {
    if (level == null || level.isClientSide) {
      return;
    }
    showSignals = level.hasNeighborSignal(worldPosition);
    rebuildSignals();
    setChanged();
  }

  /**
   * Original {@code switchSignals()}: while powered, a beam of {@code MARKER_RANGE - 1} blocks in both directions
   * along every axis that is not linked yet.
   */
  private void rebuildSignals() {
    signals = new ArrayList<>();
    if (!showSignals) {
      return;
    }
    Vec3 center = Vec3.atCenterOf(worldPosition);
    int reach = MARKER_RANGE - 1;
    for (int axis = 0; axis < 3; axis++) {
      if (origin.isSet() && origin.vect[axis].isSet()) {
        continue;
      }
      Vec3 step = new Vec3(axis == 0 ? reach : 0, axis == 1 ? reach : 0, axis == 2 ? reach : 0);
      signals.add(new LaserData(center, center.add(step), LaserKind.Blue));
      signals.add(new LaserData(center.subtract(step), center, LaserKind.Blue));
    }
  }

  // -----------------------------------------------------------------------
  // Connection
  // -----------------------------------------------------------------------

  /**
   * Original {@code tryConnection()}: links each axis that is not linked yet.
   */
  public void tryConnection() {
    if (level == null || level.isClientSide) {
      return;
    }
    for (int j = 0; j < 3; ++j) {
      if (!origin.isSet() || !origin.vect[j].isSet()) {
        setVect(j);
      }
    }
    setChanged();
  }

  /**
   * Original {@code setVect(n)}: searches outwards on axis {@code n} (0 = X, 1 = Y, 2 = Z), alternating the
   * positive and negative side, for a landmark of the same block to link with.
   */
  void setVect(int n) {
    if (origin.isSet() && origin.vect[n].isSet()) {
      return;
    }
    for (int j = 1; j < MARKER_RANGE; ++j) {
      MarkerBlockEntity plus = findLinkCandidate(offset(n, j));
      if (plus != null && linkTo(plus, n)) {
        return;
      }
      MarkerBlockEntity minus = findLinkCandidate(offset(n, -j));
      if (minus != null && linkTo(minus, n)) {
        return;
      }
    }
  }

  private BlockPos offset(int axis, int delta) {
    return switch (axis) {
      case 0 -> worldPosition.offset(delta, 0, 0);
      case 1 -> worldPosition.offset(0, delta, 0);
      default -> worldPosition.offset(0, 0, delta);
    };
  }

  /**
   * Original: {@code world.getBlock(...) == BuildCraftCore.markerBlock}. Only landmarks of the same block link,
   * and unloaded positions are never loaded by the search.
   */
  @Nullable
  private MarkerBlockEntity findLinkCandidate(BlockPos pos) {
    if (!level.isLoaded(pos) || !level.getBlockState(pos).is(getBlockState().getBlock())) {
      return null;
    }
    return level.getBlockEntity(pos) instanceof MarkerBlockEntity marker ? marker : null;
  }

  /**
   * Original {@code linkTo(marker, n)}. Two landmarks that already belong to groups are never merged.
   */
  private boolean linkTo(MarkerBlockEntity marker, int n) {
    if (marker == this) {
      return false;
    }
    if (origin.isSet() && marker.origin.isSet()) {
      return false;
    }

    if (!origin.isSet() && !marker.origin.isSet()) {
      origin = new Origin();
      marker.origin = origin;
      origin.vectO = new TileWrapper(worldPosition);
      origin.vect[n] = new TileWrapper(marker.getBlockPos());
    } else if (!origin.isSet()) {
      origin = marker.origin;
      origin.vect[n] = new TileWrapper(worldPosition);
    } else {
      marker.origin = origin;
      origin.vect[n] = new TileWrapper(marker.getBlockPos());
    }

    MarkerBlockEntity originMarker = origin.vectO.getMarker(level);
    if (originMarker != null) {
      originMarker.updateBounds();
      originMarker.createLasers();
      if (originMarker != this && originMarker != marker) {
        originMarker.updateSignals();
      }
    }
    updateSignals();
    marker.updateSignals();
    return true;
  }

  private void updateBounds() {
    if (!origin.isSet()) {
      return;
    }
    BlockPos o = origin.vectO.pos;
    int xMin = o.getX(), xMax = o.getX();
    int yMin = o.getY(), yMax = o.getY();
    int zMin = o.getZ(), zMax = o.getZ();
    if (origin.vect[0].isSet()) {
      int v = origin.vect[0].pos.getX();
      xMin = Math.min(xMin, v);
      xMax = Math.max(xMax, v);
    }
    if (origin.vect[1].isSet()) {
      int v = origin.vect[1].pos.getY();
      yMin = Math.min(yMin, v);
      yMax = Math.max(yMax, v);
    }
    if (origin.vect[2].isSet()) {
      int v = origin.vect[2].pos.getZ();
      zMin = Math.min(zMin, v);
      zMax = Math.max(zMax, v);
    }
    origin.posMin = new BlockPos(xMin, yMin, zMin);
    origin.posMax = new BlockPos(xMax, yMax, zMax);
  }

  /**
   * Original {@code createLasers()}: the box as red lasers, held by the origin landmark.
   */
  public void createLasers() {
    if (level == null || level.isClientSide || !origin.isSet()) {
      return;
    }
    BlockPos min = origin.posMin;
    BlockPos max = origin.posMax;
    List<LaserData> box = new ArrayList<>();
    for (LaserData laser : LaserUtils.createLaserDataBox(min.getX(), min.getY(), min.getZ(),
      max.getX(), max.getY(), max.getZ(), LaserKind.Red)) {
      // Degenerate edges of a flat or line-shaped box come back as null.
      if (laser != null) {
        laser.isGlowing = true;
        box.add(laser);
      }
    }
    lasers = box;
    setChanged();
  }

  public void destroyLasers() {
    if (lasers.isEmpty()) return;
    lasers = new ArrayList<>();
    if (level != null && !level.isClientSide) {
      setChanged();
    }
  }

  // -----------------------------------------------------------------------
  // IBlockEntityAreaProvider / IAreaProvider
  // -----------------------------------------------------------------------
  @Override
  public int xMin() {
    return origin.isSet() ? origin.posMin.getX() : worldPosition.getX();
  }

  @Override
  public int yMin() {
    return origin.isSet() ? origin.posMin.getY() : worldPosition.getY();
  }

  @Override
  public int zMin() {
    return origin.isSet() ? origin.posMin.getZ() : worldPosition.getZ();
  }

  @Override
  public int xMax() {
    return origin.isSet() ? origin.posMax.getX() : worldPosition.getX();
  }

  @Override
  public int yMax() {
    return origin.isSet() ? origin.posMax.getY() : worldPosition.getY();
  }

  @Override
  public int zMax() {
    return origin.isSet() ? origin.posMax.getZ() : worldPosition.getZ();
  }

  /**
   * Original {@code isValidFromLocation()}. The position must share one or two (not three) coordinates with this
   * landmark, touch the box from outside on exactly one axis, and not be inside the box.
   */
  @Override
  public boolean isValidFromLocation(BlockPos pos) {
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();

    int equal = (x == worldPosition.getX() ? 1 : 0) + (y == worldPosition.getY() ? 1 : 0) + (z == worldPosition.getZ() ? 1 : 0);
    if (equal == 0 || equal == 3) {
      return false;
    }

    if (x < (xMin() - 1) || x > (xMax() + 1)
      || y < (yMin() - 1) || y > (yMax() + 1)
      || z < (zMin() - 1) || z > (zMax() + 1)) {
      return false;
    }

    if (x >= xMin() && x <= xMax() && y >= yMin() && y <= yMax() && z >= zMin() && z <= zMax()) {
      return false;
    }

    int touching = 0;
    if (xMin() - x == 1 || x - xMax() == 1) touching++;
    if (yMin() - y == 1 || y - yMax() == 1) touching++;
    if (zMin() - z == 1 || z - zMax() == 1) touching++;

    return touching == 1;
  }

  /**
   * Original {@code removeFromWorld()}: breaks every landmark of the group and drops it as an item.
   * Only positions that still hold this landmark block are broken.
   */
  @Override
  public void removeFromWorld() {
    if (level == null || !origin.isSet()) {
      return;
    }
    Origin o = origin;
    List<BlockPos> positions = new ArrayList<>(4);
    for (TileWrapper m : o.vect) {
      if (m.isSet()) positions.add(m.pos);
    }
    positions.add(o.vectO.pos);

    var block = getBlockState().getBlock();
    for (BlockPos p : positions) {
      if (level.isLoaded(p) && level.getBlockState(p).is(block)) {
        level.destroyBlock(p, true);
      }
    }
  }

  // -----------------------------------------------------------------------
  // Destroy
  // -----------------------------------------------------------------------

  /**
   * Original {@code destroy()}: called when this landmark is broken. Dissolves the whole group, clears the box
   * lasers and lets the remaining landmarks update their signal beams.
   */
  public void destroy() {
    if (level == null || level.isClientSide) {
      return;
    }
    if (origin.isSet()) {
      Origin o = origin;
      MarkerBlockEntity originMarker = o.vectO.getMarker(level);

      List<MarkerBlockEntity> others = new ArrayList<>(4);
      if (originMarker != null && originMarker != this) others.add(originMarker);
      for (TileWrapper w : o.vect) {
        MarkerBlockEntity mark = w.getMarker(level);
        if (mark != null && mark != this && !others.contains(mark)) others.add(mark);
      }

      for (MarkerBlockEntity mark : others) {
        mark.origin = new Origin();
        mark.destroyLasers();
      }
      for (MarkerBlockEntity mark : others) {
        if (!mark.isRemoved()) mark.updateSignals();
      }
    }
    origin = new Origin();
    lasers = new ArrayList<>();
    signals = new ArrayList<>();
  }

  // -----------------------------------------------------------------------
  // NBT
  // -----------------------------------------------------------------------
  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);

    if (origin.isSet() && origin.vectO.pos.equals(worldPosition)) {
      nbt.putLong("vectO", origin.vectO.pos.asLong());
      for (int i = 0; i < 3; ++i) {
        if (origin.vect[i].isSet()) {
          nbt.putLong("vect" + i, origin.vect[i].pos.asLong());
        }
      }
    }
  }

  /**
   * The chunk-load update tag also carries the computed lasers, so a player who joins later sees the box and
   * signal beams without waiting for the next change.
   */
  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = super.getUpdateTag();
    tag.put("lasers", toLaserTag(lasers));
    tag.put("signals", toLaserTag(signals));
    tag.putBoolean("showSignals", showSignals);
    return tag;
  }

  private static ListTag toLaserTag(List<LaserData> list) {
    ListTag tag = new ListTag();
    for (LaserData ld : list) {
      tag.add(ld.toNBT());
    }
    return tag;
  }

  private static List<LaserData> fromLaserTag(ListTag tag) {
    List<LaserData> list = new ArrayList<>(tag.size());
    for (int i = 0; i < tag.size(); i++) {
      list.add(new LaserData(tag.getCompound(i)));
    }
    return list;
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);

    // Only present in the client update tag, never in saved data.
    if (nbt.contains("lasers", Tag.TAG_LIST)) {
      lasers = fromLaserTag(nbt.getList("lasers", Tag.TAG_COMPOUND));
      signals = fromLaserTag(nbt.getList("signals", Tag.TAG_COMPOUND));
      showSignals = nbt.getBoolean("showSignals");
    }

    if (nbt.contains("vectO")) {
      initVectO = BlockPos.of(nbt.getLong("vectO"));
      initVect = new BlockPos[3];
      for (int i = 0; i < 3; ++i) {
        if (nbt.contains("vect" + i)) {
          initVect[i] = BlockPos.of(nbt.getLong("vect" + i));
        }
      }
    }
  }

  // -----------------------------------------------------------------------
  // Network sync (server -> client, data only)
  // -----------------------------------------------------------------------
  @Override
  public void writeData(FriendlyByteBuf stream) {
    origin.writeData(stream);
    stream.writeBoolean(showSignals);
    writeLaserList(stream, lasers);
    writeLaserList(stream, signals);
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    Origin received = new Origin();
    received.readData(stream);
    origin = received;
    showSignals = stream.readBoolean();
    lasers = readLaserList(stream);
    signals = readLaserList(stream);
  }

  private static void writeLaserList(FriendlyByteBuf stream, List<LaserData> list) {
    stream.writeVarInt(list.size());
    for (LaserData ld : list) {
      stream.writeNbt(ld.toNBT());
    }
  }

  private static List<LaserData> readLaserList(FriendlyByteBuf stream) {
    int size = stream.readVarInt();
    List<LaserData> list = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      CompoundTag tag = stream.readNbt();
      if (tag != null) list.add(new LaserData(tag));
    }
    return list;
  }

  // -----------------------------------------------------------------------
  // TileWrapper
  // -----------------------------------------------------------------------

  /**
   * Original {@code TileMarker.TileWrapper}: a lazily resolved reference to a landmark. Unset is {@code null}
   * (the original used {@code Integer.MAX_VALUE}), so a landmark at (0, 0, 0) is still valid.
   */
  public static class TileWrapper implements IBufferSerializable {
    @Nullable
    public BlockPos pos;
    @Nullable
    private MarkerBlockEntity marker;

    public TileWrapper() {
      pos = null;
    }

    public TileWrapper(BlockPos pos) {
      this.pos = pos.immutable();
    }

    public boolean isSet() {
      return pos != null;
    }

    /**
     * @return the landmark at {@link #pos}, or null if unset, not loaded or no longer a landmark
     */
    @Nullable
    public MarkerBlockEntity getMarker(Level world) {
      if (pos == null) {
        return null;
      }
      if (marker != null && !marker.isRemoved()) {
        return marker;
      }
      marker = null;
      if (world.isLoaded(pos) && world.getBlockEntity(pos) instanceof MarkerBlockEntity m) {
        marker = m;
      }
      return marker;
    }

    public void reset() {
      pos = null;
      marker = null;
    }

    @Override
    public void readData(FriendlyByteBuf stream) {
      pos = stream.readBoolean() ? stream.readBlockPos() : null;
      marker = null;
    }

    @Override
    public void writeData(FriendlyByteBuf stream) {
      stream.writeBoolean(pos != null);
      if (pos != null) {
        stream.writeBlockPos(pos);
      }
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof TileWrapper other && Objects.equals(pos, other.pos);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(pos);
    }
  }

  // -----------------------------------------------------------------------
  // Origin
  // -----------------------------------------------------------------------

  /**
   * Original {@code TileMarker.Origin}: the group shared by up to four landmarks. {@code vectO} is the origin
   * landmark and {@code vect[n]} the landmark linked on axis {@code n}.
   */
  public static class Origin implements IBufferSerializable {
    public TileWrapper vectO = new TileWrapper();
    public TileWrapper[] vect = {new TileWrapper(), new TileWrapper(), new TileWrapper()};
    public BlockPos posMin = BlockPos.ZERO;
    public BlockPos posMax = BlockPos.ZERO;

    public boolean isSet() {
      return vectO.isSet();
    }

    @Override
    public void writeData(FriendlyByteBuf stream) {
      vectO.writeData(stream);
      for (TileWrapper tw : vect) {
        tw.writeData(stream);
      }
      stream.writeBlockPos(posMin);
      stream.writeBlockPos(posMax);
    }

    @Override
    public void readData(FriendlyByteBuf stream) {
      vectO.readData(stream);
      for (TileWrapper tw : vect) {
        tw.readData(stream);
      }
      posMin = stream.readBlockPos();
      posMax = stream.readBlockPos();
    }
  }
}
