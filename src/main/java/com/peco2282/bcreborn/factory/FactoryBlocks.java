package com.peco2282.bcreborn.factory;

import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.factory.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornFactory.MODID, priority = 0)
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
}
