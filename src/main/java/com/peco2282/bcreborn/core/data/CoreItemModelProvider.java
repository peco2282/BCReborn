package com.peco2282.bcreborn.core.data;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.core.BlocksCore;
import com.peco2282.bcreborn.core.ItemsCore;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CoreItemModelProvider extends ItemModelProvider {
  public CoreItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, BCRebornCore.MODID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    basicItem(ItemsCore.WOODEN_GEAR.get());
    basicItem(ItemsCore.STONE_GEAR.get());
    basicItem(ItemsCore.IRON_GEAR.get());
    basicItem(ItemsCore.GOLD_GEAR.get());
    basicItem(ItemsCore.DIAMOND_GEAR.get());

    basicItem(ItemsCore.LIST.get());
    basicItem(ItemsCore.PAINTBRUSH.get());
    basicItem(ItemsCore.MAP_LOCATION.get());


    withExistingParent("wrench", mcLoc("item/generated"))
        .texture("layer0", modLoc("item/wrench_item"));
    getBuilder("wood_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
//    withExistingParent("wood_engine", mcLoc("builtin/entity"));
  }
}
