package peco2282.bcreborn.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.item.BCCoreItems;
import peco2282.bcreborn.lib.item.BCLibItems;

public class BCLanguageProvider extends LanguageProvider {
  public BCLanguageProvider(PackOutput output, String locale) {
    super(output, BCReborn.MODID, locale);
  }

  @Override
  protected void addTranslations() {
    BCLibItems.getLanguages().forEach(this::add);

    BCCoreBlocks.getLanguage().forEach(this::add);
    BCCoreItems.getLanguage().forEach(this::add);
  }
}
