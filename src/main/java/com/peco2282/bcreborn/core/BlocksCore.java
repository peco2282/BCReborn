package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.core.block.WoodEngineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID, priority = 0)
public class BlocksCore {
  private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

  public static final RegistryObject<WoodEngineBlock> WOODEN_ENGINE = register("wood_engine", WoodEngineBlock::new);

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> block) {
    return REGISTRY.registerBlockItem(name, block);
  }
}
