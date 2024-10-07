package peco2282.bcreborn.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.registry.MenuTextureRegistry;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BCRegistryProvider extends DatapackBuiltinEntriesProvider {
  private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(MenuTextureRegistry.MENU_TEXTURE, MenuTextureRegistry::bootstrap);
  /**
   * Constructs a new datapack provider which generates all registry objects
   * from the provided mods using the holder. All entries that need to be
   * bootstrapped are provided within the {@link RegistrySetBuilder}.
   *
   * @param output          the target directory of the data generator
   * @param registries      a future of a lookup for registries and their objects
   */
  public BCRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, BUILDER, Set.of(BCReborn.MODID));
  }
}
