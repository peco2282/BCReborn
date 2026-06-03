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
package com.peco2282.bcreborn.energy;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.energy.block.CreativeEngineBlock;
import com.peco2282.bcreborn.energy.block.IronEngineBlock;
import com.peco2282.bcreborn.energy.block.StoneEngineBlock;
import com.peco2282.bcreborn.common.item.EngineBlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornEnergy.MODID)
public class BlocksEnergy {
  private static final BCRegistry REGISTRY = BCRebornEnergy.getRegistry();

  public static final RegistryObject<CreativeEngineBlock> CREATIVE_ENGINE = REGISTRY.registerBlockItem("creative_engine", EngineBlockItem::new, CreativeEngineBlock::new);
  public static final RegistryObject<IronEngineBlock> IRON_ENGINE = REGISTRY.registerBlockItem("iron_engine", EngineBlockItem::new, IronEngineBlock::new);
  public static final RegistryObject<StoneEngineBlock> STONE_ENGINE = REGISTRY.registerBlockItem("stone_engine", EngineBlockItem::new, StoneEngineBlock::new);

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> type) {
    return REGISTRY.registerBlockItem(name, type);
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.accept(CREATIVE_ENGINE.get());
    output.accept(IRON_ENGINE.get());
    output.accept(STONE_ENGINE.get());
  }
}
