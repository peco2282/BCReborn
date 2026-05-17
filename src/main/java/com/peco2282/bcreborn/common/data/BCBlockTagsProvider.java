package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.common.data.tag.CommonBlockTags;
import com.peco2282.bcreborn.core.BlocksCore;
import com.peco2282.bcreborn.energy.BlocksEnergy;
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

    tag(CommonBlockTags.CORE).add(BlocksCore.WOODEN_ENGINE.get());
    tag(CommonBlockTags.ENERGY).add(BlocksEnergy.STONE_ENGINE.get(), BlocksEnergy.IRON_ENGINE.get(), BlocksEnergy.CREATIVE_ENGINE.get());

    tag(CommonBlockTags.TRANSPORT).addTag(CommonBlockTags.PIPES);
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
