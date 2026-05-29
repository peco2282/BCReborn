package com.peco2282.bcreborn.builders.data;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.BuildersBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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
    ResourceLocation builder = models()
        .getBuilder("builder")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("builder", "bottom"))
        .texture("front", createTexture("builder", "front"))
        .texture("side", createTexture("builder", "side"))
        .texture("back", createTexture("builder", "back"))
        .texture("top", createTexture("builder", "top"))
        .getLocation();

    ResourceLocation filler = models()
        .getBuilder("filler")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("filler", "bottom"))
        .texture("front", createTexture("filler", "front"))
        .texture("side", createTexture("filler", "side"))
        .texture("back", createTexture("filler", "side"))
        .texture("top", createTexture("filler", "top"))
        .getLocation();

    ResourceLocation quarry = models()
        .getBuilder("quarry")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("quarry", "bottom"))
        .texture("front", createTexture("quarry", "front"))
        .texture("side", createTexture("quarry", "side"))
        .texture("back", createTexture("quarry", "back"))
        .texture("top", createTexture("quarry", "top"))
        .getLocation();

    simpleBlockWithItem(BuildersBlock.BUILDER.get(), models().getExistingFile(builder));
    simpleBlockWithItem(BuildersBlock.FILLER.get(), models().getExistingFile(filler));
    simpleBlockWithItem(BuildersBlock.QUARRY.get(),models().getExistingFile(quarry));
  }

  private ModelFile.UncheckedModelFile unCheckedModel(String name) {
    return new ModelFile.UncheckedModelFile(modLoc("block/" + name));
  }
  private ModelFile.UncheckedModelFile unCheckedModel(ResourceLocation location) {
    return new ModelFile.UncheckedModelFile(location);
  }

  private ResourceLocation createTexture(String dir, String path) {
    return BCRebornBuilders.location("block/" + dir + "_block/" + path);
  }
}
