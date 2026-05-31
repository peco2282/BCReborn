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
import com.peco2282.bcreborn.energy.FluidsEnergy;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EnergyItemModelProvider extends ItemModelProvider {
  public EnergyItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, BCRebornEnergy.MODID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    basicItem(FluidsEnergy.OIL_BUCKET.get());
    basicItem(FluidsEnergy.FUEL_BUCKET.get());

    getBuilder("creative_engine")
        .parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")))
        ;
    getBuilder("iron_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
    getBuilder("stone_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
  }
}
