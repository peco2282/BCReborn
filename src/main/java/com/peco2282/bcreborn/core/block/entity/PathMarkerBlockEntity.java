package com.peco2282.bcreborn.core.block.entity;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IPathProvider;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class PathMarkerBlockEntity extends MarkerBlockEntity implements IPathProvider {

  // パスマーカーのうち、まだ完全に接続されていないもののリスト（ロード済みチャンク内のみ）
  private static final ArrayList<PathMarkerBlockEntity> availableMarkers = new ArrayList<>();

  // NBTロード時に接続先を復元するための一時座標
  private BlockPos loadLink0Pos = BlockPos.ZERO;
  private BlockPos loadLink1Pos = BlockPos.ZERO;

  public PathMarkerBlockEntity[] links = new PathMarkerBlockEntity[2];

  public boolean tryingToConnect = false;

  public PathMarkerBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesCore.PATH_MARKER.get(), pos, state);
  }

  // -----------------------------------------------------------------------
  // 接続状態
  // -----------------------------------------------------------------------

  public boolean isFullyConnected() {
    return links[0] != null && links[1] != null;
  }

  public boolean isLinkedTo(PathMarkerBlockEntity other) {
    return links[0] == other || links[1] == other;
  }

  public void connect(PathMarkerBlockEntity marker) {
    if (links[0] == null) {
      links[0] = marker;
    } else if (links[1] == null) {
      links[1] = marker;
    }

    if (isFullyConnected()) {
      availableMarkers.remove(this);
    }
  }

  public void createAndConnect(PathMarkerBlockEntity other) {
    if (level == null || level.isClientSide) {
      return;
    }

    connect(other);
    other.connect(this);
  }

  // -----------------------------------------------------------------------
  // 最近傍の利用可能マーカーを探す
  // -----------------------------------------------------------------------

  private @Nullable PathMarkerBlockEntity findNearestAvailable() {
    PathMarkerBlockEntity nearest = null;
    double nearestDist = 0;

    for (PathMarkerBlockEntity t : availableMarkers) {
      if (t == this || t == links[0] || t == links[1]) {
        continue;
      }
      if (t.level == null || level == null) {
        continue;
      }
      // 異なるディメンションは除外
      if (!t.level.dimension().equals(level.dimension())) {
        continue;
      }

      int dx = worldPosition.getX() - t.worldPosition.getX();
      int dy = worldPosition.getY() - t.worldPosition.getY();
      int dz = worldPosition.getZ() - t.worldPosition.getZ();
      double dist = dx * dx + dy * dy + dz * dz;

      if (dist > (double) MARKER_RANGE * MARKER_RANGE) {
        continue;
      }

      if (nearest == null || dist < nearestDist) {
        nearest = t;
        nearestDist = dist;
      }
    }

    return nearest;
  }

  // -----------------------------------------------------------------------
  // tryConnection — 接続試行トグル（MarkerBlockEntityのものをオーバーライド）
  // -----------------------------------------------------------------------

  @Override
  public void tryConnection() {
    if (level == null || level.isClientSide || isFullyConnected()) {
      return;
    }

    tryingToConnect = !tryingToConnect;
    setChanged();
  }

  // -----------------------------------------------------------------------
  // Tick
  // -----------------------------------------------------------------------

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (tryingToConnect) {
      PathMarkerBlockEntity nearest = findNearestAvailable();
      if (nearest != null) {
        createAndConnect(nearest);
      }

      tryingToConnect = false;
      setChanged();
    }
  }

  // -----------------------------------------------------------------------
  // IPathProvider
  // -----------------------------------------------------------------------

  @Override
  public List<BlockIndex> getPath() {
    HashSet<BlockIndex> visited = new HashSet<>();
    ArrayList<BlockIndex> result = new ArrayList<>();

    PathMarkerBlockEntity current = this;

    while (current != null) {
      BlockIndex b = new BlockIndex(
          current.worldPosition.getX(),
          current.worldPosition.getY(),
          current.worldPosition.getZ()
      );

      visited.add(b);
      result.add(b);

      PathMarkerBlockEntity next = null;

      if (current.links[0] != null) {
        BlockIndex b0 = new BlockIndex(
            current.links[0].worldPosition.getX(),
            current.links[0].worldPosition.getY(),
            current.links[0].worldPosition.getZ()
        );
        if (!visited.contains(b0)) {
          next = current.links[0];
        }
      }

      if (next == null && current.links[1] != null) {
        BlockIndex b1 = new BlockIndex(
            current.links[1].worldPosition.getX(),
            current.links[1].worldPosition.getY(),
            current.links[1].worldPosition.getZ()
        );
        if (!visited.contains(b1)) {
          next = current.links[1];
        }
      }

      current = next;
    }

    return result;
  }

  @Override
  public void removeFromWorld() {
    if (level == null) {
      return;
    }

    List<BlockIndex> path = getPath();
    for (BlockIndex b : path) {
      BlockPos p = new BlockPos(b.x, b.y, b.z);
      level.removeBlock(p, false);
    }
  }

  // -----------------------------------------------------------------------
  // Lifecycle
  // -----------------------------------------------------------------------

  @Override
  public void initialize() {
    // MarkerBlockEntityのinitialize()は呼ばない（origin/vect系は不要）
    if (level == null) {
      return;
    }

    if (!level.isClientSide && !isFullyConnected()) {
      if (!availableMarkers.contains(this)) {
        availableMarkers.add(this);
      }
    }

    if (loadLink0Pos != BlockPos.ZERO) {
      BlockEntity e0 = level.getBlockEntity(loadLink0Pos);
      if (e0 instanceof PathMarkerBlockEntity pm && links[0] != pm && links[1] != pm) {
        createAndConnect(pm);
      }
      loadLink0Pos = BlockPos.ZERO;
    }

    if (loadLink1Pos != BlockPos.ZERO) {
      BlockEntity e1 = level.getBlockEntity(loadLink1Pos);
      if (e1 instanceof PathMarkerBlockEntity pm && links[0] != pm && links[1] != pm) {
        createAndConnect(pm);
      }
      loadLink1Pos = BlockPos.ZERO;
    }

    setChanged();
  }

  @Override
  public void setRemoved() {
    if (links[0] != null) {
      links[0].unlink(this);
    }
    if (links[1] != null) {
      links[1].unlink(this);
    }

    availableMarkers.remove(this);
    tryingToConnect = false;

    // MarkerBlockEntityのsetRemoved()を呼ぶ（destroy()はorigin系なので不要だがsuper呼び出しは必要）
    super.setRemoved();
  }

  private void unlink(PathMarkerBlockEntity tile) {
    if (links[0] == tile) {
      links[0] = null;
    }
    if (links[1] == tile) {
      links[1] = null;
    }

    if (!isFullyConnected() && !availableMarkers.contains(this) && level != null && !level.isClientSide) {
      availableMarkers.add(this);
    }

    setChanged();
  }

  // -----------------------------------------------------------------------
  // チャンクアンロード
  // -----------------------------------------------------------------------

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    availableMarkers.remove(this);
  }

  // -----------------------------------------------------------------------
  // NBT
  // -----------------------------------------------------------------------

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    // MarkerBlockEntityのsaveAdditional()はorigin系を保存するが、PathMarkerには不要
    // BuildCraftBlockEntityのsaveAdditional()のみ呼ぶ
    // super.super相当: CompoundTag保存はBlockEntityに委譲
    // ※ MarkerBlockEntityを経由するが、origin.isSet()がfalseなので実質何も書かれない
    super.saveAdditional(nbt);

    if (links[0] != null) {
      nbt.putLong("link0", links[0].worldPosition.asLong());
    }
    if (links[1] != null) {
      nbt.putLong("link1", links[1].worldPosition.asLong());
    }

    nbt.putBoolean("tryingToConnect", tryingToConnect);
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);

    if (nbt.contains("link0")) {
      loadLink0Pos = BlockPos.of(nbt.getLong("link0"));
    }
    if (nbt.contains("link1")) {
      loadLink1Pos = BlockPos.of(nbt.getLong("link1"));
    }

    tryingToConnect = nbt.getBoolean("tryingToConnect");
  }

  // -----------------------------------------------------------------------
  // 静的ユーティリティ
  // -----------------------------------------------------------------------

  public static void clearAvailableMarkers() {
    availableMarkers.clear();
  }

  public static void clearAvailableMarkers(Level world) {
    availableMarkers.removeIf(t -> t.level != null && t.level.dimension().equals(world.dimension()));
  }
}
