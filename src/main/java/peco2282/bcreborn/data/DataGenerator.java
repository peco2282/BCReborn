package peco2282.bcreborn.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import peco2282.bcreborn.data.tag.BCBlockTagsProvider;
import peco2282.bcreborn.data.tag.BCFluidTagsProvider;
import peco2282.bcreborn.data.tag.BCItemTagsProvider;

import java.util.concurrent.CompletableFuture;


/**
 * The DataGenerator class is responsible for handling the Minecraft data generation process.
 * It listens for the {@link GatherDataEvent} and registers various data providers for blocks,
 * items, fluids, advancements, languages, recipes, models, and registry entries.
 *
 * @author peco2282
 */
public class DataGenerator {
  /**
   * Handles the {@link GatherDataEvent}, using it to register data providers for the generation of
   * data such as block tags, item tags, fluid tags, advancements, item models, recipes, language files, 
   * and registry entries.
   *
   * @param event the data gathering event that provides the necessary information and tools
   *              for registering data providers.
   */
  @SubscribeEvent
  public static void onGatherData(GatherDataEvent event) {
    final ExistingFileHelper helper = event.getExistingFileHelper();
    final net.minecraft.data.DataGenerator generator = event.getGenerator();
    final PackOutput output = generator.getPackOutput();
    final CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

    var blockTags = new BCBlockTagsProvider(output, provider, helper);
    generator.addProvider(true, blockTags);
    generator.addProvider(true, new BCFluidTagsProvider(output, provider, helper));
    generator.addProvider(true, new BCItemTagsProvider(output, provider, blockTags.contentsGetter()));
    generator.addProvider(true, new BCAdvancementProvider(output, provider, helper));
    generator.addProvider(true, new BCItemModelProvider(output, provider, helper));
    generator.addProvider(true, new BCRecipeProvider(output, provider));
    generator.addProvider(true, new BCLanguageProvider(output, "en_us"));
    generator.addProvider(true, new BCRegistryProvider(output, provider));
  }
}
