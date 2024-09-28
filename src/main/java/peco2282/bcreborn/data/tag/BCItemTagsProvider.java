package peco2282.bcreborn.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.core.item.BCCoreItems;

import java.util.concurrent.CompletableFuture;

public class BCItemTagsProvider extends ItemTagsProvider {
  public BCItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_) {
    super(p_275343_, p_275729_, p_275322_);
  }

  @Override
  protected void addTags(HolderLookup.Provider p_256380_) {
    tag(BCItemTag.GEAR)
        .add(BCCoreItems.GEAR_WOOD.get())
        .add(BCCoreItems.GEAR_STONE.get())
        .add(BCCoreItems.GEAR_IRON.get())
        .add(BCCoreItems.GEAR_GOLD.get())
        .add(BCCoreItems.GEAR_DIAMOND.get());
  }
}
