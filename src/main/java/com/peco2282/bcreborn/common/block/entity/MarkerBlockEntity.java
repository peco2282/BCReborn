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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MarkerBlockEntity extends BuildCraftBlockEntity implements IBlockEntityAreaProvider {
  public static final int MARKER_RANGE = 64;
  // -----------------------------------------------------------------------
  // Fields
  // -----------------------------------------------------------------------
  public Origin origin = new Origin();
  public boolean showSignals = false;
  public List<LaserData> lasers = new ArrayList<>();
  public List<LaserData> signals = new ArrayList<>();
  // Positions loaded from NBT, resolved in initialize()
  private BlockPos initVectO = null;
  private BlockPos[] initVect = null;

  public MarkerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  // -----------------------------------------------------------------------
  // Lifecycle
  // -----------------------------------------------------------------------
  @Override
  public void initialize() {
    super.initialize();

    updateSignals();
    if (initVectO != null) {
      origin = new Origin();
      origin.vectO = new TileWrapper(initVectO);

      if (initVect != null) {
        for (int i = 0; i < 3; ++i) {
          if (initVect[i] != null) {
            origin.vect[i] = new TileWrapper(initVect[i]);
          }
        }
      }
      if (origin.vectO.pos.equals(worldPosition)) {
        updateBounds();
      }
      initVectO = null;
      initVect = null;
    }

    if (!level.isClientSide) {
      if (origin.isSet() && origin.vectO.pos.equals(worldPosition)) {
        createLasers();
      }
      updateSignalsLasers();
    }
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    // No per-tick logic needed for markers
  }

  // -----------------------------------------------------------------------
  // Signal / connection
  // -----------------------------------------------------------------------
  public void updateSignals() {
    if (!level.isClientSide) {
      boolean oldSignals = showSignals;
      showSignals = level.hasNeighborSignal(worldPosition);
      if (oldSignals != showSignals) {
        switchSignals();
      }
      setChanged();
    }
  }

  public void switchSignals() {
    if (origin.isSet()) {
      MarkerBlockEntity originMarker = origin.vectO.getMarker(level);
      if (showSignals) {
        originMarker.createLasers();
      } else {
        originMarker.destroyLasers();
      }
      // Also notify end markers to update their lasers
      for (int i = 0; i < 3; i++) {
        if (origin.vect[i].isSet()) {
          MarkerBlockEntity endMarker = origin.vect[i].getMarker(level);
          endMarker.updateSignalsLasers();
        }
      }
    }
    updateSignalsLasers();
  }

  public void updateSignalsLasers() {
    signals.clear();
    if (level.isClientSide || !origin.isSet()) {
      return;
    }

    BlockPos originPos = origin.vectO.pos;
    if (originPos.equals(worldPosition)) {
      for (int i = 0; i < 3; i++) {
        if (origin.vect[i].isSet()) {
          LaserData laser = new LaserData(
            new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5),
            new Vec3(origin.vect[i].pos.getX() + 0.5, origin.vect[i].pos.getY() + 0.5, origin.vect[i].pos.getZ() + 0.5),
            LaserKind.Blue
          );
          laser.isVisible = showSignals;
          signals.add(laser);
        }
      }
    } else {
      // If we are one of the end markers, we also render the beam back to the origin
      // to ensure visibility and multi-hop rendering consistency.
      for (int i = 0; i < 3; i++) {
        if (origin.vect[i].isSet() && origin.vect[i].pos.equals(worldPosition)) {
          LaserData laser = new LaserData(
            new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5),
            new Vec3(originPos.getX() + 0.5, originPos.getY() + 0.5, originPos.getZ() + 0.5),
            LaserKind.Blue
          );
          laser.isVisible = showSignals;
          signals.add(laser);
        }
      }
    }

    if (!level.isClientSide) {
      setChanged();
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public void createLasers() {
    if (level.isClientSide) {
      return;
    }
    if (!origin.isSet()) {
      return;
    }
    destroyLasers();

    Origin o = origin;
    int xCoord = worldPosition.getX();
    int yCoord = worldPosition.getY();
    int zCoord = worldPosition.getZ();

    if (!origin.vect[0].isSet()) {
      o.posMin.setX(origin.vectO.pos.getX());
      o.posMax.setX(origin.vectO.pos.getX());
    } else if (origin.vect[0].pos.getX() < xCoord) {
      o.posMin.setX(origin.vect[0].pos.getX());
      o.posMax.setX(xCoord);
    } else {
      o.posMin.setX(xCoord);
      o.posMax.setX(origin.vect[0].pos.getX());
    }

    if (!origin.vect[1].isSet()) {
      o.posMin.setY(origin.vectO.pos.getY());
      o.posMax.setY(origin.vectO.pos.getY());
    } else if (origin.vect[1].pos.getY() < yCoord) {
      o.posMin.setY(origin.vect[1].pos.getY());
      o.posMax.setY(yCoord);
    } else {
      o.posMin.setY(yCoord);
      o.posMax.setY(origin.vect[1].pos.getY());
    }

    if (!origin.vect[2].isSet()) {
      o.posMin.setZ(origin.vectO.pos.getZ());
      o.posMax.setZ(origin.vectO.pos.getZ());
    } else if (origin.vect[2].pos.getZ() < zCoord) {
      o.posMin.setZ(origin.vect[2].pos.getZ());
      o.posMax.setZ(zCoord);
    } else {
      o.posMin.setZ(zCoord);
      o.posMax.setZ(origin.vect[2].pos.getZ());
    }

    BlockPos posMin = origin.posMin;
    BlockPos posMax = origin.posMax;

    if (posMin.equals(posMax)) {
      return;
    }

    lasers = LaserUtils.createLaserDataBox(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ(), LaserKind.Blue);
    for (LaserData ld : lasers) {
      ld.isGlowing = true;
    }

    setChanged();
    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
  }

  public void destroyLasers() {
    if (lasers.isEmpty()) return;
    lasers.clear();
    setChanged();
    if (!level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public void tryConnection() {
    if (level.isClientSide) {
      return;
    }


    // If the origin exists but its marker is missing/removed, clear it
    if (origin.isSet()) {
      MarkerBlockEntity originMarker = origin.vectO.getMarker(level);
      if (originMarker.isRemoved()) {
        origin = new Origin();
      }
    }

    // Always try to find connections if not fully connected
    for (int j = 0; j < 3; ++j) {
      if (!origin.isSet() || !origin.vect[j].isSet()) {
        setVect(j);
      }
    }

    if (origin.isSet()) {
      MarkerBlockEntity originMarker = origin.vectO.getMarker(level);
      originMarker.createLasers();
    }

    setChanged();
  }

  void setVect(int n) {
    // n=0 → X axis, n=1 → Y axis, n=2 → Z axis
    int x = worldPosition.getX();
    int y = worldPosition.getY();
    int z = worldPosition.getZ();

    int[] coords = {x, y, z};

    if (!origin.isSet() || !origin.vect[n].isSet()) {
      for (int j = 1; j < MARKER_RANGE; ++j) {
        coords[n] += j;

        BlockPos posA = new BlockPos(coords[0], coords[1], coords[2]);

        BlockEntity block = level.getBlockEntity(posA);

        if (block instanceof MarkerBlockEntity marker) {
          if (linkTo(marker, n)) {
            break;
          }
        }

        coords[n] -= j;
        coords[n] -= j;

        BlockPos posB = new BlockPos(coords[0], coords[1], coords[2]);

        block = level.getBlockEntity(posB);

        if (block instanceof MarkerBlockEntity marker) {
          if (linkTo(marker, n)) {
            break;
          }
        }

        coords[n] += j;

//        BlockPos posPlus = offset(x, y, z, n, j);
//        BlockEntity tePlus = level.getBlockEntity(posPlus);
//        if (tePlus instanceof MarkerBlockEntity marker) {
//          if (linkTo(marker, n)) {
//            return;
//          }
//        }
//
//        BlockPos posMinus = offset(x, y, z, n, -j);
//        BlockEntity teMinus = level.getBlockEntity(posMinus);
//        if (teMinus instanceof MarkerBlockEntity marker) {
//          if (linkTo(marker, n)) {
//            return;
//          }
//        }
      }
    }
  }

  private boolean linkTo(MarkerBlockEntity marker, int n) {
    if (marker == this) {
      return false;
    }


    if (origin.isSet() && marker.origin.isSet()) {
      if (origin == marker.origin) {
        return false;
      }

      // If both have origins but different, and one is not fully formed, we might want to merge.
      // For now, prioritize the one that is already an origin.
      if (origin.vectO.pos.equals(worldPosition)) {
        marker.origin = origin;
        origin.vect[n] = new TileWrapper(marker.getBlockPos());
      } else if (marker.origin.vectO.pos.equals(marker.worldPosition)) {
        origin = marker.origin;
        origin.vect[n] = new TileWrapper(getBlockPos());
      } else {
        origin = marker.origin;
        origin.vect[n] = new TileWrapper(getBlockPos());
      }
    } else if (!origin.isSet() && !marker.origin.isSet()) {
      origin = new Origin();
      marker.origin = origin;
      origin.vectO = new TileWrapper(getBlockPos());
      origin.vect[n] = new TileWrapper(marker.getBlockPos());
    } else if (!origin.isSet()) {
      origin = marker.origin;
      origin.vect[n] = new TileWrapper(getBlockPos());
    } else {
      marker.origin = origin;
      origin.vect[n] = new TileWrapper(marker.getBlockPos());
    }

    MarkerBlockEntity mO = origin.vectO.getMarker(level);
    mO.updateBounds();
    mO.createLasers();
    mO.updateSignals();
    updateSignals();
    marker.updateSignals();

    return true;
  }

  private void updateBounds() {
    if (!origin.isSet()) {
      return;
    }

    int ox = origin.vectO.pos.getX();
    int oy = origin.vectO.pos.getY();
    int oz = origin.vectO.pos.getZ();

    int xMin, xMax, yMin, yMax, zMin, zMax;

    if (!origin.vect[0].isSet()) {
      xMin = ox;
      xMax = ox;
    } else {
      int vx = origin.vect[0].pos.getX();
      xMin = Math.min(ox, vx);
      xMax = Math.max(ox, vx);
    }

    if (!origin.vect[1].isSet()) {
      yMin = oy;
      yMax = oy;
    } else {
      int vy = origin.vect[1].pos.getY();
      yMin = Math.min(oy, vy);
      yMax = Math.max(oy, vy);
    }

    if (!origin.vect[2].isSet()) {
      zMin = oz;
      zMax = oz;
    } else {
      int vz = origin.vect[2].pos.getZ();
      zMin = Math.min(oz, vz);
      zMax = Math.max(oz, vz);
    }

    origin.posMin = new BlockPos(xMin, yMin, zMin).mutable();
    origin.posMax = new BlockPos(xMax, yMax, zMax).mutable();
  }

//  private static BlockPos offset(int x, int y, int z, int axis, int delta) {
//    return switch (axis) {
//      case 0 -> new BlockPos(x + delta, y, z);
//      case 1 -> new BlockPos(x, y + delta, z);
//      default -> new BlockPos(x, y, z + delta);
//    };
//  }

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

  @Override
  public boolean isValidFromLocation(BlockPos pos) {
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    int mx = worldPosition.getX();
    int my = worldPosition.getY();
    int mz = worldPosition.getZ();

    int equal = (x == mx ? 1 : 0) + (y == my ? 1 : 0) + (z == mz ? 1 : 0);

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

  @Override
  public void removeFromWorld() {
    if (!origin.isSet()) {
      return;
    }

    Origin o = origin;

    for (TileWrapper m : o.vect.clone()) {
      if (m.isSet()) {
        Block.dropResources(getLevel().getBlockState(m.pos), getLevel(), m.pos, this);
        getLevel().removeBlock(m.pos, false);
      }
    }

    Block.dropResources(getLevel().getBlockState(o.vectO.pos), getLevel(), o.vectO.pos, this);
    getLevel().removeBlock(o.vectO.pos, false);
  }

  // -----------------------------------------------------------------------
  // Destroy / invalidate
  // -----------------------------------------------------------------------
  @Override
  public void setRemoved() {
    super.setRemoved();
    destroyLasers();
  }

  public void destroy() {
    destroyLasers();

    if (origin.isSet()) {
      Origin o = origin;

      // 1. Notify origin marker to destroy its box lasers
      MarkerBlockEntity originMarker = o.vectO.getMarker(level);
      originMarker.destroyLasers();

      // 2. Clear origin reference from all markers in this group
      List<MarkerBlockEntity> affectedMarkers = new ArrayList<>();

      MarkerBlockEntity mO = o.vectO.getMarker(level);
      affectedMarkers.add(mO);

      for (TileWrapper m : o.vect) {
        MarkerBlockEntity mark = m.getMarker(level);
        affectedMarkers.add(mark);
      }

      for (MarkerBlockEntity mark : affectedMarkers) {
        mark.origin = new Origin();
      }

      // 3. Update signals/lasers for all affected markers
      for (MarkerBlockEntity mark : affectedMarkers) {
        if (mark != this && !mark.isRemoved()) {
          mark.updateSignals();
        }
      }
    }
  }

  // -----------------------------------------------------------------------
  // NBT
  // -----------------------------------------------------------------------
  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);

    if (origin.isSet() && origin.vectO.getMarker(level) == this) {
      nbt.putLong("vectO", origin.vectO.pos.asLong());

      for (int i = 0; i < 3; ++i) {
        if (origin.vect[i].isSet()) {
          nbt.putLong("vect" + i, origin.vect[i].pos.asLong());
        }
      }
    }
//    saveLaserList(nbt, "lasers", lasers);
//    saveLaserList(nbt, "signals", signals);
  }

  private void saveLaserList(CompoundTag nbt, String key, List<LaserData> list) {
    if (list.isEmpty()) return;
    ListTag tagList = new ListTag();
    for (LaserData ld : list) {
      tagList.add(ld.toNBT());
    }
    nbt.put(key, tagList);
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);

    if (nbt.contains("vectO")) {
      initVectO = BlockPos.of(nbt.getLong("vectO"));
      initVect = new BlockPos[3];

      for (int i = 0; i < 3; ++i) {
        if (nbt.contains("vect" + i)) {
          initVect[i] = BlockPos.of(nbt.getLong("vect" + i));
        }
      }
    }
//    lasers = loadLaserList(nbt, "lasers");
//    signals = loadLaserList(nbt, "signals");
  }

  private List<LaserData> loadLaserList(CompoundTag nbt, String key) {
    List<LaserData> list = new ArrayList<>();
    if (nbt.contains(key, CompoundTag.TAG_LIST)) {
      ListTag tagList = nbt.getList(key, CompoundTag.TAG_COMPOUND);
      for (int i = 0; i < tagList.size(); i++) {
        list.add(new LaserData(tagList.getCompound(i)));
      }
    }
    return list;
  }

  // -----------------------------------------------------------------------
  // Network sync
  // -----------------------------------------------------------------------
  @Override
  public void writeData(FriendlyByteBuf stream) {
    origin.writeData(stream);
    stream.writeBoolean(showSignals);
    writeLaserList(stream, lasers);
    writeLaserList(stream, signals);
  }

  private void writeLaserList(FriendlyByteBuf stream, List<LaserData> list) {
    stream.writeInt(list.size());
    for (LaserData ld : list) {
      stream.writeNbt(ld.toNBT());
    }
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    origin.readData(stream);
    showSignals = stream.readBoolean();
    lasers = readLaserList(stream);
    signals = readLaserList(stream);
    switchSignals();
    if (origin.vectO.isSet()) {
      origin.vectO.getMarker(level);
      origin.vectO.getMarker(level).updateSignals();
      for (TileWrapper w : origin.vect) {
        MarkerBlockEntity m = w.getMarker(level);

        m.updateSignals();
      }
    }

    if (origin.isSet() && origin.vectO.pos.equals(worldPosition)) {
      updateBounds();
      createLasers();
    }
  }

  private List<LaserData> readLaserList(FriendlyByteBuf stream) {
    int size = stream.readInt();
    List<LaserData> list = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      list.add(new LaserData(stream.readNbt()));
    }
    return list;
  }

  // -----------------------------------------------------------------------
  // TileWrapper
  // -----------------------------------------------------------------------
  public static class TileWrapper implements IBufferSerializable {

    public BlockPos pos;
    private MarkerBlockEntity marker;

    public TileWrapper() {
      pos = BlockPos.ZERO;
    }

    public TileWrapper(BlockPos pos) {
      this.pos = pos;
    }

    public boolean isSet() {
      return pos != null && !pos.equals(BlockPos.ZERO);
    }

    public MarkerBlockEntity getMarker(Level world) {
      if (!isSet()) {
        return null;
      }

      if (marker == null) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof MarkerBlockEntity) {
          marker = (MarkerBlockEntity) tile;
        }
      }

      return marker;
    }

    public void reset() {
      pos = BlockPos.ZERO;
      marker = null;
    }

    @Override
    public void readData(FriendlyByteBuf stream) {
      pos = BlockPos.of(stream.readLong());
      marker = null;
    }

    @Override
    public void writeData(FriendlyByteBuf stream) {
      stream.writeLong(pos.asLong());
    }
  }

  // -----------------------------------------------------------------------
  // Origin
  // -----------------------------------------------------------------------
  public static class Origin implements IBufferSerializable {
    public TileWrapper vectO = new TileWrapper();
    public TileWrapper[] vect = {new TileWrapper(), new TileWrapper(), new TileWrapper()};
    public BlockPos.MutableBlockPos posMin = BlockPos.ZERO.mutable();
    public BlockPos.MutableBlockPos posMax = BlockPos.ZERO.mutable();

    public boolean isSet() {
      return vectO != null && vectO.isSet();
    }

    @Override
    public void writeData(FriendlyByteBuf stream) {
      vectO.writeData(stream);
      for (TileWrapper tw : vect) {
        tw.writeData(stream);
      }
      stream.writeLong(posMin.asLong());
      stream.writeLong(posMax.asLong());
    }

    @Override
    public void readData(FriendlyByteBuf stream) {
      vectO.readData(stream);
      for (TileWrapper tw : vect) {
        tw.readData(stream);
      }
      posMin = BlockPos.of(stream.readLong()).mutable();
      posMax = BlockPos.of(stream.readLong()).mutable();
    }
  }
}
