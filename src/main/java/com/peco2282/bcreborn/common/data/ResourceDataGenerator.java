package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.BCRebornEnergy;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ResourceDataGenerator extends DatapackBuiltinEntriesProvider {
  private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();

  /**
   * Constructs a new datapack provider which generates all registry objects
   * from the provided mods using the holder.
   *
   * @param output     the target directory of the data generator
   * @param registries a future of a lookup for registries and their objects
   */
  public ResourceDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, BUILDER, Set.of(
        BCRebornCore.MODID,
        BCRebornBuilders.MODID,
        BCRebornEnergy.MODID
    ));
  }
}
