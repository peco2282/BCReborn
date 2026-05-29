package com.peco2282.bcreborn.core.block.entity;

import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.LaserKind;
import com.peco2282.bcreborn.common.utils.LaserUtils;
import com.peco2282.bcreborn.api.tiles.ITileAreaProvider;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import net.minecraft.core.BlockPos;
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
      return;
    }

    LaserData[] box = LaserUtils.createLaserDataBox(xMin, yMin, zMin, xMax, yMax, zMax, LaserKind.Blue);
    for (LaserData ld : box) {
      ld.isGlowing = true;
      lasers.add(ld);
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
