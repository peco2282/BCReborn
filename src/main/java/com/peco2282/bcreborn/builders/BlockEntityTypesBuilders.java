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
package com.peco2282.bcreborn.builders;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.block.entity.*;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.CodingUtils;
import com.peco2282.bcreborn.common.bean.InitRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornBuilders.MODID)
public class BlockEntityTypesBuilders {
  private static final BCRegistry REGISTRY = BCRebornBuilders.getRegistry();

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
    return REGISTRY.registerBlockEntityType(name, type);
  }  public static final RegistryObject<BlockEntityType<QuarryBlockEntity>> QUARRY =
    register("quarry", of(QuarryBlockEntity::new, BuildersBlock.QUARRY));

  @SafeVarargs
  private static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block>... validBlocks) {
    return () -> new BlockEntityType<>(supplier, CodingUtils.map2Set(Arrays.asList(validBlocks), Supplier::get), null);
  }  public static final RegistryObject<BlockEntityType<BlueprintLibraryBlockEntity>> BLUEPRINT_LIBRARY = register(
    "blueprint_library", of(BlueprintLibraryBlockEntity::new, BuildersBlock.BLUEPRINT_LIBRARY));
  public static final RegistryObject<BlockEntityType<ArchitectBlockEntity>> ARCHITECT =
    register("architect", of(ArchitectBlockEntity::new, BuildersBlock.ARCHITECT));
  public static final RegistryObject<BlockEntityType<ConstructionMarkerBlockEntity>> CONSTRUCTION_MARKER =
    register("construction_marker", of(ConstructionMarkerBlockEntity::new, BuildersBlock.CONSTRUCTION_MARKER));
  public static final RegistryObject<BlockEntityType<BuilderBlockEntity>> BUILDER =
    register("builder", of(BuilderBlockEntity::new, BuildersBlock.BUILDER));
  public static final RegistryObject<BlockEntityType<FillerBlockEntity>> FILLER =
    register("filler", of(FillerBlockEntity::new, BuildersBlock.FILLER));




}
