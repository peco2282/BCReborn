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
package com.peco2282.bcreborn.transport;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@InitRegister(modId = BCRebornTransport.MODID, priority = 1)
public class BlockEntityTypesTransport {
  private static final BCRegistry REGISTRY = BCRebornTransport.getRegistry();

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
    return REGISTRY.registerBlockEntityType(name, type);
  }

  private static Set<Block> filter(PipeType type) {
    return BlocksTransport.getPipeByType(type).values().stream().map(RegistryObject::get).collect(Collectors.toUnmodifiableSet());
  }

  public static final RegistryObject<BlockEntityType<PipeBlockEntity>> ITEM_PIPE = register("item_pipe", () -> new BlockEntityType<>(PipeBlockEntity::new, filter(PipeType.ITEM), null));

  public static final RegistryObject<BlockEntityType<PipeBlockEntity>> FLUID_PIPE = register("fluid_pipe", () -> new BlockEntityType<>(PipeBlockEntity::new, filter(PipeType.FLUID), null));

  public static final RegistryObject<BlockEntityType<PipeBlockEntity>> ENERGY_PIPE = register("energy_pipe", () -> new BlockEntityType<>(PipeBlockEntity::new, filter(PipeType.ENERGY), null));

  public static final RegistryObject<BlockEntityType<PipeBlockEntity>> PIPE = register("pipe", () -> new BlockEntityType<>(PipeBlockEntity::new, BlocksTransport.getPipeList().stream().map(RegistryObject::get).collect(Collectors.toUnmodifiableSet()), null));
}
