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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.registry.KeyedRegistryObject;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@InitRegister(modId = BCRebornTransport.MODID)
public class TransportBlocks {
  private static final BCRegistry REGISTRY = BCRebornTransport.getRegistry();

  public static final KeyedRegistryObject.TwoKeys<PipeBlock, PipeType, PipeMaterial> PIPES = KeyedRegistryObject.two(
    Arrays.stream(PipeType.values()).toList(),
    Arrays.stream(PipeMaterial.values()).toList(),
    (type, material) -> "pipe_" + material.getSerializedName() + "_" + type.getSerializedName(),
    REGISTRY::registerBlockItem,
    (type, material) -> new PipeBlock(type, material, BlockBehaviour.Properties.of().noOcclusion()),
    PipeType::supports
  );


  public static List<RegistryObject<PipeBlock>> getPipeList() {
    return PIPES.getAll();
  }

  @Nullable
  public static RegistryObject<PipeBlock> get(PipeType type, PipeMaterial material) {
    return PIPES.get(type, material);
  }

  public static void pipesForEach(TriConsumer<PipeType, PipeMaterial, RegistryObject<PipeBlock>> consumer) {
    for (PipeMaterial material : PipeMaterial.values()) {
      for (PipeType type : PipeType.values()) {
        if (!type.supports(material)) continue;
        RegistryObject<PipeBlock> block = PIPES.get(type, material);
        consumer.accept(type, material, block);
      }
    }
  }

  public static List<Map<PipeType, RegistryObject<PipeBlock>>> getAllPipesByMat() {
    var map = ImmutableMap.<PipeType, RegistryObject<PipeBlock>>builder();
    var list = ImmutableList.<Map<PipeType, RegistryObject<PipeBlock>>>builder();
    for (var material : PipeMaterial.values()) {
      map.putAll(PIPES.getMapByKey2(material));
    }
    return list.build();
  }


  public static Map<PipeType, RegistryObject<PipeBlock>> getPipesByMat(PipeMaterial material) {
    return PIPES.getMapByKey2(material);
  }

  public static Map<PipeMaterial, RegistryObject<PipeBlock>> getAllPipeByType() {
    var map = ImmutableMap.<PipeMaterial, RegistryObject<PipeBlock>>builder();
    for (var type : PipeType.values()) {
      map.putAll(PIPES.getMapByKey1(type));
    }
    return map.build();
  }

  public static Map<PipeMaterial, RegistryObject<PipeBlock>> getPipeByType(PipeType type) {
    return PIPES.getMapByKey1(type);
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.acceptAll(getPipeList().stream().map(RegistryObject::get).map(it -> {
      System.out.println("Tr" + it);return it;
    }).map(ItemStack::new).toList());
  }
}
