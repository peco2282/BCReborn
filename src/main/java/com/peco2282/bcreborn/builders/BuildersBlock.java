package com.peco2282.bcreborn.builders;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.block.*;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornBuilders.MODID, priority = 0)
public class BuildersBlock {
  private static final BCRegistry REGISTRY = BCRebornBuilders.getRegistry();

  public static final RegistryObject<ArchitectBlock> ARCHITECT = register("architect", ArchitectBlock::new);
  public static final RegistryObject<BuilderBlock> BUILDER = register("builder", BuilderBlock::new);
  public static final RegistryObject<FrameBlock> FRAME = register("frame", FrameBlock::new);
  public static final RegistryObject<ConstructionMarkerBlock> CONSTRUCTION_MARKER = register("construction_marker", ConstructionMarkerBlock::new);
  public static final RegistryObject<BlueprintLibraryBlock> BLUEPRINT_LIBRARY = register("blueprint_library", BlueprintLibraryBlock::new);

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> type) {
    return REGISTRY.registerBlockItem(name, type);
  }
}
