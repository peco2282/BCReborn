package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.common.data.tag.CommonBlockTags;
import com.peco2282.bcreborn.core.BlocksCore;
import com.peco2282.bcreborn.energy.BlocksEnergy;
import com.peco2282.bcreborn.energy.FluidsEnergy;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.transport.BlocksTransport;
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
    fuelBucketTag();

    tag(CommonBlockTags.BUILDERS).add(BuildersBlock.ARCHITECT.get(), BuildersBlock.BUILDER.get(), BuildersBlock.FRAME.get(), BuildersBlock.CONSTRUCTION_MARKER.get(), BuildersBlock.BLUEPRINT_LIBRARY.get(), BuildersBlock.QUARRY.get(), BuildersBlock.FILLER.get());
    tag(CommonBlockTags.CORE)
        .add(BlocksCore.WOODEN_ENGINE.get(), BlocksCore.SPRING.get(), BlocksCore.BLUE_MARKER.get(), BlocksCore.PATH_MARKER.get(), BlocksCore.BUILD_TOOL.get());
    tag(CommonBlockTags.ENERGY).add(BlocksEnergy.STONE_ENGINE.get(), BlocksEnergy.IRON_ENGINE.get(), BlocksEnergy.CREATIVE_ENGINE.get()).addTag(CommonBlockTags.FUEL);
    tag(CommonBlockTags.FACTORY)
        .add(FactoryBlocks.AUTO_WORKBENCH.get(), FactoryBlocks.HOPPER.get(), FactoryBlocks.FLOOD_GATE.get(), FactoryBlocks.MINING_WELL.get(), FactoryBlocks.PLAIN_PIPE.get(), FactoryBlocks.PUMP.get(), FactoryBlocks.REFINERY.get(), FactoryBlocks.TANK.get());
    tag(CommonBlockTags.ROBOTICS).add(RoboticsBlocks.REQUESTER.get(), RoboticsBlocks.ZONE_PLAN.get());
    tag(CommonBlockTags.SILICON).addTag(CommonBlockTags.LASER_TABLE).add(SiliconBlocks.LASER.get(), SiliconBlocks.PACKAGER.get());
    tag(CommonBlockTags.TRANSPORT).addTag(CommonBlockTags.PIPES);
  }

  private void fuelBucketTag() {
    tag(CommonBlockTags.FUEL)
        .add(FluidsEnergy.OIL_BLOCK.get(), FluidsEnergy.FUEL_BLOCK.get());
  }

  private void laserTableTag() {
    tag(CommonBlockTags.LASER_TABLE).add(SiliconBlocks.ASSEMBLY_TABLE.get(), SiliconBlocks.ADVANCED_CRAFTING_TABLE.get(), SiliconBlocks.INTEGRATION_TABLE.get(), SiliconBlocks.CHARGING_TABLE.get(), SiliconBlocks.PROGRAMMING_TABLE.get(), SiliconBlocks.STAMPING_TABLE.get());
  }

  void engineTag() {
    tag(CommonBlockTags.ENGINES)
        .add(BlocksCore.WOODEN_ENGINE.get())
        .add(BlocksEnergy.STONE_ENGINE.get())
        .add(BlocksEnergy.IRON_ENGINE.get())
        .add(BlocksEnergy.CREATIVE_ENGINE.get());
  }

  void pipeTag() {
    tag(CommonBlockTags.PIPES).addTag(
        CommonBlockTags.PIPE_ITEM).addTag(
        CommonBlockTags.PIPE_FLUID).addTag(
        CommonBlockTags.PIPE_ENERGY
    );

    var items = BlocksTransport
        .getPipeByType(PipeType.ITEM)
        .values()
        .stream()
        .map(RegistryObject::get)
        .toArray(Block[]::new);

    var fluids = BlocksTransport
        .getPipeByType(PipeType.FLUID)
        .values()
        .stream()
        .map(RegistryObject::get)
        .toArray(Block[]::new);

    var energy = BlocksTransport
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
