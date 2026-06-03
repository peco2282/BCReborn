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
import com.peco2282.bcreborn.builders.block.*;
import com.peco2282.bcreborn.builders.item.ConstructionMarkerBlockItem;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornBuilders.MODID)
public class BuildersBlock {
  private static final BCRegistry REGISTRY = BCRebornBuilders.getRegistry();

  public static final RegistryObject<ArchitectBlock> ARCHITECT = register("architect", ArchitectBlock::new);
  public static final RegistryObject<BuilderBlock> BUILDER = register("builder", BuilderBlock::new);
  public static final RegistryObject<FrameBlock> FRAME = register("frame", FrameBlock::new);
  public static final RegistryObject<ConstructionMarkerBlock> CONSTRUCTION_MARKER = REGISTRY.registerBlockItem("construction_marker", ConstructionMarkerBlockItem::new, ConstructionMarkerBlock::new);
  public static final RegistryObject<BlueprintLibraryBlock> BLUEPRINT_LIBRARY = register("blueprint_library", BlueprintLibraryBlock::new);
  public static final RegistryObject<QuarryBlock> QUARRY = register("quarry", QuarryBlock::new);
  public static final RegistryObject<FillerBlock> FILLER = register("filler", FillerBlock::new);

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> type) {
    return REGISTRY.registerBlockItem(name, type);
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.accept(ARCHITECT.get());
    output.accept(BUILDER.get());
    output.accept(FRAME.get());
    output.accept(CONSTRUCTION_MARKER.get());
    output.accept(BLUEPRINT_LIBRARY.get());
    output.accept(QUARRY.get());
    output.accept(FILLER.get());
  }
}
