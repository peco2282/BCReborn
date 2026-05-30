package com.peco2282.bcreborn.core.block.entity;

import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.LaserKind;
import com.peco2282.bcreborn.common.utils.LaserUtils;
import com.peco2282.bcreborn.api.tiles.ITileAreaProvider;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;

public class BlueMarkerBlockEntity extends MarkerBlockEntity implements ITileAreaProvider {
  public BlueMarkerBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesCore.BLUE_MARKER.get(), pos, state);
  }

  @Override
  public void updateSignals() {
    super.updateSignals();
    if (level != null && !level.isClientSide) {
      if (showSignals) {
        createLasers();
      } else {
        destroyLasers();
      }
    }
  }

  @Override
  public void createLasers() {
    if (level == null || level.isClientSide) {
      return;
    }
    if (!origin.isSet()) {
      return;
    }
    destroyLasers();

    double xMin = origin.posMin.getX();
    double yMin = origin.posMin.getY();
    double zMin = origin.posMin.getZ();
    double xMax = origin.posMax.getX();
    double yMax = origin.posMax.getY();
    double zMax = origin.posMax.getZ();

    if (xMin == xMax && yMin == yMax && zMin == zMax) {
      lasers.clear();
      // 同一座標でも、いずれかの軸がorigin.vect[i]と繋がっている場合はsignalsで描画されるため、
      // ここではbox（lasers）が空であることを保証して終了する
      setChanged();
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
      return;
    }
    Origin o = origin;
    BlockPos pos = worldPosition;
    int xCoord = pos.getX();
    int yCoord = pos.getY();
    int zCoord = pos.getZ();
    if (!origin.vect[0].isSet()) {
      o.posMin.setX(o.vectO.pos.getX());
      o.posMax.setX(o.vectO.pos.getX());
    } else if (origin.vect[0].pos.getX() < xCoord) {
      o.posMin.setX(origin.vect[0].pos.getX());
      o.posMax.setX(xCoord);
    } else {
      o.posMin.setX(xCoord);
      o.posMax.setX(origin.vect[0].pos.getX());
    }

    if (!origin.vect[1].isSet()) {
      o.posMin.setY(o.vectO.pos.getY());
      o.posMax.setY(o.vectO.pos.getY());
    } else if (origin.vect[1].pos.getY() < yCoord) {
      o.posMin.setY(origin.vect[1].pos.getY());
      o.posMax.setY(yCoord);
    } else {
      o.posMin.setY(yCoord);
      o.posMax.setY(origin.vect[1].pos.getY());
    }

    if (!origin.vect[2].isSet()) {
      o.posMin.setZ(o.vectO.pos.getZ());
      o.posMax.setZ(o.vectO.pos.getZ());
    } else if (origin.vect[2].pos.getZ() < zCoord) {
      o.posMin.setZ(origin.vect[2].pos.getZ());
      o.posMax.setZ(zCoord);
    } else {
      o.posMin.setZ(zCoord);
      o.posMax.setZ(origin.vect[2].pos.getZ());
    }
    lasers = LaserUtils.createLaserDataBox(o.posMin.getX(), o.posMin.getY(), o.posMin.getZ(), o.posMax.getX(), o.posMax.getY(), o.posMax.getZ(), LaserKind.Blue);
    for (LaserData ld : lasers) {
      ld.isGlowing = true;
    }

    setChanged();
    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
  }

  @Override
  public void destroy() {
    destroyLasers();
    super.destroy();
  }
}
