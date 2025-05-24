/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.PipeType;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;
import peco2282.bcreborn.transport.block.pipe.PipeStorage;

public abstract class EnergyPipeBlockEntity extends BasePipeBlockEntity {
  public EnergyPipeBlockEntity(
      BlockEntityType<?> p_155228_,
      BlockPos p_155229_,
      BlockState p_155230_,
      PipeMaterial material) {
    super(p_155228_, p_155229_, p_155230_, material, PipeType.ENERGY);
  }

  @Override
  public PipeStorage.EnergyStorage getStorage() {
    return this.storage.asEnergyStorage();
  }
}
