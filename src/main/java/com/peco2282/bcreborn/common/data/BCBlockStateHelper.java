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

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

@SuppressWarnings("UnusedReturnValue")
public abstract class BCBlockStateHelper extends BlockStateProvider {
  private final ModelProvider<?> provider;

  public BCBlockStateHelper(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
    super(output, modid, exFileHelper);
    this.provider = models();
  }

  protected abstract void registerStatesAndModels();

  protected ItemModelBuilder basicItem(Item item) {
    return itemModels().basicItem(item);
  }

  protected BlockModelBuilder getBlockBuilder(String name) {
    return models().getBuilder(name);
  }

  protected ItemModelBuilder getItemBuilder(String name) {
    return itemModels().getBuilder(name);
  }

  protected ModelFile.ExistingModelFile getExistingFile(ResourceLocation name) {
    return provider.getExistingFile(name);
  }

  protected ItemModelBuilder withExistingItemParent(String name, ResourceLocation parent) {
    return itemModels().withExistingParent(name, parent);
  }
}
