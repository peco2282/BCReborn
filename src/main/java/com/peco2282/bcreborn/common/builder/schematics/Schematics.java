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
package com.peco2282.bcreborn.common.builder.schematics;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.common.bean.InitRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID)
public class Schematics {

  public static final RegistryObject<SchematicBlockCreative> CREATIVE_BLOCK = register("creative_block", SchematicBlockCreative::new);
  public static final RegistryObject<SchematicBlockFloored> FLOORED_BLOCK = register("floored_block", SchematicBlockFloored::new);
  public static final RegistryObject<SchematicFree> FREE_BLOCK = register("free_block", SchematicFree::new);
  public static final RegistryObject<SchematicIgnore> IGNORE_BLOCK = register("ignore_block", SchematicIgnore::new);
  public static final RegistryObject<SchematicIgnoreMeta> IGNORE_META = register("ignore_meta", SchematicIgnoreMeta::new);
  public static final RegistryObject<SchematicRotateMeta> ROTATE_META = register("rotate_meta", SchematicRotateMeta::new);
  public static final RegistryObject<SchematicRotateMetaSupported> ROTATE_META_SUPPORTED = register("rotate_meta_supported", SchematicRotateMetaSupported::new);
  public static final RegistryObject<SchematicBlockEntityCreative> CREATIVE_TILE = register("creative_tile", SchematicBlockEntityCreative::new);
  public static final RegistryObject<SchematicWallSide> WALL_SIDE = register("wall_side", SchematicWallSide::new);

  private static <S extends Schematic> RegistryObject<S> register(String name, Supplier<S> supplier) {
    return BCRebornCore.getRegistry().registerSchematic(name, supplier);
  }
}
