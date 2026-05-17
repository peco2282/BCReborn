package com.peco2282.bcreborn.energy;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.energy.block.CreativeEngineBlock;
import com.peco2282.bcreborn.energy.block.IronEngineBlock;
import com.peco2282.bcreborn.energy.block.StoneEngineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornEnergy.MODID, priority = 0)
public class BlocksEnergy {
  private static final BCRegistry REGISTRY = BCRebornEnergy.getRegistry();

  public static final RegistryObject<CreativeEngineBlock> CREATIVE_ENGINE = register("creative_engine", CreativeEngineBlock::new);
  public static final RegistryObject<IronEngineBlock> IRON_ENGINE = register("iron_engine", IronEngineBlock::new);
  public static final RegistryObject<StoneEngineBlock> STONE_ENGINE = register("stone_engine", StoneEngineBlock::new);

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> type) {
    return REGISTRY.registerBlockItem(name, type);
  }
}
