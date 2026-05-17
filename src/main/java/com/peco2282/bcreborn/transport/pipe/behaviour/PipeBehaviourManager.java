package com.peco2282.bcreborn.transport.pipe.behaviour;

import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.energy.*;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid.*;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.item.*;

import java.util.HashMap;
import java.util.Map;

public class PipeBehaviourManager {
  private static final Map<PipeType, Map<PipeMaterial, PipeBehaviour>> BEHAVIOURS = new HashMap<>();

  static {
    // アイテムパイプ
    register(PipeType.ITEM, PipeMaterial.WOOD, WoodenItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.COBBLESTONE, CobblestoneItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.STONE, StoneItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.SANDSTONE, SandstoneItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.IRON, IronItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.GOLD, GoldenItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.OBSIDIAN, ObsidianItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.EMERALD, EmeraldItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.DIAMOND, DiamondItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.VOID, VoidItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.STRIPES, StripesItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.QUARTZ, QuartzItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.LAPIS, LapisItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.DAIZULI, DaizuliItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.EMZULI, EmzuliItemPipeBehaviour.INSTANCE);
    register(PipeType.ITEM, PipeMaterial.CLAY, ClayItemPipeBehaviour.INSTANCE);

    // 液体パイプ
    register(PipeType.FLUID, PipeMaterial.WOOD, WoodenFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.COBBLESTONE, CobblestoneFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.STONE, StoneFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.SANDSTONE, SandstoneFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.GOLD, GoldenFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.EMERALD, EmeraldFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.VOID, VoidFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.IRON, IronFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.DIAMOND, DiamondFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.CLAY, ClayFluidPipeBehaviour.INSTANCE);
    register(PipeType.FLUID, PipeMaterial.QUARTZ, QuartzFluidPipeBehaviour.INSTANCE);

    // エネルギーパイプ
    register(PipeType.ENERGY, PipeMaterial.WOOD, WoodenEnergyPipeBehaviour.INSTANCE);
    register(PipeType.ENERGY, PipeMaterial.COBBLESTONE, CobblestoneEnergyPipeBehaviour.INSTANCE);
    register(PipeType.ENERGY, PipeMaterial.STONE, StoneEnergyPipeBehaviour.INSTANCE);
    register(PipeType.ENERGY, PipeMaterial.QUARTZ, QuartzEnergyPipeBehaviour.INSTANCE);
    register(PipeType.ENERGY, PipeMaterial.IRON, IronEnergyPipeBehaviour.INSTANCE);
    register(PipeType.ENERGY, PipeMaterial.GOLD, GoldenEnergyPipeBehaviour.INSTANCE);
    register(PipeType.ENERGY, PipeMaterial.DIAMOND, DiamondEnergyPipeBehaviour.INSTANCE);
  }

  private static void register(PipeType type, PipeMaterial material, PipeBehaviour behaviour) {
    BEHAVIOURS.computeIfAbsent(type, k -> new HashMap<>()).put(material, behaviour);
  }

  public static PipeBehaviour getBehaviour(PipeType type, PipeMaterial material) {
    Map<PipeMaterial, PipeBehaviour> materialMap = BEHAVIOURS.get(type);
    if (materialMap != null) {
      return materialMap.get(material);
    }
    throw new IllegalArgumentException("No behaviour registered for " + type + " and " + material);
  }
}
