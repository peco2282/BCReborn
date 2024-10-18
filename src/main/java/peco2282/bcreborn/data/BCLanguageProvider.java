package peco2282.bcreborn.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import peco2282.bcreborn.BCReborn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BCLanguageProvider extends LanguageProvider {
  private static final Map<String, String> translations = new HashMap<>();

  public BCLanguageProvider(PackOutput output, String locale) {
    super(output, BCReborn.MODID, locale);
  }

  public static void put(String key, String value) {
    translations.put(key, value);
  }

  public static void putBlock(Supplier<Block> key, String value) {
    put(key.get().getDescriptionId(), value);
  }

  public static void putItem(Supplier<Item> key, String value) {
    put(key.get().getDescriptionId(), value);
  }

  @Override
  protected void addTranslations() {
    translations.forEach(this::add);
  }
}
