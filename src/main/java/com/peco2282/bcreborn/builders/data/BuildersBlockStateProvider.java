package com.peco2282.bcreborn.builders.data;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.BuildersBlock;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BuildersBlockStateProvider extends BlockStateProvider {
  public BuildersBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornBuilders.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlockWithItem(BuildersBlock.BUILDER.get(), unCheckedModel("builder"));
    simpleBlockWithItem(BuildersBlock.FILLER.get(), unCheckedModel("filler"));
    simpleBlockWithItem(BuildersBlock.QUARRY.get(), unCheckedModel("quarry"));
  }

  private ModelFile.UncheckedModelFile unCheckedModel(String name) {
    return new ModelFile.UncheckedModelFile(modLoc("block/" + name));
  }
}
