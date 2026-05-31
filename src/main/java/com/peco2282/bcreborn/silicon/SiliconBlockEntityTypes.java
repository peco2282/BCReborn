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
package com.peco2282.bcreborn.silicon;

import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.silicon.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornSilicon.MODID)
public class SiliconBlockEntityTypes {
  private static final BCRegistry REGISTRY = BCRebornSilicon.getRegistry();

  public static final RegistryObject<BlockEntityType<LaserBlockEntity>> LASER = REGISTRY.registerBlockEntityType("laser", () -> BlockEntityType.Builder.of(LaserBlockEntity::new, SiliconBlocks.LASER.get()).build(null));
  public static final RegistryObject<BlockEntityType<AssemblyTableBlockEntity>> ASSEMBLY_TABLE = REGISTRY.registerBlockEntityType("assembly_table", () -> BlockEntityType.Builder.of(AssemblyTableBlockEntity::new, SiliconBlocks.ASSEMBLY_TABLE.get()).build(null));
  public static final RegistryObject<BlockEntityType<AdvancedCraftingTableBlockEntity>> ADVANCED_CRAFTING_TABLE = REGISTRY.registerBlockEntityType("advanced_crafting_table", () -> BlockEntityType.Builder.of(AdvancedCraftingTableBlockEntity::new, SiliconBlocks.ADVANCED_CRAFTING_TABLE.get()).build(null));
  public static final RegistryObject<BlockEntityType<IntegrationTableBlockEntity>> INTEGRATION_TABLE = REGISTRY.registerBlockEntityType("integration_table", () -> BlockEntityType.Builder.of(IntegrationTableBlockEntity::new, SiliconBlocks.INTEGRATION_TABLE.get()).build(null));
  public static final RegistryObject<BlockEntityType<ChargingTableBlockEntity>> CHARGING_TABLE = REGISTRY.registerBlockEntityType("charging_table", () -> BlockEntityType.Builder.of(ChargingTableBlockEntity::new, SiliconBlocks.CHARGING_TABLE.get()).build(null));
  public static final RegistryObject<BlockEntityType<ProgrammingTableBlockEntity>> PROGRAMMING_TABLE = REGISTRY.registerBlockEntityType("programming_table", () -> BlockEntityType.Builder.of(ProgrammingTableBlockEntity::new, SiliconBlocks.PROGRAMMING_TABLE.get()).build(null));
  public static final RegistryObject<BlockEntityType<StampingTableBlockEntity>> STAMPING_TABLE = REGISTRY.registerBlockEntityType("stamping_table", () -> BlockEntityType.Builder.of(StampingTableBlockEntity::new, SiliconBlocks.STAMPING_TABLE.get()).build(null));
  public static final RegistryObject<BlockEntityType<PackagerBlockEntity>> PACKAGER = REGISTRY.registerBlockEntityType("packager", () -> BlockEntityType.Builder.of(PackagerBlockEntity::new, SiliconBlocks.PACKAGER.get()).build(null));
}
