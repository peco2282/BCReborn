package peco2282.bcreborn.data;

import net.minecraft.advancements.AdvancementHolder;
import peco2282.bcreborn.annotation.LateinitField;
import peco2282.bcreborn.utils.OptionalWith;

public class Advancements {
  @LateinitField
  public static OptionalWith<AdvancementHolder> ROOT = OptionalWith.empty();

  @LateinitField
  public static OptionalWith<AdvancementHolder> BUILDER = OptionalWith.empty();
}
