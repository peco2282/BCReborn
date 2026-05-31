package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.core.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID, priority = 0)
public class BlocksCore {
  private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

  public static final RegistryObject<WoodEngineBlock> WOODEN_ENGINE = register("wood_engine", WoodEngineBlock::new);
  public static final RegistryObject<PathMarkerBlock> PATH_MARKER = register("path_marker", PathMarkerBlock::new);
  public static final RegistryObject<SpringBlock> SPRING = register("eternal_spring", SpringBlock::new);
  public static final RegistryObject<BlueMarkerBlock> BLUE_MARKER = register("blue_marker", BlueMarkerBlock::new);
  public static final RegistryObject<BuildToolBlock> BUILD_TOOL = register("build_tool", BuildToolBlock::new);

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> block) {
    return REGISTRY.registerBlockItem(name, block);
  }
}
