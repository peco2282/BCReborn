package com.peco2282.bcreborn.common.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.HashMap;
import java.util.Map;

public class BCLanguageProvider extends LanguageProvider {
  private static final Map<String, String> EN_US = new HashMap<>();

  public BCLanguageProvider(PackOutput output, String modid, String locale) {
    super(output, modid, locale);
  }

  @Override
  protected void addTranslations() {
    add("itemGroup.bcreborn", "BCReborn");
  }
}
