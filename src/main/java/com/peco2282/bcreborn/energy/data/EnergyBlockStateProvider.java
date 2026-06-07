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
package com.peco2282.bcreborn.energy.data;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.common.data.BCBlockStateHelper;
import com.peco2282.bcreborn.energy.BlocksEnergy;
import com.peco2282.bcreborn.energy.FluidsEnergy;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EnergyBlockStateProvider extends BCBlockStateHelper {
  public EnergyBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornEnergy.MODID, exFileHelper);
  }

  private static String getName(Block block) {
    return block.builtInRegistryHolder().key().location().getPath();
  }

  @Override
  protected void registerStatesAndModels() {

    // creative_engine / stone_engine / iron_engine（エネルギー）
    simpleEngine(BlocksEnergy.CREATIVE_ENGINE.get());
    simpleEngine(BlocksEnergy.STONE_ENGINE.get());
    simpleEngine(BlocksEnergy.IRON_ENGINE.get());


    liquidBlock(FluidsEnergy.OIL_BLOCK.get(), modLoc("block/fluids/oil_still"));
    liquidBlock(FluidsEnergy.FUEL_BLOCK.get(), modLoc("block/fluids/fuel_still"));

    basicItem(FluidsEnergy.OIL_BUCKET.get());
    basicItem(FluidsEnergy.FUEL_BUCKET.get());

    getItemBuilder("creative_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
    getItemBuilder("iron_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
    getItemBuilder("stone_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
  }

  private void liquidBlock(Block block, ResourceLocation model) {
    // 液体は液体モデルで描画する
    simpleBlock(block, new ModelFile.UncheckedModelFile(model));
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
  }
}
