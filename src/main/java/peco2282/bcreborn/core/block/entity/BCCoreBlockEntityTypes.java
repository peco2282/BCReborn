/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCCoreBlockEntityTypes {
  public static final RegistryObject<BlockEntityType<EngineBlockEntity>> ENGINE =
      register(
          "engine",
          () ->
              BlockEntityType.Builder.of(
                      EngineBlockEntity::new,
                      BCCoreBlocks.WOOD_ENGINE.get(),
                      BCCoreBlocks.STONE_ENGINE.get(),
                      BCCoreBlocks.IRON_ENGINE.get(),
                      BCCoreBlocks.CREATIVE_ENGINE.get())
                  .build(null));
  public static final RegistryObject<BlockEntityType<IronEngineBlockEntity>> IRON_ENGINE =
      register(
          "iron_engine",
          () ->
              BlockEntityType.Builder.of(IronEngineBlockEntity::new, BCCoreBlocks.IRON_ENGINE.get())
                  .build(null));
  public static final RegistryObject<BlockEntityType<MarkerVolumeBlockEntity>> MARKER_VOLUME =
      register(
          "volume_marker",
          () ->
              BlockEntityType.Builder.of(
                      MarkerVolumeBlockEntity::new, BCCoreBlocks.MARKER_VOLUME.get())
                  .build(null));

  private static <T extends BlockEntity & BCBlockEntity>
      RegistryObject<BlockEntityType<T>> register(
          final String name, final Supplier<BlockEntityType<T>> type) {
    return BCRegistry.registerBlockEntityType(name, type);
  }
}
