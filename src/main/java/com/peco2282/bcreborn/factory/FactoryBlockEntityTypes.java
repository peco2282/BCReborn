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
import com.peco2282.bcreborn.factory.block.entity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@InitRegister(modId = BCRebornFactory.MODID)
public class FactoryBlockEntityTypes {
  private static final BCRegistry REGISTRY = BCRebornFactory.getRegistry();

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> supplier) {
    return REGISTRY.registerBlockEntityType(name, supplier);
  }

  @SafeVarargs
  static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BlockEntityType.BlockEntitySupplier<T> supplier, RegistryObject<? extends Block>... blocks) {
    return () -> new BlockEntityType<>(supplier, Arrays.stream(blocks).map(RegistryObject::get).collect(Collectors.toUnmodifiableSet()), null);
  }

  public static final RegistryObject<BlockEntityType<AutoWorkbenchBlockEntity>> AUTO_WORKBENCH = register("auto_workbench", of(AutoWorkbenchBlockEntity::new, FactoryBlocks.AUTO_WORKBENCH));


  public static final RegistryObject<BlockEntityType<FloodGateBlockEntity>> FLOOD_GATE = register("flood_gate", of(FloodGateBlockEntity::new, FactoryBlocks.FLOOD_GATE));
  public static final RegistryObject<BlockEntityType<HopperBlockEntity>> HOPPER = register("hopper", of(HopperBlockEntity::new, FactoryBlocks.HOPPER));
  public static final RegistryObject<BlockEntityType<MiningWellBlockEntity>> MINING_WELL = register("mining_well", of(MiningWellBlockEntity::new, FactoryBlocks.MINING_WELL));
  public static final RegistryObject<BlockEntityType<PumpBlockEntity>> PUMP = register("pump", of(PumpBlockEntity::new, FactoryBlocks.PUMP));
  public static final RegistryObject<BlockEntityType<RefineryBlockEntity>> REFINERY = register("refinery", of(RefineryBlockEntity::new, FactoryBlocks.REFINERY));
  public static final RegistryObject<BlockEntityType<TankBlockEntity>> TANK = register("tank", of(TankBlockEntity::new, FactoryBlocks.TANK));


}
