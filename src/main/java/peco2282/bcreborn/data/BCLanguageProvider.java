package peco2282.bcreborn.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import peco2282.bcreborn.BCReborn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


/**
 * This class is responsible for providing language translations for the mod.
 * It facilitates adding language keys and their respective translations for blocks, items, or general entries.
 *
 * @author peco2282
 */
public class BCLanguageProvider extends LanguageProvider {
  private static final Map<String, String> translations = new HashMap<>();

  /**
   * Constructs a new language provider for the specified locale.
   *
   * @param output The output where the language pack will be written.
   * @param locale The locale (e.g., "en_us") for which the translations will be generated.
   */
  public BCLanguageProvider(PackOutput output, String locale) {
    super(output, BCReborn.MODID, locale);
  }

  /**
   * Adds a translation entry to the language pack.
   *
   * @param key   The language key (e.g., "item.modid.item_name").
   * @param value The translation in the specified locale.
   */
  public static void put(String key, String value) {
    translations.put(key, value);
  }

  /**
   * Adds a translation entry for a block to the language pack.
   *
   * @param key   The supplier providing the block whose translation ID is used as the language key.
   * @param value The translation for the block.
   */
  public static void putBlock(Supplier<? extends Block> key, String value) {
    put(key.get().getDescriptionId(), value);
  }

  /**
   * Adds a translation entry for an item to the language pack.
   *
   * @param key   The supplier providing the item whose translation ID is used as the language key.
   * @param value The translation for the item.
   */
  public static void putItem(Supplier<? extends Item> key, String value) {
    put(key.get().getDescriptionId(), value);
  }

  /**
   * Adds all translations from the stored map to the language pack.
   * This method is called during the generation of the language pack to include the translations.
   */
  @Override
  protected void addTranslations() {
    translations.forEach(this::add);
  }
}
