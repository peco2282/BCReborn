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
import com.peco2282.bcreborn.common.CodingUtils;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.energy.block.entity.CreativeEngineBlockEntity;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;
import com.peco2282.bcreborn.energy.block.entity.StoneEngineBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornEnergy.MODID)
public class BlockEntityTypesEnergy {
  private static final BCRegistry REGISTRY = BCRebornEnergy.getRegistry();

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
    return REGISTRY.registerBlockEntityType(name, type);
  }

  @SafeVarargs
  private static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block>... validBlocks) {
    return () -> new BlockEntityType<>(supplier, CodingUtils.map2Set(Arrays.asList(validBlocks), Supplier::get), null);
  }  public static final RegistryObject<BlockEntityType<StoneEngineBlockEntity>> STONE_ENGINE = register("stone_engine", of(StoneEngineBlockEntity::new, BlocksEnergy.STONE_ENGINE));



  public static final RegistryObject<BlockEntityType<CreativeEngineBlockEntity>> CREATIVE_ENGINE = register("creative_engine", of(CreativeEngineBlockEntity::new, BlocksEnergy.CREATIVE_ENGINE));
  public static final RegistryObject<BlockEntityType<IronEngineBlockEntity>> IRON_ENGINE = register("iron_engine", of(IronEngineBlockEntity::new, BlocksEnergy.IRON_ENGINE));


}
