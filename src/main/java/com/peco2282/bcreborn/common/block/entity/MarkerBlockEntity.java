package com.peco2282.bcreborn.common.block.entity;

import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.api.tiles.ITileAreaProvider;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MarkerBlockEntity extends BuildCraftBlockEntity implements ITileAreaProvider {
  public static final int MARKER_RANGE = 64;

  // -----------------------------------------------------------------------
  // TileWrapper
  // -----------------------------------------------------------------------
  public static class TileWrapper implements ISerializable {

    public BlockPos pos;
    private MarkerBlockEntity marker;

    public TileWrapper() {
      pos = BlockPos.ZERO;
    }

    public TileWrapper(BlockPos pos) {
      this.pos = pos;
    }

    public boolean isSet() {
      return !pos.equals(BlockPos.ZERO);
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
  public static class Origin implements ISerializable {
    public TileWrapper vectO = new TileWrapper();
    public TileWrapper[] vect = {new TileWrapper(), new TileWrapper(), new TileWrapper()};
    public BlockPos.MutableBlockPos posMin = BlockPos.ZERO.mutable();
    public BlockPos.MutableBlockPos posMax = BlockPos.ZERO.mutable();

    public boolean isSet() {
      return vectO.isSet();
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
            BlockEntity te = level.getBlockEntity(initVect[i]);
            if (te instanceof MarkerBlockEntity other) {
              linkTo(other, i);
            }
          }
        }
      }

      initVectO = null;
      initVect = null;
    }

    if (showSignals) {
      createLasers();
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
    if (level != null && !level.isClientSide) {
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
      if (originMarker != null) {
        if (showSignals) {
          originMarker.createLasers();
        } else {
          originMarker.destroyLasers();
        }
      }
    }
  }

  public void createLasers() {
  }

  public void destroyLasers() {
    if (lasers.isEmpty()) return;
    lasers.clear();
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

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

  void setVect(int n) {
    // n=0 → X axis, n=1 → Y axis, n=2 → Z axis
    int x = worldPosition.getX();
    int y = worldPosition.getY();
    int z = worldPosition.getZ();

    if (!origin.isSet() || !origin.vect[n].isSet()) {
      for (int j = 1; j < MARKER_RANGE; ++j) {
        BlockPos posPlus = offset(x, y, z, n, j);
        BlockEntity tePlus = level.getBlockEntity(posPlus);
        if (tePlus instanceof MarkerBlockEntity marker) {
          if (linkTo(marker, n)) {
            return;
          }
        }

        BlockPos posMinus = offset(x, y, z, n, -j);
        BlockEntity teMinus = level.getBlockEntity(posMinus);
        if (teMinus instanceof MarkerBlockEntity marker) {
          if (linkTo(marker, n)) {
            return;
          }
        }
      }
    }
  }

  private static BlockPos offset(int x, int y, int z, int axis, int delta) {
    return switch (axis) {
      case 0 -> new BlockPos(x + delta, y, z);
      case 1 -> new BlockPos(x, y + delta, z);
      default -> new BlockPos(x, y, z + delta);
    };
  }

  private boolean linkTo(MarkerBlockEntity marker, int n) {
    if (marker == null) {
      return false;
    }

    if (origin.isSet() && marker.origin.isSet()) {
      return false;
    }

    if (!origin.isSet() && !marker.origin.isSet()) {
      origin = new Origin();
      marker.origin = origin;
      origin.vectO = new TileWrapper(worldPosition);
      origin.vect[n] = new TileWrapper(marker.worldPosition);
    } else if (!origin.isSet()) {
      origin = marker.origin;
      origin.vect[n] = new TileWrapper(worldPosition);
    } else {
      marker.origin = origin;
      origin.vect[n] = new TileWrapper(marker.worldPosition);
    }

    updateBounds();
    updateSignals();
    marker.updateSignals();

    if (origin.isSet()) {
      MarkerBlockEntity originMarker = origin.vectO.getMarker(level);
      if (originMarker != null) {
        if (originMarker.showSignals) {
          originMarker.createLasers();
        } else {
          originMarker.destroyLasers();
        }
      }
    }

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

  // -----------------------------------------------------------------------
  // ITileAreaProvider / IAreaProvider
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
    if (level == null || !origin.isSet()) {
      return;
    }

    Origin o = origin;

    for (TileWrapper m : o.vect.clone()) {
      if (m.isSet()) {
        level.removeBlock(m.pos, false);
      }
    }

    level.removeBlock(o.vectO.pos, false);
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
    if (level == null) {
      return;
    }
    destroyLasers();

    if (origin.isSet()) {
      MarkerBlockEntity originMarker = origin.vectO.getMarker(level);
      if (originMarker != null) {
        originMarker.destroyLasers();
      }
    }

    if (origin.isSet()) {
      Origin o = origin;

      for (TileWrapper m : o.vect) {
        MarkerBlockEntity mark = m.getMarker(level);
        if (mark != null && mark != this) {
          mark.origin = new Origin();
        }
      }

      MarkerBlockEntity markerOrigin = o.vectO.getMarker(level);
      if (markerOrigin != null && markerOrigin != this) {
        markerOrigin.origin = new Origin();
      }

      for (TileWrapper wrapper : o.vect) {
        MarkerBlockEntity mark = wrapper.getMarker(level);
        if (mark != null) {
          mark.updateSignals();
        }
      }
      if (markerOrigin != null) {
        markerOrigin.updateSignals();
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
    saveLaserList(nbt, "lasers", lasers);
    saveLaserList(nbt, "signals", signals);
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
    lasers = loadLaserList(nbt, "lasers");
    signals = loadLaserList(nbt, "signals");
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
//    writeLaserList(stream, lasers);
//    writeLaserList(stream, signals);
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
    switchSignals();
    if (origin.vectO.isSet() && origin.vectO.getMarker(level) != null) {
      origin.vectO.getMarker(level).updateSignals();
      for (TileWrapper w : origin.vect) {
        MarkerBlockEntity m = w.getMarker(level);

        if (m != null) {
          m.updateSignals();
        }
      }
    }
    createLasers();
//    lasers = readLaserList(stream);
//    signals = readLaserList(stream);
  }

  private List<LaserData> readLaserList(FriendlyByteBuf stream) {
    int size = stream.readInt();
    List<LaserData> list = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      list.add(new LaserData(stream.readNbt()));
    }
    return list;
  }
}
