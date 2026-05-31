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

import com.google.common.collect.ImmutableMap;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornTransport.MODID, priority = 0)
public class BlocksTransport {
  public static final Map<PipeMaterial, Map<PipeType, RegistryObject<PipeBlock>>> PIPES_BY_MAT = new HashMap<>();
  public static final Map<PipeType, Map<PipeMaterial, RegistryObject<PipeBlock>>> PIPES_BY_TYPE = new HashMap<>();
  private static final BCRegistry REGISTRY = BCRebornTransport.getRegistry();

  static {
    for (PipeMaterial material : PipeMaterial.values()) {
      Map<PipeType, RegistryObject<PipeBlock>> materialPipes = new HashMap<>();
      for (PipeType type : PipeType.values()) {
        if (material.unsupports(type)) continue;
        String name = "pipe_" + material.getSerializedName() + "_" + type.getSerializedName();
        materialPipes.put(type, register(name, () -> new PipeBlock(type, material, BlockBehaviour.Properties.of().noOcclusion())));
      }
      if (!materialPipes.isEmpty()) {
        PIPES_BY_MAT.put(material, materialPipes);
      }
    }
    for (PipeType type : PipeType.values()) {
      Map<PipeMaterial, RegistryObject<PipeBlock>> typePipes = new HashMap<>();
      for (PipeMaterial material : PipeMaterial.values()) {
        if (material.unsupports(type)) continue;
        typePipes.put(material, PIPES_BY_MAT.get(material).get(type));
      }
      if (!typePipes.isEmpty()) {
        PIPES_BY_TYPE.put(type, typePipes);
      }
    }
  }


  public static List<RegistryObject<PipeBlock>> getPipeList() {
    return PIPES_BY_MAT.values().stream().flatMap(m -> m.values().stream()).toList();
  }

  public static Map<PipeType, RegistryObject<PipeBlock>> getPipesByMat(PipeMaterial material) {
    return ImmutableMap.copyOf(PIPES_BY_MAT.get(material));
  }

  public static Map<PipeMaterial, RegistryObject<PipeBlock>> getPipeByType(PipeType type) {
    return ImmutableMap.copyOf(PIPES_BY_TYPE.get(type));
  }

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> type) {
    return REGISTRY.registerBlockItem(name, type);
  }
}
