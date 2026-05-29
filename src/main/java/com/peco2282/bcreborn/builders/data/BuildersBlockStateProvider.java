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
    ResourceLocation architect = models()
        .getBuilder("architect")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("architect", "bottom"))
        .texture("front", createTexture("architect", "front"))
        .texture("left", createTexture("architect", "left"))
        .texture("right", createTexture("architect", "right"))
        .texture("back", createTexture("architect", "back"))
        .texture("top", createTexture("architect", "top"))
        .getLocation();

    ResourceLocation builder = models()
        .getBuilder("builder")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("builder", "bottom"))
        .texture("front", createTexture("builder", "front"))
        .texture("left", createTexture("builder", "side"))
        .texture("right", createTexture("builder", "side"))
        .texture("back", createTexture("builder", "back"))
        .texture("top", createTexture("builder", "top"))
        .getLocation();

    ResourceLocation filler = models()
        .getBuilder("filler")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("filler", "bottom"))
        .texture("front", createTexture("filler", "front"))
        .texture("left", createTexture("filler", "side"))
        .texture("right", createTexture("filler", "side"))
        .texture("back", createTexture("filler", "side"))
        .texture("top", createTexture("filler", "top"))
        .getLocation();

    ResourceLocation library = models()
        .getBuilder("library")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("library", "bottom"))
        .texture("front", createTexture("library", "front"))
        .texture("left", createTexture("library", "left"))
        .texture("right", createTexture("library", "right"))
        .texture("back", createTexture("library", "back"))
        .texture("top", createTexture("library", "top"))
        .getLocation();

    ResourceLocation marker = models().cross("construction_marker", createTexture("construction_marker", "default")).getLocation();

    ResourceLocation quarry = models()
        .getBuilder("quarry")
        .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
        .texture("bottom", createTexture("quarry", "bottom"))
        .texture("front", createTexture("quarry", "front"))
        .texture("left", createTexture("quarry", "side"))
        .texture("right", createTexture("quarry", "side"))
        .texture("back", createTexture("quarry", "back"))
        .texture("top", createTexture("quarry", "top"))
        .getLocation();

    getVariantBuilder(BuildersBlock.CONSTRUCTION_MARKER.get())
        .forAllStates(state -> ConfiguredModel.builder()
            .modelFile(models().getExistingFile(marker))
            .build()
        );
    simpleBlockWithItem(BuildersBlock.ARCHITECT.get(), models().getExistingFile(architect));
    simpleBlockWithItem(BuildersBlock.BUILDER.get(), models().getExistingFile(builder));
    simpleBlockWithItem(BuildersBlock.FILLER.get(), models().getExistingFile(filler));
    simpleBlockWithItem(BuildersBlock.BLUEPRINT_LIBRARY.get(),models().getExistingFile(library));
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
