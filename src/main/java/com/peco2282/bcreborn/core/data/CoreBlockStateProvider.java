package com.peco2282.bcreborn.core.data;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.core.BlocksCore;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CoreBlockStateProvider extends BlockStateProvider {
  public CoreBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornCore.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    simpleEngine(BlocksCore.WOODEN_ENGINE.get());
    simpleBlockWithItem(BlocksCore.BLUE_MARKER.get(), models().withExistingParent("blue_marker", mcLoc("block/template_torch")).texture("torch", "block/marker_block/default").renderType(mcLoc("cutout")));
  }

  private void simpleEngine(Block block) {
    // BERで描画するため builtin/entity モデルを使用
    ModelFile model = new ModelFile.UncheckedModelFile(mcLoc("builtin/entity"));
    simpleBlock(block, model);
    itemModels()
        .getBuilder(getName(block))
        .parent(model)
        .transforms()
        .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(30, 160, 0).translation(0, 3, -2).scale(0.23F, 0.23F, 0.23F).end()
        .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(30, 160, 0).translation(0, 3, 0).scale(0.375F, 0.375F, 0.375F).end()
        .transform(ItemDisplayContext.GUI).rotation(30, 160, 0).translation(2, 3, 0).scale(0.5325F, 0.5325F, 0.5325F).end()
        .end();
//    itemModels().withExistingParent(getName(block), ResourceLocation.withDefaultNamespace("item/generated"));
  }

  private ModelFile cubeAllWithBaseTex(Block block, String texturePath) {
    ResourceLocation tex = modLoc("block/" + texturePath);
    String modelName = getName(block);
    var all = models().cubeAll(modelName, tex);
    itemModels().withExistingParent(modelName, all.getLocation());
    return all;
  }

  @SuppressWarnings("deprecation")
  private static String getName(Block block) {
    return block.builtInRegistryHolder().key().location().getPath();
  }

}
