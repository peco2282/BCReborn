package peco2282.bcreborn.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import peco2282.bcreborn.BCReborn;

public class BCLanguageProvider extends LanguageProvider {
  public BCLanguageProvider(PackOutput output, String locale) {
    super(output, BCReborn.MODID, locale);
  }

  @Override
  protected void addTranslations() {
  }
}
