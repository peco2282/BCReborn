package peco2282.bcreborn.builder.item;

import peco2282.bcreborn.api.enums.EnumFillerType;
import peco2282.bcreborn.lib.item.BCBaseItem;

public class FillerTypePanel extends BCBaseItem {
  public FillerTypePanel(EnumFillerType type) {
    super(new Properties().stacksTo(1).durability(1), "filler_" + type.getSerializedName());
  }
}
