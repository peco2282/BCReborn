package peco2282.bcreborn.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.block.BCCoreBlocks;

import java.util.concurrent.CompletableFuture;

public class BCBlockTagsProvider extends BlockTagsProvider {
  public BCBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, BCReborn.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider p_256380_) {
    tag(BCBlockTag.ENGINE)
        .add(BCCoreBlocks.WOOD_ENGINE.get())
        .add(BCCoreBlocks.STONE_ENGINE.get())
        .add(BCCoreBlocks.IRON_ENGINE.get())
        .add(BCCoreBlocks.CREATIVE_ENGINE.get());
  }
}
