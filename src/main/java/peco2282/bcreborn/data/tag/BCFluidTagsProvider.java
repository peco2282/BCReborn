/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.fluid.BCCoreFluids;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides fluid tags for the BC Reborn mod. It extends {@link FluidTagsProvider} to
 * define custom tags for fluids.
 *
 * <p>Use this provider for generating and managing fluid tags during the datapack loading process.
 *
 * @author peco2282
 */
public class BCFluidTagsProvider extends FluidTagsProvider {
  /**
   * Constructs a new instance of {@code BCFluidTagsProvider}.
   *
   * @param p_255941_ The output target to save the tags.
   * @param p_256600_ The lookup provider for obtaining fluid holders.
   * @param existingFileHelper Helper for accessing existing resources.
   */
  public BCFluidTagsProvider(
      PackOutput p_255941_,
      CompletableFuture<HolderLookup.Provider> p_256600_,
      @Nullable ExistingFileHelper existingFileHelper) {
    super(p_255941_, p_256600_, BCReborn.MODID, existingFileHelper);
  }

  /**
   * Registers fluid tags for the mod.
   *
   * <p>This method overrides {@link FluidTagsProvider#addTags} to define custom tags for fluids.
   *
   * @param p_256366_ The provider used for looking up holders.
   */
  @Override
  protected void addTags(HolderLookup.Provider p_256366_) {
    tag(BCFluidTag.BURNABLE_SOURCE)
        .add(BCCoreFluids.OIL_SOURCE.get(), BCCoreFluids.FUEL_SOURCE.get());

    tag(BCFluidTag.BURNABLE_FLOWING)
        .add(BCCoreFluids.OIL_FLOWING.get(), BCCoreFluids.FUEL_FLOWING.get());

    tag(BCFluidTag.BURNABLE).addTag(BCFluidTag.BURNABLE_SOURCE).addTag(BCFluidTag.BURNABLE_FLOWING);
  }
}
