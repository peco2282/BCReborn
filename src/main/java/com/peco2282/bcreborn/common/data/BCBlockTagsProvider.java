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
package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.common.data.tag.CommonBlockTags;
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.energy.EnergyFluids;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BCBlockTagsProvider extends BlockTagsProvider {
  public BCBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, modId, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider p_256380_) {
    engineTag();
    pipeTag();
    laserTableTag();
    fuelTag();

    tag(CommonBlockTags.BUILDERS).add(BuildersBlock.ARCHITECT.get(), BuildersBlock.BUILDER.get(), BuildersBlock.FRAME.get(), BuildersBlock.CONSTRUCTION_MARKER.get(), BuildersBlock.BLUEPRINT_LIBRARY.get(), BuildersBlock.QUARRY.get(), BuildersBlock.FILLER.get());
    tag(CommonBlockTags.CORE)
      .add(CoreBlocks.WOODEN_ENGINE.get(), CoreBlocks.SPRING.get(), CoreBlocks.BLUE_MARKER.get(), CoreBlocks.PATH_MARKER.get(), CoreBlocks.BUILD_TOOL.get());
    tag(CommonBlockTags.ENERGY).add(EnergyBlocks.STONE_ENGINE.get(), EnergyBlocks.IRON_ENGINE.get(), EnergyBlocks.CREATIVE_ENGINE.get()).addTag(CommonBlockTags.FUEL);
    tag(CommonBlockTags.FACTORY)
      .add(FactoryBlocks.AUTO_WORKBENCH.get(), FactoryBlocks.HOPPER.get(), FactoryBlocks.FLOOD_GATE.get(), FactoryBlocks.MINING_WELL.get(), FactoryBlocks.PLAIN_PIPE.get(), FactoryBlocks.PUMP.get(), FactoryBlocks.REFINERY.get(), FactoryBlocks.TANK.get());
    tag(CommonBlockTags.ROBOTICS).add(RoboticsBlocks.REQUESTER.get(), RoboticsBlocks.ZONE_PLAN.get());
    tag(CommonBlockTags.SILICON).addTag(CommonBlockTags.LASER_TABLE).add(SiliconBlocks.LASER.get(), SiliconBlocks.PACKAGER.get());
    tag(CommonBlockTags.TRANSPORT).addTag(CommonBlockTags.PIPES);
  }

  private void fuelTag() {
    tag(CommonBlockTags.FUEL)
      .add(EnergyFluids.OIL_BLOCK.get(), EnergyFluids.FUEL_BLOCK.get());
  }

  private void laserTableTag() {
    tag(CommonBlockTags.LASER_TABLE).add(SiliconBlocks.ASSEMBLY_TABLE.get(), SiliconBlocks.ADVANCED_CRAFTING_TABLE.get(), SiliconBlocks.INTEGRATION_TABLE.get(), SiliconBlocks.CHARGING_TABLE.get(), SiliconBlocks.PROGRAMMING_TABLE.get(), SiliconBlocks.STAMPING_TABLE.get());
  }

  void engineTag() {
    tag(CommonBlockTags.ENGINES)
      .add(CoreBlocks.WOODEN_ENGINE.get())
      .add(EnergyBlocks.STONE_ENGINE.get())
      .add(EnergyBlocks.IRON_ENGINE.get())
      .add(EnergyBlocks.CREATIVE_ENGINE.get());
  }

  void pipeTag() {
    tag(CommonBlockTags.PIPES).addTag(
      CommonBlockTags.PIPE_ITEM).addTag(
      CommonBlockTags.PIPE_FLUID).addTag(
      CommonBlockTags.PIPE_ENERGY
    );

    var items = TransportBlocks
      .getPipeByType(PipeType.ITEM)
      .values()
      .stream()
      .map(RegistryObject::get)
      .toArray(Block[]::new);

    var fluids = TransportBlocks
      .getPipeByType(PipeType.FLUID)
      .values()
      .stream()
      .map(RegistryObject::get)
      .toArray(Block[]::new);

    var energy = TransportBlocks
      .getPipeByType(PipeType.ENERGY)
      .values()
      .stream()
      .map(RegistryObject::get)
      .toArray(Block[]::new);

    tag(CommonBlockTags.PIPE_ITEM).add(items);
    tag(CommonBlockTags.PIPE_FLUID).add(fluids);
    tag(CommonBlockTags.PIPE_ENERGY).add(energy);

  }
}
