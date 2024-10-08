package peco2282.bcreborn.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.fluid.BCCoreFluids;

import java.util.concurrent.CompletableFuture;

public class BCFluidTagsProvider extends FluidTagsProvider {
  public BCFluidTagsProvider(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_, @Nullable ExistingFileHelper existingFileHelper) {
    super(p_255941_, p_256600_, BCReborn.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider p_256366_) {
    tag(BCFluidTag.BURNABLE_SOURCE)
        .add(BCCoreFluids.OIL_SOURCE.get(), BCCoreFluids.FUEL_SOURCE.get());

    tag(BCFluidTag.BURNABLE_FLOWING)
        .add(BCCoreFluids.OIL_FLOWING.get(), BCCoreFluids.FUEL_FLOWING.get());

    tag(BCFluidTag.BURNABLE)
        .addTag(BCFluidTag.BURNABLE_SOURCE)
        .addTag(BCFluidTag.BURNABLE_FLOWING);
  }
}
