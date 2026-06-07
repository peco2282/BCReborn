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
package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.core.block.entity.BlueMarkerBlockEntity;
import com.peco2282.bcreborn.core.block.entity.PathMarkerBlockEntity;
import com.peco2282.bcreborn.core.block.entity.WoodEngineBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@InitRegister(modId = BCRebornCore.MODID, priority = 1)
public class CoreBlockEntityTypes {
  private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
    return REGISTRY.registerBlockEntityType(name, type);
  }

  @SafeVarargs
  private static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block>... validBlocks) {
    return () -> new BlockEntityType<>(supplier, Arrays.stream(validBlocks).map(Supplier::get).collect(Collectors.toSet()), null);
  }  public static final RegistryObject<BlockEntityType<WoodEngineBlockEntity>> WOODEN_ENGINE = register("wood_engine", of(WoodEngineBlockEntity::new, BlocksCore.WOODEN_ENGINE));



  public static final RegistryObject<BlockEntityType<PathMarkerBlockEntity>> PATH_MARKER = register("path_marker", of(PathMarkerBlockEntity::new, BlocksCore.PATH_MARKER));
  public static final RegistryObject<BlockEntityType<BlueMarkerBlockEntity>> BLUE_MARKER = register("blue_marker", of(BlueMarkerBlockEntity::new, BlocksCore.BLUE_MARKER));


}
