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
package com.peco2282.bcreborn.factory;

import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.factory.block.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornFactory.MODID)
public class FactoryBlocks {
  private static final BCRegistry REGISTRY = BCRebornFactory.getRegistry();

  public static final RegistryObject<AutoWorkbenchBlock> AUTO_WORKBENCH = register("auto_workbench", AutoWorkbenchBlock::new);
  public static final RegistryObject<HopperBlock> HOPPER = register("hopper", HopperBlock::new);
  public static final RegistryObject<FloodGateBlock> FLOOD_GATE = register("flood_gate", FloodGateBlock::new);
  public static final RegistryObject<MiningWellBlock> MINING_WELL = register("mining_well", MiningWellBlock::new);
  public static final RegistryObject<PlainPipeBlock> PLAIN_PIPE = register("plain_pipe", PlainPipeBlock::new);
  public static final RegistryObject<PumpBlock> PUMP = register("pump", PumpBlock::new);
  public static final RegistryObject<RefineryBlock> REFINERY = register("refinery", RefineryBlock::new);
  public static final RegistryObject<TankBlock> TANK = register("tank", TankBlock::new);

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> block) {
    return REGISTRY.registerBlockItem(name, block);
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.accept(AUTO_WORKBENCH.get());
    output.accept(HOPPER.get());
    output.accept(FLOOD_GATE.get());
    output.accept(MINING_WELL.get());
    output.accept(PUMP.get());
    output.accept(REFINERY.get());
    output.accept(TANK.get());
  }
}
