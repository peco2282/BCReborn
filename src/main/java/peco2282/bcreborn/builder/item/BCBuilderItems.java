package peco2282.bcreborn.builder.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.enums.EnumFillerType;
import peco2282.bcreborn.api.item.BCItem;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCBuilderItems {
  public static final RegistryObject<FillerTypePanel> BOX = register("filler_box", () -> new FillerTypePanel(EnumFillerType.BOX));
  public static final RegistryObject<FillerTypePanel> CLEAR = register("filler_clear", () -> new FillerTypePanel(EnumFillerType.CLEAR));
  public static final RegistryObject<FillerTypePanel> CYLINDER = register("filler_cylinder", () -> new FillerTypePanel(EnumFillerType.CYLINDER));
  public static final RegistryObject<FillerTypePanel> FILL = register("filler_fill", () -> new FillerTypePanel(EnumFillerType.FILL));
  public static final RegistryObject<FillerTypePanel> FLATTEN = register("filler_flatten", () -> new FillerTypePanel(EnumFillerType.FLATTEN));
  public static final RegistryObject<FillerTypePanel> HORIZONRAL = register("filler_horizontal", () -> new FillerTypePanel(EnumFillerType.HORIZONTAL));
  public static final RegistryObject<FillerTypePanel> NONE = register("filler_none", () -> new FillerTypePanel(EnumFillerType.NONE));
  public static final RegistryObject<FillerTypePanel> PYRAMID = register("filler_pyramid", () -> new FillerTypePanel(EnumFillerType.PYRAMID));
  public static final RegistryObject<FillerTypePanel> STAIR = register("filler_stair", () -> new FillerTypePanel(EnumFillerType.STAIR));
  private static <I extends Item & BCItem> RegistryObject<I> register(String name, Supplier<I> item) {
    return BCRegistry.registerItem(name, item);
  }

  public static void init() {}
}
